package es.uva.tfg.hector.SkyWalkerApp;

import android.app.Activity;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import java.io.IOException;

/**
 * Created by Hector on 12/9/16.
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
        camera = android.hardware.Camera.open();
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
    protected void setOrientation(int rotation, int width, int height) {

        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(0, info);

        int degrees = -1;

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
}
