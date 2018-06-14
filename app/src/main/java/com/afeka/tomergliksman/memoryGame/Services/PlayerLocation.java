package com.afeka.tomergliksman.memoryGame.Services;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class PlayerLocation implements LocationListener {
    private Location currentLocation;
    private boolean didAlreadyRequestLocationPermission;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private LocationManager locationManager;

    public PlayerLocation(Context context) {
        didAlreadyRequestLocationPermission = false;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        getCurrentLocation(context);
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    // get current location by permission
    private void getCurrentLocation(Context context) {
        boolean isAccessGranted;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
            String coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION;
            if (context.checkSelfPermission(fineLocationPermission) != PackageManager.PERMISSION_GRANTED ||
                    context.checkSelfPermission(coarseLocationPermission) != PackageManager.PERMISSION_GRANTED) {
                // The user blocked the location services of THIS app / not yet approved
                isAccessGranted = false;
                if (!didAlreadyRequestLocationPermission) {
                    didAlreadyRequestLocationPermission = true;
                    String[] permissionsToAsk = new String[]{fineLocationPermission, coarseLocationPermission};
                    ((FragmentActivity) context).requestPermissions(permissionsToAsk, LOCATION_PERMISSION_REQUEST_CODE);
                }
            } else {
                isAccessGranted = true;
            }



            if (isAccessGranted) {
                float metersToUpdate = 1;
                long intervalMilliseconds = 1000;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, intervalMilliseconds, metersToUpdate, this);
                if (currentLocation == null) {

                    currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);


                }

            }
        }
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void removeUpdates() {
        locationManager.removeUpdates(this);
    }
}
