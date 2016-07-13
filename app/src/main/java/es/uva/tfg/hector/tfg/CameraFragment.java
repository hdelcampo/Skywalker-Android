package es.uva.tfg.hector.tfg;

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
public class CameraFragment extends Fragment{

    /**
     * Camera's preview
     */
    private CameraPreview cameraPreview;

    /**
     * Retrieves a new {@link CameraFragment}.
     * @return the new {@link CameraFragment}.
     */
    public static CameraFragment newInstance(){
        return new CameraFragment();
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        cameraPreview = new CameraPreview((TextureView)getActivity().findViewById(R.id.textura), getActivity());
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause() {
        super.onPause();
        cameraPreview.onPause();
    }

}
