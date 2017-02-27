package es.uva.tfg.hector.SkyWalkerApp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Vector3D class tests.
 * @author Héctor Del Campo Pando
 */
public class Vector3DUnitTests {

    /*
     * Constructors
     */
    @Test
    public void constructor() {
        final Vector3D vector = new Vector3D(1,2,3);
        assertEquals(1, vector.getX(), 0);
        assertEquals(2, vector.getY(), 0);
        assertEquals(3, vector.getZ(), 0);
    }

    /*
     * Normalize method
     */
    @Test
    public void normalizeVector() {
        final Vector3D vector = new Vector3D(3, 1, 2);
        vector.normalize();
        assertEquals(0.802, vector.getX(), 0.1f);
        assertEquals(0.267, vector.getY(), 0.1f);
        assertEquals(0.534, vector.getZ(), 0.1f);
    }

    @Test
    public void normalizeX() {
        final Vector3D vector = new Vector3D(1, 0, 0);
        vector.normalize();
        assertEquals(1, vector.getX(), 0);
        assertEquals(0, vector.getY(), 0);
        assertEquals(0, vector.getZ(), 0);
    }

    @Test
    public void normalizeZ() {
        final Vector3D vector = new Vector3D(0, 1, 0);
        vector.normalize();
        assertEquals(0, vector.getX(), 0);
        assertEquals(1, vector.getY(), 0);
        assertEquals(0, vector.getZ(), 0);
    }

    @Test
    public void normalizeY() {
        final Vector3D vector = new Vector3D(0, 0, 1);
        vector.normalize();
        assertEquals(0, vector.getX(), 0);
        assertEquals(0, vector.getY(), 0);
        assertEquals(1, vector.getZ(), 0);
    }

    /*
     * Dot method
     */
    @Test
    public void dot () {
        final Vector3D vector = new Vector3D(4, 8, 10);
        final Vector3D vector2 = new Vector3D(9, 2, 7);
        final double dot = vector.dotProduct(vector2);
        assertEquals(122, dot, 0);
    }

    /*
     * Angle method
     */
    @Test
    public void angle0() {
        final Vector3D vector = new Vector3D(1, 0, 0);
        final Vector3D vector2 = new Vector3D(1, 0, 0);
        assertEquals(0, vector.angle(vector2), 0);
    }

    @Test
    public void angle90() {
        final Vector3D vector = new Vector3D(0, 0, 1);
        final Vector3D vector2 = new Vector3D(0, -1, 0);
        assertEquals(90, vector.angle(vector2), 0);
    }

    @Test
    public void angle180() {
        final Vector3D vector = new Vector3D(0, -1, 0);
        final Vector3D vector2 = new Vector3D(0, 1, 0);
        assertEquals(180, vector.angle(vector2), 0);
    }

    /*
     * Module method
     */
    @Test
    public void module() {
        final Vector3D vector = new Vector3D(1, 1, 1);
        assertEquals(Math.sqrt(3), vector.module(), 0);
    }

    @Test
    public void moduleX() {
        final Vector3D vector = new Vector3D(1, 0, 0);
        assertEquals(1, vector.module(), 0);
    }

    @Test
    public void moduleY() {
        final Vector3D vector = new Vector3D(0, 1, 0);
        assertEquals(1, vector.module(), 0);
    }

    @Test
    public void moduleZ() {
        final Vector3D vector = new Vector3D(0, 0, 1);
        assertEquals(1, vector.module(), 0);
    }
}
