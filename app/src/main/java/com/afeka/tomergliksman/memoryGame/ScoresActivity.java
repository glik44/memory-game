package com.afeka.tomergliksman.memoryGame;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.afeka.tomergliksman.memoryGame.Services.PlayerLocation;
import com.afeka.tomergliksman.memoryGame.classes.Score;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoresActivity extends FragmentActivity {

    private TableLayout tl;
    private PlayerLocation myCurrentLocation;
    private GoogleMap map;
    final static int TABLE_SIZE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);

        //get scoreTable
        //scoreTable = MainActivity.highScoreTable.getScoreTable();
        myCurrentLocation = new PlayerLocation(this);

        if (isGoogleMapsInstalled()) {
            MapFragment mapFragment = (MapFragment) getFragmentManager()
                    .findFragmentById(R.id.map);

            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    setGoogleMap(googleMap);
                    showPinsOnMap();
                }
            });
        } else {
            // Notify the user he should install GoogleMaps (after installing Google Play Services)
            FrameLayout mapsPlaceHolder = (FrameLayout) findViewById(R.id.mapPlaceHolder);
            TextView errorMessageTextView = new TextView(getApplicationContext());
            errorMessageTextView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
            errorMessageTextView.setText("missing_google_maps_error_message");
            errorMessageTextView.setTextColor(Color.RED);
            mapsPlaceHolder.addView(errorMessageTextView);
        }


        //show score table

        tl = (TableLayout) findViewById(R.id.high_score_table);
        //tl.removeAllViews();
        // map.clear();
        showTable(MainActivity.scoreTable.getScoreTable());
        showPinsOnMap();
    }

    private void showTable(ArrayList<Score> scoreTable) {

        int rankVal = 1;
        Typeface face;

        // view for headline
        TableRow headlines = new TableRow(this);

        //rank
        TextView rankHeadline = new TextView(this);
        rankHeadline.setText("Rank");

        //time
        TextView scoreHeadline = new TextView(this);
        scoreHeadline.setText("Score");

        //name
        TextView nameHeadline = new TextView(this);
        nameHeadline.setText("Name");

        rankHeadline.setTextSize(25);
        scoreHeadline.setTextSize(25);
        nameHeadline.setTextSize(25);
        rankHeadline.setPadding(0, 2, 70, 2);
        scoreHeadline.setPadding(0, 2, 70, 2);


        rankHeadline.setGravity(Gravity.CENTER);
        scoreHeadline.setGravity(Gravity.CENTER);
        nameHeadline.setGravity(Gravity.CENTER);

        rankHeadline.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        scoreHeadline.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        nameHeadline.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

        //add the headlines to the view
        headlines.addView(rankHeadline);
        headlines.addView(scoreHeadline);
        headlines.addView(nameHeadline);
        tl.addView(headlines, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

        //sort table
        Collections.sort(scoreTable);
        Collections.reverse(scoreTable);

        //create the table
        for (Score e : scoreTable) {

            if (rankVal <= TABLE_SIZE) {
                TableRow tr = new TableRow(this);
                TextView rank = new TextView(this);
                TextView score = new TextView(this);
                TextView name = new TextView(this);

                // set row text
                rank.setText((rankVal++) + " ");
                name.setText(e.getPlayerName());
                score.setText(e.getScore()+" ");

                rank.setGravity(Gravity.CENTER);
                score.setGravity(Gravity.CENTER);
                name.setGravity(Gravity.CENTER);

                rank.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                score.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
                name.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);

                rank.setTextSize(20);
                score.setTextSize(20);
                name.setTextSize(20);
                rank.setPadding(0, 2, 70, 2);
                score.setPadding(0, 2, 70, 2);

                // add row to view
                tr.addView(rank);
                tr.addView(score);
                tr.addView(name);
                tl.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        myCurrentLocation.removeUpdates();
    }

    private void showPinsOnMap() {
        try {
            int rankVal = 1;
            LatLng current;
            for (Score e : MainActivity.scoreTable.getScoreTable()) {
                /* Create a new row to be added. */
                if (rankVal <=TABLE_SIZE) {
                    if (e.getPlayerLocation() != null) {
                        current = e.getPlayerLocation();
                        Location temp = new Location(LocationManager.GPS_PROVIDER);
                        temp.setLatitude(e.getPlayerLocation().latitude);
                        temp.setLongitude(e.getPlayerLocation().longitude);
                        map.addMarker(new MarkerOptions()
                                .title("Rank: " + rankVal + " score: " + e.getScore() + " Name: " + e.getPlayerName())
                                .snippet(getStreetName(temp))
                                .position(current)).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.trophy));
                    }
                    rankVal++;
                }
            }
        } catch (NullPointerException e) {
            return;
        }
    }

    public boolean isGoogleMapsInstalled() {
        try {
            ApplicationInfo info = getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.map = googleMap;
        boolean isAllowedToUseLocation = hasPermissionForLocationServices();
        if (isAllowedToUseLocation) {
            try {
                googleMap.setMyLocationEnabled(true);
                LatLng current = new LatLng(myCurrentLocation.getCurrentLocation().getLatitude(), myCurrentLocation.getCurrentLocation().getLongitude());
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(current, 15f));

            } catch (SecurityException exception) {
                Toast.makeText(this, "Error getting location", Toast.LENGTH_LONG).show();
            } catch (NullPointerException exception) {
                Toast.makeText(this, "Error getting location", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(this, "Location is blocked in this app", Toast.LENGTH_LONG).show();
        }
    }

    public boolean hasPermissionForLocationServices() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Because the user's permissions started only from Android M and on...
            return true;
        }

        if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // The user blocked the location services of THIS app
            return false;
        }

        return true;
    }

    private String getStreetName(Location location) {
        Geocoder geoCoder = new Geocoder(this);
        List<Address> matches = null;
        try {
            matches = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            Address bestMatch = (matches.isEmpty() ? null : matches.get(0));
            return bestMatch.getAddressLine(0);
        } catch (IOException e) {
            return "Cant Find Street Name";
        } catch (NullPointerException e) {
            return "Cant Find Street Name";
        }
    }
}
