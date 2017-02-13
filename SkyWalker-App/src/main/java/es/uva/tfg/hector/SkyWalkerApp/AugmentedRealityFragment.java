package es.uva.tfg.hector.SkyWalkerApp;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Augmented reality fragment
 * @author Hector Del Campo Pando
 */
public class AugmentedRealityFragment extends Fragment {

    /**
     * Camera's preview.
     */
    private CameraPreview cameraPreview;

    /**
     * Overlay where objects will be displayed.
     */
    private OverlayView overlayView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.augmented_reality_layout, container, false);

        cameraPreview = new CameraPreview((TextureView)rootView.findViewById(R.id.camera_texture), getActivity());
        overlayView = new OverlayView((TextureView)rootView.findViewById(R.id.overlay_texture), getActivity(), cameraPreview.getCamera());

        return rootView;

    }

    /**
     * Retrieves all points that are being shown.
     * @return A list of points being shown.
     */
    public List<PointOfInterest> getActivePoints () {
        return overlayView.getActivePoints();
    }

    /**
     * Stops showing all points except the desired ones.
     * @param points to be shown.
     */
    public void setActivePoints (List<PointOfInterest> points) {
        overlayView.display(points);
    }
}
