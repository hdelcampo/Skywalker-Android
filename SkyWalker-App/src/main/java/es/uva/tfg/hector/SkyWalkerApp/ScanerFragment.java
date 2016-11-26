package es.uva.tfg.hector.SkyWalkerApp;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Hector Del Campo Pando on 06/07/2016.
 */
public class ScanerFragment extends Fragment {

    /**
     * Constants for activitys
     */
    private static final int FILTER_POINTS = 1;

    /**
     * Camera's preview.
     */
    private CameraPreview cameraPreview;

    /**
     * Overlay where objects will be displayed.
     */
    private OverlayView overlayView;

    /**
     * Retrieves a new {@link ScanerFragment}.
     * @return the new {@link ScanerFragment}.
     */
    public static ScanerFragment newInstance(){
        return new ScanerFragment();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        cameraPreview = new CameraPreview((TextureView)getActivity().findViewById(R.id.cameraView), getActivity());
        overlayView = new OverlayView((TextureView)getActivity().findViewById(R.id.overlayView), getActivity(), cameraPreview.getCamera());
        ImageButton filter = (ImageButton) getActivity().findViewById(R.id.filterButton);
        filter.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent dialog = new Intent(getActivity(), FilterActivity.class);

                ArrayList<PointOfInterest> allPoints = (ArrayList) PointOfInterest.getPoints();
                dialog.putParcelableArrayListExtra("allPoints", allPoints);

                ArrayList<PointOfInterest> usedPoints = (ArrayList) overlayView.getActivePoints();
                dialog.putParcelableArrayListExtra("usedPoints", usedPoints);

                startActivityForResult(dialog, FILTER_POINTS);
            }
        });

        ImageButton connection = (ImageButton) getActivity().findViewById(R.id.connectionButton);
        connection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent dialog = new Intent(getActivity(), ConnectionActivity.class);
                startActivity(dialog);
            }
        });
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode) {
            if (FILTER_POINTS == requestCode) {
                ArrayList<PointOfInterest> selected = data.getParcelableArrayListExtra("selected");
                overlayView.display(selected);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
