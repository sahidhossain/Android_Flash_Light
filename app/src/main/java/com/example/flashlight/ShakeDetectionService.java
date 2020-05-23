package com.example.flashlight;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.os.IBinder;
import android.os.Vibrator;

import androidx.annotation.Nullable;

public class ShakeDetectionService extends Service implements SensorEventListener {
    public final int MIN_TIME_BETWEEN_SHAKE = 1000;
    SensorManager sensorManager = null;

    Vibrator vibrator = null;
    private long lastShakeTime = 0;
    private boolean isFlashLightOn = false;
    private Float shakeThrashold = 10.0f;
    Utilities utilities;

    public ShakeDetectionService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        utilities = new Utilities(this);
        if (sensorManager != null) {
            Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, sensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            if ((curTime - lastShakeTime) > MIN_TIME_BETWEEN_SHAKE) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                double accelaration = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2) - SensorManager.GRAVITY_EARTH);

                if (accelaration > shakeThrashold) {
                    lastShakeTime = curTime;
                    if (!isFlashLightOn) {
                        try {
                            isFlashLightOn = utilities.torchToggle("on");
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            isFlashLightOn = utilities.torchToggle("off");
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
