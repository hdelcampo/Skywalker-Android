package es.uva.tfg.hector.SkyWalkerApp;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;
import android.view.Display;

import java.util.Observable;

/**
 * Created by Hector Del Campo Pando on 06/07/2016.
 */
public class OrientationSensor extends Observable{

    /**
     * Sensor's manager.
     */
    private SensorManager manager;

    /**
     *
     */
    private Display display;

    /**
     * Orientation's sensors.
     */
    private Sensor sensorRt;    //Rotation vector

    /**
     * Orientation's degrees.
     */
    private float azimuth;
            float pitch;
            float roll;

    //TODO observer pattern
    private MainActivity activity;

    /**
     * Listener for the sensors
     */
    private SensorEventListener2 sEvent = new SensorEventListener2(){

        /**
         * Value to filter data.
         */
        private static final float ALPHA = 0.25f;

        /**
         * Sensor's values.
         */
        private float[] rotateVector;

        /**
         * {@inheritDoc}
         */
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if(sensorEvent.sensor.getType() != Sensor.TYPE_ROTATION_VECTOR) {
                return;
            }

            rotateVector = lowFilter(sensorEvent.values.clone(), rotateVector);

            float[] rotationMatrix = new float[16];
            SensorManager.getRotationMatrixFromVector(rotationMatrix, rotateVector);

            /* Compensate device orientation */
            float[] remappedRotationMatrix = new float[16];
            remapCoordinates(rotationMatrix, remappedRotationMatrix);

            float[] orientationValues = new float[3];
            SensorManager.getOrientation(remappedRotationMatrix, orientationValues);
            azimuth = (float)Math.toDegrees(orientationValues[0]);
            pitch = (float)Math.toDegrees(orientationValues[1]);
            roll = (float)Math.toDegrees(orientationValues[2]);

            //Once degrees have been updated notify
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

        /**
         * {@inheritDoc}
         */
        @Override
        public void onFlushCompleted(Sensor sensor) {

        }
    };

    /**
     * Creates a new {@link OrientationSensor} instating acceleration and magnetic field sensors.
     * @param activity where the sensor will be used.
     */
    private OrientationSensor(Activity activity, Display display){
        this.display = display;
        manager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        sensorRt = manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        this.activity = (MainActivity) activity;
    }

    /**
     * Retrieves a new {@link OrientationSensor}, beware events won't be registered until {@link #registerEvents()} is called.
     * @param activity where the sensor will be used.
     */
    public static OrientationSensor createSensor(Activity activity, Display display){
        return new OrientationSensor(activity, display);
    }

    /**
     * Starts registering sensors changes.
     */
    public void registerEvents(){
        manager.registerListener(sEvent, sensorRt, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Stops registering sensors changes.
     */
    public void unregisterEvents(){
        if (null != manager){
            manager.unregisterListener(sEvent);
        }
    }

    /**
     * Retrieves the azimuth in degrees.
     * @return the azimuth.
     */
    public float getAzimuth() {
        return azimuth;
    }

    /**
     * Retrieves the pitch in degrees.
     * @return the pitch.
     */
    public float getPitch() {
        return pitch;
    }

    /**
     * Retrieves the roll in degrees.
     * @return the roll.
     */
    public float getRoll() {
        return roll;
    }
}
