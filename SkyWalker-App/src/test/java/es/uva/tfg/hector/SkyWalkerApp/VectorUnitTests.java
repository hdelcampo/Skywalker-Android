package es.uva.tfg.hector.SkyWalkerApp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Hector Del Campo Pando on 20/10/2016.
 */

public class VectorUnitTests {

    private static final float ANGLE_DELTA = 2f;

    @Test
    public void getAngle0() {
        float angle = Vector3D.getAngle(1, 0);
        assertEquals(0 , angle, ANGLE_DELTA);
    }

    @Test
    public void getAngle90() {
        float angle = Vector3D.getAngle(0, 1);
        assertEquals(90 , angle, ANGLE_DELTA);
    }

    @Test
    public void getAngle180() {
        float angle = Vector3D.getAngle(-1, 0);
        assertEquals(180 , angle, ANGLE_DELTA);
    }

    @Test
    public void getAngle270() {
        float angle = Vector3D.getAngle(0, -1);
        assertEquals(270 , angle, ANGLE_DELTA);
    }

    @Test
    public void getAngle45() {
        float angle = Vector3D.getAngle(1, 1);
        assertEquals(45 , angle, ANGLE_DELTA);
    }

    @Test
    public void getAngle135() {
        float angle = Vector3D.getAngle(-1, 1);
        assertEquals(135 , angle, ANGLE_DELTA);
    }

    @Test
    public void getAngle225() {
        float angle = Vector3D.getAngle(-1, -1);
        assertEquals(225 , angle, ANGLE_DELTA);
    }

    @Test
    public void getAngle315() {
        float angle = Vector3D.getAngle(1, -1);
        assertEquals(315 , angle, ANGLE_DELTA);
    }

}
