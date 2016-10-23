package es.uva.tfg.hector.SkyWalkerApp;

/**
 * Created by Hector Del Campo Pando on 20/10/2016.
 */

/**
 * Class representing 3D location, also provides some services such as angles, distances, etc.
 */
public class Vector3D {

    /**
     * Retrieves angle in degrees, values from [0, 360).
     * @param x the abscissa coordinate.
     * @param y the ordinate coordinate.
     * @return the angle
     */
    public static float getAngle(float x, float y){
        float angle = 0;

        angle = (float) Math.toDegrees(Math.atan2(y, x));

        if (angle < 0) {
            // Sum complete circle
            angle += 180*2;
        }

        return angle;
    }

}
