package es.uva.tfg.hector.tfg;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener2;
import android.hardware.SensorManager;

/**
 * Created by Hector Del Campo Pando on 06/07/2016.
 */
public class OrientationSensor{

    /**
     * Sensor's manager.
     */
    private SensorManager manager;

    /**
     * Orientation's sensors.
     */
    private Sensor sensorAc;    //Acceleration sensor
            Sensor sensorMf;    //Magnetic field sensor

    // Gravity rotational data
    private float gravity[];
    // Magnetic rotational data
    private float magnetic[]; //for magnetic rotational data
    private float accels[] = new float[3];
    private float mags[] = new float[3];
    private float[] values = new float[3];

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
         * {@inheritDoc}
         */
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            switch (sensorEvent.sensor.getType()) {
                case Sensor.TYPE_MAGNETIC_FIELD:
                    mags = sensorEvent.values.clone();
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    accels = sensorEvent.values.clone();
                    break;
            }

            if (mags != null && accels != null) {
                gravity = new float[9];
                magnetic = new float[9];
                SensorManager.getRotationMatrix(gravity, magnetic, accels, mags);
                float[] outGravity = new float[9];
                SensorManager.remapCoordinateSystem(gravity, SensorManager.AXIS_X,SensorManager.AXIS_Z, outGravity);
                SensorManager.getOrientation(outGravity, values);

                azimuth = values[0] * 57.2957795f;
                pitch = values[1] * 57.2957795f;
                roll = values[2] * 57.2957795f;
                mags = null;
                accels = null;

                if(null != activity) {
                    activity.updateDegrees();
                }
            }

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
    private OrientationSensor(Activity activity){
        manager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        sensorAc = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMf = manager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        this.activity = (MainActivity) activity;
    }

    /**
     * Retrieves a new {@link OrientationSensor}, beware events won't be registered until {@link #registerEvents()} is called.
     * @param activity where the sensor will be used.
     */
    public static OrientationSensor createSensor(Activity activity){
        return new OrientationSensor(activity);
    }

    /**
     * Starts registering sensors changes.
     */
    public void registerEvents(){
        manager.registerListener( sEvent, sensorAc, SensorManager.SENSOR_DELAY_NORMAL);
        manager.registerListener( sEvent, sensorMf, SensorManager.SENSOR_DELAY_NORMAL);
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
