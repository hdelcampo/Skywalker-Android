package es.uva.tfg.hector.tfg;

import android.app.Fragment;
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
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Size;
import android.util.SizeF;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Arrays;

/**
 * Created by Hector Del Campo Pando on 06/07/2016.
 */
public class CameraFragment extends Fragment{

    private final static String TAG = "Camera2testJ";

    /**
     * {@link TextureView} where to display the camera preview.
     */
    private TextureView mTextureView;

    private Size mPreviewSize;

    private CaptureRequest.Builder mPreviewBuilder;
    private CameraCaptureSession mPreviewSession;

    /**
     * The view listener
     */
    private TextureView.SurfaceTextureListener mSurfaceTextureListener = new TextureView.SurfaceTextureListener(){

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface,
                                              int width, int height) {
            // TODO Auto-generated method stub
            openCamera();
            //showFOV();
            transformImage(width, height);

        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface,
                                                int width, int height) {

        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {

        }

    };

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
        associateView();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    /**
     * Associates the view where to set the camera preview and prepares it.
     */
    private void associateView() {
        mTextureView = (TextureView)getActivity().findViewById(R.id.textura);
        mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause() {
        super.onPause();

        //When preview is paused the camera device must be freed
        freeCameraDevice();
    }

    /**
     * Field of view of the selected camera, and the selected configuration for that camera.
     */
    private float fovWidth,
                 fovHeight;

    /**
     * Selected camera
     */
    private CameraDevice cameraDevice;

    /**
     * Characteristics of the selected camera.
     */
    private CameraCharacteristics cameraCharacteristics;

    /**
     * Closes the {@link CameraDevice} and clears its reference.
     */
    void freeCameraDevice() {
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }

    public void openCamera() {
        CameraManager manager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
        try {
            //TODO select camera
            String cameraId = manager.getCameraIdList()[0];
            cameraCharacteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            mPreviewSize = map.getOutputSizes(SurfaceTexture.class)[0];
            manager.openCamera(cameraId, mStateCallback, null);
            evaluateFOV();
        } catch (CameraAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void transformImage(int width, int height) {
        Matrix matrix = new Matrix();
        int rotation = getActivity().getWindowManager().getDefaultDisplay().getRotation();
        RectF textureRectF = new RectF(0, 0, width, height);
        RectF previewRectF = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = textureRectF.centerX();
        float centerY = textureRectF.centerY();
        if(rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270){
            previewRectF.offset(centerX - previewRectF.centerX(),
                    centerY - previewRectF.centerY());
            matrix.setRectToRect(textureRectF, previewRectF, Matrix.ScaleToFit.FILL);
            float scale = Math.max((float) width / mPreviewSize.getWidth(),
                    (float)height / mPreviewSize.getHeight());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }

    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice camera) {
            // TODO Auto-generated method stub
            Log.e(TAG, "onOpened");
            cameraDevice = camera;
            startPreview();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            // TODO Auto-generated method stub
            Log.e(TAG, "onDisconnected");
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            // TODO Auto-generated method stub
            Log.e(TAG, "onError");
        }

    };

    private void startPreview() {
        // TODO Auto-generated method stub
        if(null == cameraDevice || !mTextureView.isAvailable() || null == mPreviewSize) {
            Log.e(TAG, "startPreview fail, return");
        }

        SurfaceTexture texture = mTextureView.getSurfaceTexture();
        if(null == texture) {
            Log.e(TAG,"texture is null, return");
            return;
        }

        texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
        Surface surface = new Surface(texture);

        try {
            mPreviewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
        } catch (CameraAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        mPreviewBuilder.addTarget(surface);

        try {
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {

                @Override
                public void onConfigured(CameraCaptureSession session) {
                    // TODO Auto-generated method stub
                    mPreviewSession = session;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession session) {
                    // TODO Auto-generated method stub
                    Toast.makeText(getActivity(), "onConfigureFailed", Toast.LENGTH_LONG).show();
                }
            }, null);
        } catch (CameraAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void updatePreview() {
        // TODO Auto-generated method stub
        if(null == cameraDevice) {
            Log.e(TAG, "updatePreview error, return");
        }

        mPreviewBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        HandlerThread thread = new HandlerThread("CameraPreview");
        thread.start();
        Handler backgroundHandler = new Handler(thread.getLooper());

        try {
            mPreviewSession.setRepeatingRequest(mPreviewBuilder.build(), null, backgroundHandler);
        } catch (CameraAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     *  Retrieves actual field of view and saves it onto fov variable.
     */
    private void evaluateFOV() {
        SizeF sSize = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
        //TODO ARRAY?
        float fLength = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)[0];

        fovWidth = (float) Math.toDegrees((2 * Math.atan(sSize.getWidth() / (2 * fLength))));
        fovHeight = (float) Math.toDegrees((2 * Math.atan(sSize.getHeight() / (2 * fLength))));
    }

    /**
     * Retrieves camera current fov width.
     * @return fov width in degrees.
     */
    public float getFOVWidth(){
        return fovWidth;
    }

    /**
     * Retrieves camera current fov width.
     * @return fov width in degrees.
     */
    public float getFOVHeight(){
        return fovHeight;
    }

}
