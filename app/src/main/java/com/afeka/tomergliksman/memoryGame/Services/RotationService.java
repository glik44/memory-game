package com.afeka.tomergliksman.memoryGame.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.List;

public class RotationService extends Service implements SensorEventListener{


    public static final String SENSOR_SERVICE_BROADCAST_ACTION = "SENSOR_SERVICE_BROADCAST_ACTION";
    public static final String SENSOR_SERVICE_VALUES_KEY = "SENSOR_SERVICE_VALUES_KEY";

    protected SensorServiceBinder sensorServiceBinder = new SensorServiceBinder();// An IBinder implementation subclass
    private SensorManager sensorManager;
    boolean isListening = false;
    protected HandlerThread sensorThread;
    private Handler sensorHandler;
    private boolean hasDefaultVec = false;
    private boolean isValid = true;
    private float firstPitch;
    private float firstRoll;
    private float firstAzimut;
    private float[] currentOrientaion = new float[3];
    private static final String TAG = RotationService.class.getSimpleName();

    private float x;
    private float y;
    private float z;

    @Override
    public void onCreate() {
        super.onCreate();

        sensorThread = new HandlerThread(RotationService.class.getSimpleName());
        sensorThread.start();
        sensorHandler = new Handler(sensorThread.getLooper());

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sensorServiceBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
            sensorManager = null;
            isListening = false;
        }

        return super.onUnbind(intent);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR && event.accuracy > 2) {
            if (event.values.length > 4) {
                float[] truncatedRotationVector = new float[4];
                System.arraycopy(event.values, 0, truncatedRotationVector, 0, 4);
                currentOrientaion = getOrientaionFromEvent(truncatedRotationVector);
            } else {
                currentOrientaion = getOrientaionFromEvent(event.values);
            }
            if (!hasDefaultVec) {
                setDefaultVec();
            }
            boolean tempRotation = checkValidtyRotaionAngle();
            if (tempRotation != isValid) {
                Log.d("motion Handler", "Detection");
                isValid = tempRotation;
                notifyEvaluation(tempRotation);
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private boolean checkValidtyRotaionAngle() {
        float azimuth = currentOrientaion[0];
        float pitch = currentOrientaion[1];
        float roll = currentOrientaion[2];
        if (hasDefaultVec) {
            if (Math.abs(firstPitch - pitch) > 0.4
                    || Math.abs(firstRoll - roll) > 1.5
                    || Math.abs(firstAzimut - azimuth) > 1.5) {

                return false;
            }
        }
        return true;

    }

    private float[] getOrientaionFromEvent(float[] eventValues) {
        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, eventValues);
        int worldAxisX = SensorManager.AXIS_X;
        int worldAxisZ = SensorManager.AXIS_Z;
        float[] adjustedRotationMatrix = new float[9];
        SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisX, worldAxisZ, adjustedRotationMatrix);
        float[] orientation = new float[3];
        SensorManager.getOrientation(adjustedRotationMatrix, orientation);

        return orientation;
    }

    private void notifyEvaluation(boolean detect) {
        Intent intent = new Intent(SENSOR_SERVICE_BROADCAST_ACTION);

        intent.putExtra(SENSOR_SERVICE_VALUES_KEY, detect);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void setDefaultVec() {
        if (!hasDefaultVec) {
            this.firstAzimut = currentOrientaion[0];
            this.firstPitch = currentOrientaion[1];
            this.firstRoll = currentOrientaion[2];
            hasDefaultVec = true;
        }
    }

    public String getTag() {
        return TAG;
    }

    public class SensorServiceBinder extends Binder {
        public static final String START_LISTENING = "Start";


       public RotationService getService() {
            return RotationService.this;
        }

        public void notifyService(String msg) {
            // A.D: "you must provide an interface that clients use to communicate with the service, by returning an IBinder."
            Log.v(getTag(), RotationService.class.getSimpleName() + " has got a message from its binding activity. Message: " + msg);

            if (msg == SensorServiceBinder.START_LISTENING && !isListening) { // Why can we use this instead of equals?
                List<Sensor> sensorList= sensorManager.getSensorList(Sensor.TYPE_ALL);
                Log.v(getTag(), "Available sensors: " + sensorList);
                Sensor sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);//TYPE_ROTATION_VECTOR); // Sensor.TYPE_GYROSCOPE will be null in Genymotion free edition
                if (sensor == null && sensorList.size() > 0) {
                    // Backup
                    //sensor = sensorList.get(0); // for Genymotion sensors (Genymotion Accelerometer in my case)
                }

                if (sensor == null) return;

                sensorManager.registerListener(getService(), sensor, SensorManager.SENSOR_DELAY_UI, sensorHandler);
                isListening = true;
            }
        }
    }
}
