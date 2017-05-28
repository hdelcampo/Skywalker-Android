package es.uva.tfg.hector.SkyWalkerApp.presentation;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.view.TextureView;

import java.io.IOException;

import es.uva.tfg.hector.SkyWalkerApp.business.Camera;

/**
 * Controls the camera preview
 * @author Hector Del Campo Pando
 */
class CameraPreview {

    /**
     * {@link TextureView} where to display the camera preview.
     */
    private final TextureView previewView;

    /**
     * Camera from where take images.
     */
    private final Camera camera;

    /**
     * Activity where this preview lives in.
     */
    private final Activity activity;

    private final int width, height;

    /**
     * The view listener
     */
    private final TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener(){

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface,
                                              int width,
                                              int height) {

            try {
                camera.setView(previewView);
            } catch (IOException e) {
                e.printStackTrace();
            }


            camera.transform(activity.getWindowManager().getDefaultDisplay().getRotation(),
                    CameraPreview.this.width == -1 ? width : CameraPreview.this.width,
                    CameraPreview.this.height == -1 ? height : CameraPreview.this.height);

            camera.startPreview();

        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface,
                                                int width, int height) {

            if (surface == null) {
                return;
            }

            try {
                camera.stopPreview();
            } catch (IllegalStateException e) {
                return;
            }

            camera.transform(activity.getWindowManager().getDefaultDisplay().getRotation(),
                    CameraPreview.this.width == -1 ? width : CameraPreview.this.width,
                    CameraPreview.this.height == -1 ? height : CameraPreview.this.height);

            camera.startPreview();

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
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
    CameraPreview(TextureView view, Activity activity){
        this.activity = activity;
        previewView = view;
        camera = Camera.createInstance();
        previewView.setSurfaceTextureListener(surfaceTextureListener);
        width = -1;
        height = -1;
    }

    /**
     * Creates a new camera preview.
     * @param view where to show the camera preview.
     */
    CameraPreview(TextureView view, Activity activity, int width, int height){
        this.activity = activity;
        previewView = view;
        camera = Camera.createInstance();
        previewView.setSurfaceTextureListener(surfaceTextureListener);
        this.width = width;
        this.height = height;
    }

    /**
     * Stops the camera.
     */
    void stop() {
        camera.stopPreview();
        camera.closeCamera();
    }

    /**
     * Starts the camera.
     */
    public void start () {
        camera.openCamera(activity);
        if (previewView.isAvailable()) {
            surfaceTextureListener.onSurfaceTextureAvailable(previewView.getSurfaceTexture(),
                    previewView.getWidth(),
                    previewView.getHeight());
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
