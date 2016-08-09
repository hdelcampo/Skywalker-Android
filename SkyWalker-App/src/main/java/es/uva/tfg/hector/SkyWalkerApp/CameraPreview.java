package es.uva.tfg.hector.SkyWalkerApp;

import android.app.Activity;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import java.util.Arrays;
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
     * Preview's size.
     */
    private Size previewSize;

    /**
     * Preview's builder.
     */
    private CaptureRequest.Builder previewBuilder;

    /**
     * Preview's session.
     */
    private CameraCaptureSession previewSession;

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
            previewSize = camera.getStreamConfigurationMap().getOutputSizes(SurfaceTexture.class)[0];
            transformImage(width, height);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface,
                                                int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            //When preview is paused the camera device must be freed
            camera.closeCamera();
            return false;
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
        camera = new Camera();
        camera.addObserver(this);
        previewView.setSurfaceTextureListener(surfaceTextureListener);
    }

    /**
     * Starts the preview if it's not {@code null}.
     */
    private void startPreview() {
        if(null == camera ||
                !previewView.isAvailable() ||
                null == previewSize) {
            Log.e(TAG, "startPreview fail, return");
            return;
        }

        SurfaceTexture texture = previewView.getSurfaceTexture();
        if(null == texture) {
            Log.e(TAG,"texture is null, return");
            return;
        }

        texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
        Surface surface = new Surface(texture);

        try {
            previewBuilder = camera.getCaptureRequest();
        } catch (CameraAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        previewBuilder.addTarget(surface);

        try {
            camera.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(CameraCaptureSession session) {
                    previewSession = session;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                    Log.e(TAG, "configure failed");
                }
            }, null);
        } catch (CameraAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        if(null == camera) {
            Log.e(TAG, "updatePreview error, return");
        }

        previewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);

        HandlerThread thread = new HandlerThread("CameraPreview");
        thread.start();
        Handler backgroundHandler = new Handler(thread.getLooper());

        try {
            previewSession.setRepeatingRequest(previewBuilder.build(), null, backgroundHandler);
        } catch (CameraAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void transformImage(int width, int height) {
        Matrix matrix = new Matrix();
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        RectF textureRectF = new RectF(0, 0, width, height);
        RectF previewRectF = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
        float centerX = textureRectF.centerX();
        float centerY = textureRectF.centerY();
        if(rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270){
            previewRectF.offset(centerX - previewRectF.centerX(),
                    centerY - previewRectF.centerY());
            matrix.setRectToRect(textureRectF, previewRectF, Matrix.ScaleToFit.FILL);
            float scale = Math.max((float) width / previewSize.getWidth(),
                    (float)height / previewSize.getHeight());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        previewView.setTransform(matrix);
    }

    @Override
    public void update(Observable observable, Object o) {
        switch (observable.getClass().getSimpleName()){
            case "Camera":
                startPreview();
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
