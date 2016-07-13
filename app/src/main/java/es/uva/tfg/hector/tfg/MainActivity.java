package es.uva.tfg.hector.tfg;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Surface;
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
        orientationSensor = OrientationSensor.createSensor(this, getWindowManager().getDefaultDisplay());
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

    public void updateSlider(){
        if(getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_0 ||
                getWindowManager().getDefaultDisplay().getRotation() == Surface.ROTATION_180) {
            updateSliderPortrait();
        } else {
            updateSliderLandscape();
        }
    }

    private void updateSliderPortrait() {
        View column = (View)findViewById(R.id.column);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;

        float azimuth = orientationSensor.getAzimuth(),
                pitch = orientationSensor.getPitch(),
                roll = orientationSensor.getRoll();

        float fov = ((CameraFragment) (getFragmentManager().findFragmentById(R.id.cameraContainer))).getFOVHeight();

        int pos = width/2 - (int)(azimuth * (width/fov));
        if(azimuth >= fov/2) {
            column.setX(0);
        } else if(azimuth <= -fov/2){
            column.setX(width - 10);
        } else {
            column.setX(pos);
        }
    }

    private void updateSliderLandscape(){
        View column = (View)findViewById(R.id.column);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        Resources resources = getApplicationContext().getResources();
        int resourceId = resources.getIdentifier("navigation_bar_width", "dimen", "android");
        if (resourceId > 0) {
            width += resources.getDimensionPixelSize(resourceId);
        }

        float azimuth = orientationSensor.getAzimuth(),
                pitch = orientationSensor.getPitch(),
                roll = orientationSensor.getRoll();

        float fov = ((CameraFragment) (getFragmentManager().findFragmentById(R.id.cameraContainer))).getFOVWidth();

        int pos = width/2 - (int)(azimuth * (width/fov));
        if(azimuth >= fov/2) {
            column.setX(0);
        } else if(azimuth <= -fov/2){
            column.setX(width - 10);
        } else {
            column.setX(pos);
        }
    }
}  