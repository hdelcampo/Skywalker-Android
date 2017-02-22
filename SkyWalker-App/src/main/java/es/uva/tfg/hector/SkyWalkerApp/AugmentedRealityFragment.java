package es.uva.tfg.hector.SkyWalkerApp;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
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

    /**
     * Connection thread.
     */
    private final Thread connectionThread = new Thread() {

        private boolean stopped = false;

        @Override
        public void run() {

            while (!stopped) {
                List<PointOfInterest> points = getActivePoints();
                for (PointOfInterest point : points) {
                    ServerHandler.getInstance(getActivity().getApplicationContext()).
                            getLastPosition(new ServerHandler.OnServerResponse<PointOfInterest>() {
                                @Override
                                public void onSuccess(PointOfInterest response) {

                                }

                                @Override
                                public void onError(ServerHandler.Errors error) {
                                    Log.e("Updating error", "Couldnt update " + error.toString());
                                }
                            }, point);
                }

                try {
                    sleep(1000);
                    Log.e("Updating", "Updating tags");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void interrupt() {
            super.interrupt();
            stopped = true;
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.augmented_reality_layout, container, false);

        cameraPreview = new CameraPreview((TextureView)rootView.findViewById(R.id.camera_texture), getActivity());
        overlayView = new OverlayView((TextureView)rootView.findViewById(R.id.overlay_texture), getActivity(), cameraPreview.getCamera());

        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();

        if (connectionThread.isInterrupted()) {
            connectionThread.start();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        connectionThread.interrupt();
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
