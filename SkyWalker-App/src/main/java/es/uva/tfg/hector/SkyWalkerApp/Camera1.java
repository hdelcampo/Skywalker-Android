package es.uva.tfg.hector.SkyWalkerApp;

import android.app.Activity;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import java.io.IOException;
import java.util.List;

/**
 * Camera API for Android API 1 or greater
 * @author Hector Del Campo Pando
 */
public class Camera1 extends es.uva.tfg.hector.SkyWalkerApp.Camera {

    /**
     * {@link android.hardware.Camera} to control.
     */
    private android.hardware.Camera camera;

    @Override
    public void setView(TextureView view) throws IOException {
        camera.setPreviewTexture(view.getSurfaceTexture());
    }

    @Override
    public void startPreview() {
        camera.startPreview();
    }

    @Override
    public void stopPreview() {
        camera.stopPreview();
    }

    @Override
    public void openCamera(Activity activity) {
        camera = android.hardware.Camera.open(getBackFacingCameraID());
        evaluateFOV();
    }

    @Override
    public void closeCamera() {
        if(null != camera) {
            camera.release();
            camera = null;
        }
    }

    @Override
    protected void evaluateFOV() {
        Camera.Parameters p = camera.getParameters();

        setFOVWidth(p.getHorizontalViewAngle());
        setFOVHeight(p.getVerticalViewAngle());
    }

    @Override
    protected void transform(int rotation, int width, int height) {

        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(getBackFacingCameraID(), info);

        int degrees = -1;

        // Sets preview size
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size size = getOptimalPreviewSize(parameters.getSupportedPreviewSizes(), width, height);
        parameters.setPreviewSize(size.width, size.height);
        camera.setParameters(parameters);

        // Sets rotation
        switch (rotation){
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            default:
                Log.e(TAG, "Wrong screen rotation");
                break;
        }

        int result = (info.orientation - degrees + 360) % 360;

        camera.setDisplayOrientation(result);

    }

    /**
     * Retrieves back facing camera ID.
     * @return the back facing camera ID.
     */
    private int getBackFacingCameraID () {
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();

        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                break;
            }
        }

        return cameraId;
    }

    /**
     * Retrieves optimal camera size
     * @param sizes available sizes
     * @param w desired width
     * @param h desired height
     * @return the optimal {@link android.hardware.Camera.Size}
     */
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio=(double)h / w;

        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            if (size.width == h && size.height == w) {
                return size;
            }
        }

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }
}
