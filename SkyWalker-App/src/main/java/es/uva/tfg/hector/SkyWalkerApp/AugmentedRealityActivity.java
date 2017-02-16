package es.uva.tfg.hector.SkyWalkerApp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Augmented reality activity
 * @author Héctor Del Campo Pando
 */
public class AugmentedRealityActivity extends FragmentActivity
        implements AugmentedRealityControlsFragment.AugmentedRealityControls {

    /**
     * Permissions IDs.
     */
    private static final int CAMERA_PERMISSION = 0;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.augmented_reality_activity_layout);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION);
        }

        setBuildStamp();

    }

    /**
     * Sets a build time stamp to the UI.
     */
    private void setBuildStamp() {
        String s = "";
        try{
            s +=  " (Build: " + getPackageManager().getPackageInfo(getPackageName(), 0).versionCode +") ";
        }catch(Exception e){
            s = "Couldn't get build";
        }

        ((TextView)findViewById(R.id.compilationStamp)).setText(s);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION: {
                if (grantResults.length == 0
                        || grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, getString(R.string.needs_camera_permissions), Toast.LENGTH_LONG).show();
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.CAMERA},
                            CAMERA_PERMISSION);
                }
            }
        }
    }

    @Override
    public List<PointOfInterest> getActivePoints () {
        return ((AugmentedRealityFragment) getFragmentManager().findFragmentById(R.id.augmented_reality_fragment)).getActivePoints();
    }

    @Override
    public void setActivePoints (List<PointOfInterest> points) {
        ((AugmentedRealityFragment) getFragmentManager().findFragmentById(R.id.augmented_reality_fragment)).setActivePoints(points);
    }

}
