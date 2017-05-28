package es.uva.tfg.hector.SkyWalkerApp.business;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;

import es.uva.tfg.hector.SkyWalkerApp.services.Vector2D;
import es.uva.tfg.hector.SkyWalkerApp.services.Vector3D;

/**
 * Orientation sensors manager.
 * @author Hector Del Campo Pando
 **/
public class OrientationSensor {

    /**
     * Sensor data sampling refresh delay in microseconds
     */
    public static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_GAME;

    /**
     * Sensor's manager.
     */
    private final SensorManager manager;

    /**
     * Orientation's sensors.
     */
    private final Sensor sensorRt;

    /**
     * Orientation's handling thread.
     */
    private HandlerThread thread;

    /**
     * Device's 3D orientation vector.
     */
    private Vector3D orientationVector = new Vector3D(0, 1, 0);

    /**
     * Delegate who wants to get sensor events.
     */
    private final OrientationSensorDelegate delegate;

    /**
     * Offset to correct angle.
     */
    private final float mapNorthOffset = User.getInstance().getCenter().getMapNorth();

    /**
     * Listener for the sensors
     */
    private final SensorEventListener eventListener = new SensorEventListener(){

        /**
         * Value to filter data.
         */
        private static final float ALPHA = .25f;

        /**
         * Sensor's values.
         */
        private float[] previousValues;

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            if(sensorEvent.sensor.getType() != Sensor.TYPE_ROTATION_VECTOR) {
                return;
            }

            float[] rotationMatrix = new float[9];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, sensorEvent.values.clone());

            previousValues = lowFilter(new float[]{rotationMatrix[2], rotationMatrix[5], rotationMatrix[8]}, previousValues);

            Vector2D mapVector =
                    new Vector2D(
                            -previousValues[0],
                            -previousValues[1]);
            mapVector.rotateClockwise(mapNorthOffset);

            orientationVector = new Vector3D(
                    mapVector.getX(),
                    mapVector.getY(),
                    -previousValues[2]);
            orientationVector.normalize();

            delegate.onSensorValueEvent(orientationVector);

        }

        /**
         * Low pass filters the input data and returns corrected values.
         * @param input data to be filtered.
         * @param previousValues previous data vector.
         */
        private float[] lowFilter(float[] input, float[] previousValues){
            if(null == previousValues){
                return input;
            }

            float[] output = new float[input.length];

            for(int i = 0; i<input.length; i++){
                output[i] = previousValues[i] + ALPHA * (input[i] - previousValues[i]);
            }

            return output;
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int value) {
            if (Sensor.TYPE_ROTATION_VECTOR == sensor.getType()) {
                delegate.onSensorAccuracyChange(value);
            }
        }

    };

    /**
     * Creates a new {@link OrientationSensor}, beware events won't be registered until {@link #registerEvents()} is called.
     * @param context where the sensor will be used.
     * @param delegate who wants to receive events.
     */
    public OrientationSensor(Context context, OrientationSensorDelegate delegate) {
        manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorRt = manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        this.delegate = delegate;
    }

    /**
     * Starts registering sensors changes.
     */
    public void registerEvents() {
        thread = new HandlerThread("Orientation thread");
        thread.start();
        Handler handler = new Handler(thread.getLooper());
        manager.registerListener(eventListener, sensorRt, SENSOR_DELAY, handler);
    }

    /**
     * Stops registering sensors changes.
     */
    public void unregisterEvents(){
        manager.unregisterListener(eventListener);
        thread.quitSafely();
    }

    /**
     * Retrieves the device's orientation as a Vector.
     * @return the orientation's vector.
     */
    public Vector3D getOrientationVector() {
        return orientationVector;
    }

    /**
     * Checks whether the device is capable of using this sensor or not.
     * @param context of the App.
     * @return true if device has the needed sensor, false otherwise.
     */
    public static boolean isCapable (Context context) {

        SensorManager manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        return null != manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

    }

    /**
     * Interface for sensor event's delegates
     */
    public interface OrientationSensorDelegate {

        /**
         * Callback for sensor value's changes.
         * @param values the new orientation values.
         */
        void onSensorValueEvent (Vector3D values);

        /**
         * Callback for sensor's accuracy changes.
         * @param accuracy the new accuracy value.
         */
        void onSensorAccuracyChange (int accuracy);

    }

}
