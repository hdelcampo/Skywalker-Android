package es.uva.tfg.hector.SkyWalkerApp.presentation;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import es.uva.tfg.hector.SkyWalkerApp.R;
import es.uva.tfg.hector.SkyWalkerApp.business.PointOfInterest;
import es.uva.tfg.hector.SkyWalkerApp.persistence.ServerFacade;

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
    private Thread connectionThread;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.augmented_reality_layout, container, false);

        cameraPreview = new CameraPreview((TextureView)rootView.findViewById(R.id.camera_texture), getActivity());
        overlayView = new OverlayView((TextureView)rootView.findViewById(R.id.overlay_texture), getActivity(), cameraPreview.getCamera());

        return rootView;

    }

    @Override
    public void onStart() {
        super.onStart();

        if (!ServerFacade.getInstance(getActivity().getApplicationContext()).isDemo()) {
            connectionThread = new ConnectionThread();
            connectionThread.start();
        }

        cameraPreview.start();
        overlayView.start();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (connectionThread != null) {
            connectionThread.interrupt();
        }
        cameraPreview.stop();
        overlayView.stop();
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

    private class ConnectionThread extends Thread {

        private static final long SLEEP_TIME = 250; //miliseconds

        private volatile boolean running = true;

        @Override
        public void run() {

            while (running) {
                List<PointOfInterest> points = AugmentedRealityFragment.this.getActivePoints();
                for (PointOfInterest point : points) {
                    ServerFacade.getInstance(getActivity().getApplicationContext()).
                            getLastPosition(new ServerFacade.OnServerResponse<PointOfInterest>() {
                                @Override
                                public void onSuccess(PointOfInterest response) {
                                    Log.e("Updating success", "Could update " + response.toString());
                                }

                                @Override
                                public void onError(ServerFacade.Errors error) {
                                    Log.e("Updating error", "Couldnt update " + error.toString());
                                }
                            }, point);
                }

                try {
                    sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }

        @Override
        public void interrupt() {
            super.interrupt();
            running = false;
        }


        }

}
