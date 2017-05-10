package es.uva.tfg.hector.SkyWalkerApp.presentation;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

import java.util.ArrayList;
import java.util.List;

import es.uva.tfg.hector.SkyWalkerApp.R;
import es.uva.tfg.hector.SkyWalkerApp.business.PointOfInterest;
import es.uva.tfg.hector.SkyWalkerApp.business.User;

/**
 * Fragment for Augmented Reality controls.
 * @author Hector Del Campo Pando
 */
public class AugmentedRealityControlsFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    /**
     * Constants for new intents
     */
    private static final int FILTER_POINTS = 1;

    /**
     * Fragment to control interface
     */
    private AugmentedRealityActivity controls;

    /**
     * Indicates whether this is the first time this fragment is instantiated or not.
     */
    private static boolean firstTime = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.augmented_reality_controls_layout, container, false);

        rootView.findViewById(R.id.filter_button).setOnClickListener(this);
        rootView.findViewById(R.id.logout_button).setOnClickListener(this);
        rootView.findViewById(R.id.debug_info_layout).setOnClickListener(this);

        ((Switch)rootView.findViewById(R.id.switch_debug_info)).setOnCheckedChangeListener(this);

        ((TextView)rootView.findViewById(R.id.real_server_label)).setText(User.getInstance().getToken().getURL());

        setBuildStamp(rootView);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!firstTime) {
            return;
        }

        TapTargetView.showFor(getActivity(),
                TapTarget.forView(getView().findViewById(R.id.navigation_drawer), getString(R.string.controls_discovery))
                .outerCircleColor(R.color.colorAccent)
                .textColor(android.R.color.white),
                new TapTargetView.Listener() {

                    @Override
                    public void onOuterCircleClick(TapTargetView view) {
                        super.onOuterCircleClick(view);
                        view.dismiss(true);
                    }

                    @Override
                    public void onTargetDismissed(TapTargetView view, boolean userInitiated) {
                        super.onTargetDismissed(view, userInitiated);
                        if (null != getView()) {
                            ((DrawerLayout) getView()).openDrawer(Gravity.START, true);
                        }
                    }
                });

        firstTime = false;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (Activity.RESULT_OK == resultCode) {
            if (FILTER_POINTS == requestCode) {
                List<PointOfInterest> selecteds =
                        data.getParcelableArrayListExtra(FilterActivity.POINTS_TO_SHOW);
                controls.setActivePoints(selecteds);
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.filter_button:
                filter();
                break;
            case R.id.logout_button:
                logout();
                break;
            case R.id.debug_info_layout:
                ((DrawerLayout) getView()).openDrawer(Gravity.START, true);
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof AugmentedRealityControls) {
            controls = (AugmentedRealityActivity) context;
        }
    }

    /**
     * Shows an UI to select points to be shown.
     */
    private void filter () {
        Intent dialog = new Intent(getContext(), FilterActivity.class);

        ArrayList<PointOfInterest> allPoints = (ArrayList) PointOfInterest.getPoints();
        dialog.putParcelableArrayListExtra(FilterActivity.ALL_POINTS_EXTRA, allPoints);

        ArrayList<PointOfInterest> usedPoints = (ArrayList) controls.getActivePoints();
        dialog.putParcelableArrayListExtra(FilterActivity.USED_POINTS_EXTRA, usedPoints);

        startActivityForResult(dialog, FILTER_POINTS);
    }

    /**
     * Shows an UI to start a new server connection, also logouts from server.
     */
    private void logout() {
        Intent dialog = new Intent(getContext(), NewConnectionActivity.class);
        startActivity(dialog);
        getActivity().finish();
    }

    /**
     * Sets a build time stamp to the UI.
     */
    private void setBuildStamp(View rootView) {
        String s = "";
        try{
            s +=  "Version " + getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionName;
            s +=  "\nBuild " + getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionCode;
        }catch(Exception e){
            s = "Couldn't get version info";
        }

        ((TextView)rootView.findViewById(R.id.compilationStamp)).setText(s);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switch_debug_info:
                View layout = getView().findViewById(R.id.debug_info_layout);
                if (!isChecked) {
                    //layout.setVisibility(View.VISIBLE);
                    layout.setAlpha(1);
                } else {
                    //layout.setVisibility(View.INVISIBLE);
                    layout.setAlpha(0);
                }
        }
    }

    /**
     * Interface an Augmented Reality that must be controlled must implement.
     */
    public interface AugmentedRealityControls {

        /**
         * Retrieves points being shown.
         * @return a list of the points being shown.
         */
        List<PointOfInterest> getActivePoints();

        /**
         * Sets the points to show.
         */
        void setActivePoints (List<PointOfInterest> points);
    }

}
