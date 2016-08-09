package es.uva.tfg.hector.SkyWalkerApp;

import android.app.Activity;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Handler;
import android.util.SizeF;
import android.view.Surface;

import java.util.List;
import java.util.Observable;

/**
 * Created by Hector Del Campo Pando on 13/07/2016.
 */
public class Camera extends Observable {

    private final static String TAG = "Camera";

    private CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice camera) {
            cameraDevice = camera;
            setChanged();
            notifyObservers();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {

        }

        @Override
        public void onError(CameraDevice camera, int error) {

        }

    };

    /**
     * Selected camera
     */
    private CameraDevice cameraDevice;

    /**
     * Characteristics of the selected camera.
     */
    private CameraCharacteristics cameraCharacteristics;

    /**
     * Field of view of the selected camera, and the selected configuration for that camera.
     */
    private float fovWidth,
                fovHeight;

    public void openCamera(Activity activity) {
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            //TODO select camera
            String cameraId = manager.getCameraIdList()[0];
            cameraCharacteristics = manager.getCameraCharacteristics(cameraId);
            manager.openCamera(cameraId, mStateCallback, null);
            evaluateFOV();
        } catch (CameraAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public StreamConfigurationMap getStreamConfigurationMap() {
        return cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
    }

    /**
     * Closes the {@link CameraDevice} and clears its reference.
     */
    public void closeCamera() {
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
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

    public CaptureRequest.Builder getCaptureRequest() throws CameraAccessException {
        return cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
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

    public void createCaptureSession(List<Surface> surfaces,
                                     CameraCaptureSession.StateCallback stateCallback,
                                     Handler handler) throws CameraAccessException {
        cameraDevice.createCaptureSession(surfaces, stateCallback, handler);
    }

}
