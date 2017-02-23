package es.uva.tfg.hector.SkyWalkerApp;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.TextureView;

import java.io.IOException;

/**
 * Controls the camera preview
 * @author Hector Del Campo Pando
 */
public class CameraPreview {

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

        /**
         * Indicates whether the surface was already destroyed or not.
         */
        private volatile boolean destroyed = true;

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface,
                                              int width,
                                              int height) {

            if (!destroyed) {
                return;
            }

            destroyed = false;

            camera.openCamera(activity);

            try {
                camera.setView(previewView);
            } catch (IOException e) {
                Log.e(TAG, "Setting camera's preview surface failed");
            }


            camera.transform(activity.getWindowManager().getDefaultDisplay().getRotation(),
                    width,
                    height);

            camera.startPreview();

        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface,
                                                int width, int height) {

            camera.transform(activity.getWindowManager().getDefaultDisplay().getRotation(),
                    width,
                    height);

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            if (destroyed) {
                return true;
            }
            //When preview is paused the camera device must be freed
            destroyed = true;
            camera.stopPreview();
            camera.closeCamera();
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
        camera = Camera.createInstance();
        previewView.setSurfaceTextureListener(surfaceTextureListener);
    }

    public void stop () {
        if (previewView.isAvailable()) {
            surfaceTextureListener.onSurfaceTextureDestroyed(previewView.getSurfaceTexture());
        }
    }

    public void start () {
        if (previewView.isAvailable()) {
            surfaceTextureListener.onSurfaceTextureAvailable(previewView.getSurfaceTexture(), previewView.getWidth(), previewView.getHeight());
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
