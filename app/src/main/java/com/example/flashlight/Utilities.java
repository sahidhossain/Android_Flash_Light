package com.example.flashlight;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.widget.Toast;

import java.util.List;

public class Utilities {
    private static float threshold = 15.0f;
    private static int interval = 200;
    private static Sensor sensor;
    private static SensorManager sensorManager;
    private static AccelerometerListener listener;
    private static Boolean supported;
    private static boolean running = false;
    private static Context aContext;
    public static boolean isSwitchOn = false;

    public Utilities(Context context) {
        this.aContext = context;
    }

    public static boolean torchToggle(String command) throws CameraAccessException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CameraManager cameraManager = (CameraManager) aContext.getSystemService(Context.CAMERA_SERVICE);
            String CameraId = null;

            if (cameraManager != null) {
                CameraId = cameraManager.getCameraIdList()[0];
            }
            if (cameraManager != null) {
                if (command.equals("on")) {
                    cameraManager.setTorchMode(CameraId, true);
                    isSwitchOn = true;
                } else {
                    cameraManager.setTorchMode(CameraId, false);
                    isSwitchOn = false;
                }
            }

        }
        return isSwitchOn;
    }


    public static boolean isSupported(Context context) {
        aContext = context;
        if (supported == null) {
            if (aContext != null) {
                sensorManager = (SensorManager) aContext.getSystemService(Context.SENSOR_SERVICE);
                List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
                supported = new Boolean(sensors.size() > 0);
//                Toast.makeText(context, sensors.toString(), Toast.LENGTH_LONG).show();
            } else {
                supported = Boolean.FALSE;
            }
        }
        return supported;
    }


    public static void startListening(AccelerometerListener accelerometerListener) {
        sensorManager = (SensorManager) aContext.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensors.size() > 0) {
            sensor = sensors.get(0);
            running = sensorManager.registerListener(sensorEventListener, sensor, SensorManager.SENSOR_DELAY_GAME);
            listener = accelerometerListener;
        }
    }

    private static SensorEventListener sensorEventListener = new SensorEventListener() {

        private long now = 0;
        private long timeDiff = 0;
        private long lastUpdate = 0;
        private long lastShake = 0;
        private float x = 0;
        private float y = 0;
        private float z = 0;
        private float lastX = 0;
        private float lastY = 0;
        private float lastZ = 0;
        private float force = 0;

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public void onSensorChanged(SensorEvent event) {
            now = event.timestamp;
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];
            if (lastUpdate == 0) {
                lastUpdate = now;
                lastShake = now;
                lastX = x;
                lastY = y;
                lastZ = z;
                Toast.makeText(aContext, "No Motion detected", Toast.LENGTH_SHORT).show();
            } else {
                timeDiff = now - lastUpdate;
                if (timeDiff > 0) {
                    /*force = Math.abs(x + y + z - lastX - lastY - lastZ)  / timeDiff;*/
                    force = Math.abs(x + y + z - lastX - lastY - lastZ);
                    if (Float.compare(force, threshold) > 0) {
//                        Toast.makeText(aContext, (now - lastShake) + "  >= " + interval, Toast.LENGTH_LONG).show();
                        if (now - lastShake >= interval) {
                            listener.onShake(force);
                        } else {
//                            Toast.makeText(aContext, "No Motion detected", Toast.LENGTH_SHORT).show();
                        }
                        lastShake = now;
                    }
                    lastX = x;
                    lastY = y;
                    lastZ = z;
                    lastUpdate = now;
                } else {
//                    Toast.makeText(aContext, "No Motion detected", Toast.LENGTH_SHORT).show();
                }
            }
            listener.onAccelerationChanged(x, y, z);
        }
    };
}
