package es.uva.tfg.hector.SkyWalkerApp.business;

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

import java.util.Collections;

/**
 * Camera API for Android API 21 or greater
 * @author Hector Del Campo Pando
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class Camera21 extends Camera {

    public static final String BACKGROUND_THREAD = "CameraThread";
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

    /**
     * Additional thread to run tasks without blocking UI.
     */
    private HandlerThread backgroundThread;

    /**
     * A {@code Handler} for running tasks in the background.
     */
    private Handler backgroundHandler;

    /**
     * Camera's state
     */
    private volatile boolean openning = false;

    /**
     * Camera's callback
     */
    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice camera) {
            cameraDevice = camera;
            evaluateFOV();
            openning = false;
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            closeCamera();
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

        if(null == texture || null == cameraDevice) {
            return;
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                createCaptureRequest();
            }
        };

        Thread thread = new Thread(runnable);
        thread.run();

    }

    /**
     * Creates a capture request, with preview template,
     * if camera is not opened yet, this call will make thread sleep until its opened.
     */
    private void createCaptureRequest() {

        while (openning) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            previewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        SurfaceTexture surfaceTexture = texture.getSurfaceTexture();
        final int width = Math.max(texture.getWidth(), texture.getHeight());
        final int height = Math.min(texture.getWidth(), texture.getHeight());
        surfaceTexture.setDefaultBufferSize(width, height);
        Surface surface = new Surface(surfaceTexture);
        previewBuilder.addTarget(surface);

        try {
            cameraDevice.createCaptureSession(Collections.singletonList(surface), new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(CameraCaptureSession session) {
                    previewSession = session;
                    startUpdatingPreview();
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                    Log.e(TAG, "configure failed");
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts updating the preview.
     */
    private void startUpdatingPreview() {

        previewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_VIDEO);

        try {
            previewSession.setRepeatingRequest(previewBuilder.build(), null, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void stopPreview() {

        if (previewSession == null) {
            return;
        }

        try {
            previewSession.stopRepeating();
            previewSession.abortCaptures();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void openCamera(Activity activity) {

        openning = true;
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);

        backgroundThread = new HandlerThread(BACKGROUND_THREAD);
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());

        try {
            String cameraId = getRearCamera(manager);
            manager.openCamera(cameraId, mStateCallback, backgroundHandler);
        } catch (CameraAccessException e) {
            openning = false;
            e.printStackTrace();
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

        /**
         * Stop threads
         */
        backgroundThread.quitSafely();

        try {
            backgroundThread.join();
            backgroundThread = null;
            backgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void evaluateFOV() {
        SizeF sSize = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
        //TODO ARRAY?
        float fLength = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)[0];

        setFOVWidth((float) Math.toDegrees((2 * Math.atan(sSize.getWidth() / (2 * fLength)))));
        setFOVHeight((float) Math.toDegrees((2 * Math.atan(sSize.getHeight() / (2 * fLength)))));
    }

    @Override
    public void transform(int rotation, int width, int height) {

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
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }

        texture.setTransform(matrix);

    }

}
