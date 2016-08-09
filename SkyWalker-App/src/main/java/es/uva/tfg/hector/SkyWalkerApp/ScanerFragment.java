package es.uva.tfg.hector.SkyWalkerApp;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Hector Del Campo Pando on 06/07/2016.
 */
public class ScanerFragment extends Fragment{

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
        return super.onCreateView(inflater, container, savedInstanceState);
    }

}
