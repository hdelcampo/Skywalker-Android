package es.uva.tfg.hector.SkyWalkerApp.presentation;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import es.uva.tfg.hector.SkyWalkerApp.R;
import es.uva.tfg.hector.SkyWalkerApp.business.PointOfInterest;

/**
 * Augmented reality activity
 * @author Héctor Del Campo Pando
 */
public class AugmentedRealityActivity extends FragmentActivity
        implements AugmentedRealityControlsFragment.AugmentedRealityControls {

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
    public List<PointOfInterest> getActivePoints () {
        return ((AugmentedRealityFragment) getFragmentManager().findFragmentById(R.id.augmented_reality_fragment)).getActivePoints();
    }

    @Override
    public void setActivePoints (List<PointOfInterest> points) {
        ((AugmentedRealityFragment) getFragmentManager().findFragmentById(R.id.augmented_reality_fragment)).setActivePoints(points);
    }

}
