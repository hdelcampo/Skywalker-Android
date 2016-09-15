package es.uva.tfg.hector.SkyWalkerApp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.SizeF;
import android.view.Surface;
import android.view.TextureView;

import java.util.Arrays;

/**
 * Created by Hector Del Campo Pando on 13/07/2016.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Camera21 extends Camera {

    /**
     * Selected camera
     */
    private CameraDevice cameraDevice;

    /**
     * Characteristics of the selected camera.
     */
    private CameraCharacteristics cameraCharacteristics;

    /**
     * Surface where to show camera's preview.
     */
    private TextureView texture;

    /**
     * Preview's builder.
     */
    private CaptureRequest.Builder previewBuilder;

    /**
     * Preview's session.
     */
    private CameraCaptureSession previewSession;

    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice camera) {
            cameraDevice = camera;
        }

        @Override
        public void onDisconnected(CameraDevice camera) {

        }

        @Override
        public void onError(CameraDevice camera, int error) {

        }

    };

    @Override
    public void setView(TextureView view) {
        texture = view;
    }

    @Override
    public void startPreview() {
        if(null == texture){
            Log.e(TAG, "surface not set");
            return;
        }

        if(null == cameraDevice) {
            return;
        }

        try {
            previewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        } catch (CameraAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        SurfaceTexture surfaceTexture = texture.getSurfaceTexture();
        surfaceTexture.setDefaultBufferSize(1080, 1920);
        Surface surface = new Surface(surfaceTexture);
        previewBuilder.addTarget(surface);

        try {
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {

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

    @Override
    public void stopPreview() {

    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void openCamera(Activity activity) {
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);

        final HandlerThread thread = new HandlerThread("Camera opening handler");
        thread.start();
        final Handler handler = new Handler(thread.getLooper());

        try {
            String cameraId = getRearCamera(manager);
            manager.openCamera(cameraId, mStateCallback, handler);
            evaluateFOV();
        } catch (CameraAccessException e) {
            Log.e(TAG, "Error opening camera");
        }
    }

    /**
     * Finds rear facing camera.
     * @param manager of the camera.
     * @return rear facing camera's ID.
     * @throws CameraAccessException if there is an error within the camera.
     */
    private String getRearCamera(CameraManager manager) throws CameraAccessException {
        int orientation;
        for(final String id: manager.getCameraIdList()){
            cameraCharacteristics = manager.getCameraCharacteristics(id);
            orientation = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING);
            if(CameraCharacteristics.LENS_FACING_BACK == orientation){
                return id;
            }
        }

        return null;
    }

    @Override
    public void closeCamera() {
        if (null != cameraDevice){
            cameraDevice.close();
            cameraDevice = null;
        }
    }

    @Override
    protected void evaluateFOV() {
        SizeF sSize = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
        //TODO ARRAY?
        float fLength = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)[0];

        setFOVWidth((float) Math.toDegrees((2 * Math.atan(sSize.getWidth() / (2 * fLength)))));
        setFOVHeight((float) Math.toDegrees((2 * Math.atan(sSize.getHeight() / (2 * fLength)))));
    }

    @Override
    protected void setOrientation(int rotation, int width, int height) {
        Matrix matrix = new Matrix();
        RectF textureRectF = new RectF(0, 0, width, height);
        RectF previewRectF = new RectF(0, 0, texture.getHeight(), texture.getWidth());
        float centerX = textureRectF.centerX();
        float centerY = textureRectF.centerY();
        if(rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) {
            previewRectF.offset(centerX - previewRectF.centerX(),
                    centerY - previewRectF.centerY());
            matrix.setRectToRect(textureRectF, previewRectF, Matrix.ScaleToFit.FILL);
            float scale = Math.max((float)width / texture.getWidth(),
                    (float)height / texture.getHeight());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        texture.setTransform(matrix);

    }

}
