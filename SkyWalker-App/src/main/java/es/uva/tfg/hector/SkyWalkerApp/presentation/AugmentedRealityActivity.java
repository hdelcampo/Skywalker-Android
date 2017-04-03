package es.uva.tfg.hector.SkyWalkerApp.presentation;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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
