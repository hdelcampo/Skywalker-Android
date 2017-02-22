package es.uva.tfg.hector.SkyWalkerApp;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for Augmented Reality controls.
 * @author Hector Del Campo Pando
 */
public class AugmentedRealityControlsFragment extends Fragment implements View.OnClickListener {

    /**
     * Constants for new intents
     */
    private static final int FILTER_POINTS = 1;

    /**
     * Fragment to control interface
     */
    private AugmentedRealityActivity controls;

    public AugmentedRealityControlsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.augmented_reality_controls_layout, container, false);

        rootView.findViewById(R.id.filter_button).setOnClickListener(this);
        rootView.findViewById(R.id.connection_button).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (getActivity().RESULT_OK == resultCode) {
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
            case R.id.connection_button:
                newConnection();
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
     * Shows an UI to start a new server connection.
     */
    private void newConnection () {
        Intent dialog = new Intent(getContext(), NewConnectionActivity.class);
        startActivity(dialog);
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
