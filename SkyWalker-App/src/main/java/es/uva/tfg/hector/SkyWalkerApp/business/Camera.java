package es.uva.tfg.hector.SkyWalkerApp.business;

import android.app.Activity;
import android.view.TextureView;

import java.io.IOException;
import java.util.Observable;

/**
 * API for camera, Android API Level independent
 * @author Hector Del Campo Pando
 */
public abstract class Camera extends Observable {

    protected final static String TAG = "Camera";

    /**
     * Field of view of the selected camera, and the selected configuration for that camera.
     */
    private float fovWidth,
            fovHeight;

    /**
     * Creates a new instance of the {@link Camera} following singleton pattern.
     * @return the {@link Camera}'s instance.
     */
    public static Camera createInstance(){

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            return new Camera21();
        } else {
            return new Camera1();
        }

    }

    /**
     * Sets where to show the camera's preview
     * @param view
     */
    public abstract void setView(TextureView view) throws IOException;

    /**
     * Starts showing preview where it was previously set with {@link #setView(TextureView)}.
     */
    public abstract void startPreview();

    /**
     * Stops showing preview where it was previously set with {@link #setView(TextureView)}.
     */
    public abstract void stopPreview();

    /**
     * Opens the camera to start using it.
     * @param activity which will use the camera.
     */
    public abstract void openCamera(Activity activity);

    /**
     * Closes the {@link Camera}.
     */
    public abstract void closeCamera();

    /**
     * Retrieves actual field of view and saves it onto fov variable.
     */
    public abstract void evaluateFOV();

    /**
     * Changes camera's orientation to the actual device's one.
     */
    public abstract void transform(int rotation, int width, int height);

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

    /**
     * Setter for field of view width.
     * @param width on degrees.
     */
    protected void setFOVWidth(float width){
        fovWidth = width;
    }

    /**
     * Setter for field of view height.
     * @param height on degrees.
     */
    protected void setFOVHeight(float height){
        fovHeight = height;
    }

}
