package es.uva.tfg.hector.SkyWalkerApp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.Observable;

/**
 * Orientation sensors manager
 * @author Hector Del Campo Pando
 **/
public class OrientationSensor extends Observable {

    /**
     * Sensor data sampling refresh delay in microseconds
     */
    public static final int SENSOR_DELAY = SensorManager.SENSOR_DELAY_GAME;

    /**
     * Sensor's manager.
     */
    private SensorManager manager;

    /**
     * Orientation's sensors.
     */
    private Sensor sensorRt;

    /**
     * Device's 3D orientation vector.
     */
    private Vector3D orientationVector = new Vector3D(0, 0, 0);

    /**
     * Listener for the sensors
     */
    private SensorEventListener sEvent = new SensorEventListener(){

        /**
         * Value to filter data.
         */
        private static final float ALPHA = 0.25f;

        /**
         * Sensor's values.
         */
        private float[] rotateVector;

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {

            if(sensorEvent.sensor.getType() != Sensor.TYPE_ROTATION_VECTOR) {
                return;
            }

            rotateVector = lowFilter(sensorEvent.values.clone(), rotateVector);

            float[] rotationMatrix = new float[16];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, rotateVector);

            // Compensate device's rotation
            remapCoordinates(rotationMatrix, rotationMatrix);

            Matrix rMatrix = new Matrix(4, 4);

            for (int i = 0; i < rotationMatrix.length; i++) {
                rMatrix.set(i / 4, i % 4 , rotationMatrix[i]);
            }

            Vector2D mapNorth = Center.getInstance().getMapNorth();

            Matrix rotationVector = rMatrix.multiply(
                    new Matrix(
                    new double[][]{
                            {mapNorth.getX()},
                            {mapNorth.getY()},
                            {0},
                            {0}
                    }));

            orientationVector = new Vector3D(
                    rotationVector.get(0, 0),
                    rotationVector.get(1, 0),
                    rotationVector.get(2, 0));
            orientationVector.normalize();

            setChanged();
            notifyObservers();
        }

        /**
         * Remaps coordinates considering device rotation.
         * Note that input and output parameters can't be the same.
         * @param rotationMatrix input to be remaped.
         * @param remappedRotationMatrix output remaped.
         */
        private void remapCoordinates(float[] rotationMatrix, float[] remappedRotationMatrix) {
            SensorManager.remapCoordinateSystem(rotationMatrix,
                    SensorManager.AXIS_X,
                    SensorManager.AXIS_Z,
                    remappedRotationMatrix);
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

        /**
         * {@inheritDoc}
         */
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }

    };

    /**
     * Creates a new {@link OrientationSensor}, beware events won't be registered until {@link #registerEvents()} is called.
     * @param context where the sensor will be used.
     */
    public OrientationSensor(Context context) {
        manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensorRt = manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    /**
     * Starts registering sensors changes.
     */
    public void registerEvents(){
        manager.registerListener(sEvent, sensorRt, SENSOR_DELAY);
    }

    /**
     * Stops registering sensors changes.
     */
    public void unregisterEvents(){
        manager.unregisterListener(sEvent);
    }

    /**
     * Retrieves the device's orientation as a Vector.
     * @return the orientation's vector.
     */
    public Vector3D getOrientationVector() {
        return orientationVector;
    }

}
