package es.uva.tfg.hector.tfg;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

    /**
     * The orientation's sensor.
     */
    private OrientationSensor orientationSensor;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orientationSensor = OrientationSensor.createSensor(this);
        setContentView(R.layout.activity_main);
        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.cameraContainer, CameraFragment.newInstance())
                    .commit();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onResume() {
        super.onResume();
        orientationSensor.registerEvents();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onPause() {
        super.onPause();
        orientationSensor.unregisterEvents();
    }

    public void updateDegrees(){
        TextView degreeText = (TextView)findViewById(R.id.zRotation);
        degreeText.setText("Azimuth " + String.valueOf(orientationSensor.getAzimuth()));

        degreeText = (TextView)findViewById(R.id.yRotation);
        degreeText.setText("Roll " + String.valueOf(orientationSensor.getRoll()));

        degreeText = (TextView)findViewById(R.id.xRotation);
        degreeText.setText("Pitch " + String.valueOf(orientationSensor.getPitch()));
    }

}  