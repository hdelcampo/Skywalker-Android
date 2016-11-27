package es.uva.tfg.hector.SkyWalkerApp;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.TextureView;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Hector Del Campo Pando on 13/07/2016.
 */
public class CameraPreview implements Observer{

    private final static String TAG = "Preview";

    /**
     * {@link TextureView} where to display the camera preview.
     */
    private TextureView previewView;

    /**
     * Camera from where take images.
     */
    private Camera camera;

    /**
     * Activity where this preview lives in.
     */
    private Activity activity;

    /**
     * The view listener
     */
    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener(){

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface,
                                              int width,
                                              int height) {

            camera.openCamera(activity);

            try {
                camera.setView(previewView);
            } catch (IOException e) {
                Log.e(TAG, "Setting camera's preview surface failed");
            }

            camera.startPreview();

            camera.setOrientation(activity.getWindowManager().getDefaultDisplay().getRotation(),
                    width,
                    height);

        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface,
                                                int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            //When preview is paused the camera device must be freed
            camera.stopPreview();
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }

    };

    /**
     * Creates a new camera preview.
     * @param view where to show the camera preview.
     */
    public CameraPreview(TextureView view, Activity activity){
        this.activity = activity;
        previewView = view;
        camera = Camera.getInstance();
        camera.addObserver(this);
        previewView.setSurfaceTextureListener(surfaceTextureListener);
    }

    @Override
    public void update(Observable observable, Object o) {
        switch (observable.getClass().getSimpleName()){
            case "Camera21":
                //startPreview();
                break;
            default:
                Log.e(TAG, "unexpected update");
                break;
        }
    }


    /**
     * Retrieves the used {@code Camera}.
     * @return the camera.
     */
    public Camera getCamera(){
        return camera;
    }
}
