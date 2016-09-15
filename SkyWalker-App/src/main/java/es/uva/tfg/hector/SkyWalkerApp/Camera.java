package es.uva.tfg.hector.SkyWalkerApp;

import android.app.Activity;
import android.view.TextureView;

import java.io.IOException;
import java.util.Observable;

/**
 * Created by Hector on 12/9/16.
 */
public abstract class Camera extends Observable {

    protected final static String TAG = "Camera";

    /**
     * Field of view of the selected camera, and the selected configuration for that camera.
     */
    private float fovWidth,
            fovHeight;

    /**
     * Singleton instance.
     */
    private static Camera instance;

    /**
     * Gets an instance of the {@link Camera} following singleton pattern.
     * @return the {@link Camera}'s instance.
     */
    public static Camera getInstance(){
        //TODO complete

        if(null == instance){
            if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
                instance = new Camera21();
            } else {
                instance = new Camera1();
            }
        }

        return instance;

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

    public abstract void openCamera(Activity activity);

    /**
     * Closes the {@link Camera} and clears its reference.
     */
    public abstract void closeCamera();

    /**
     * Retrieves actual field of view and saves it onto fov variable.
     */
    protected abstract void evaluateFOV();

    /**
     * Changes camera's orientation to the actual device's one.
     */
    protected abstract void setOrientation(int rotation, int width, int height);


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

    public void setFOVWidth(float width){
        fovWidth = width;
    }

    public void setFOVHeight(float height){
        fovHeight = height;
    }

}
