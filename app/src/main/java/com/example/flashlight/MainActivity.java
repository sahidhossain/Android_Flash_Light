package com.example.flashlight;

import android.hardware.camera2.CameraAccessException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements AccelerometerListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Toast.makeText(getBaseContext(), "App is on Resume", Toast.LENGTH_LONG).show();

        if (Utilities.isSupported(this)) {
            Utilities.startListening(this);
        }
    }

    public void toggle_LED(View v) throws CameraAccessException {
        Button button = (Button) v;

        if (button.getText().toString().equals("Switch On")) {
            button.setText("Switch Off");
            Utilities.torchToggle("on");
        } else {
            button.setText("Switch On");
            Utilities.torchToggle("off");
        }
    }

    @Override
    public void onAccelerationChanged(float x, float y, float z) {

    }

    private boolean isFlashOn = false;

    @Override
    public void onShake(float force) {
        Button button = findViewById(R.id.switchButton);
        if (!isFlashOn) {
            try {
                button.setText("Switch Off");
                isFlashOn = Utilities.torchToggle("on");
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            try {
                button.setText("Switch On");
                isFlashOn = Utilities.torchToggle("off");
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
