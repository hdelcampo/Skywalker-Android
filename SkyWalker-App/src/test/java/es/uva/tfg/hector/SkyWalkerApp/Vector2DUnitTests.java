package es.uva.tfg.hector.SkyWalkerApp;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Vector2D class tests.
 * @author Héctor Del Campo Pando
 */
public class Vector2DUnitTests {

    private static final float ANGLE_DELTA = 2f;

    /*
     * Constructors
     */
    @Test
    public void constructor() {
        final double x = 1, y = 2;
        final Vector2D vector = new Vector2D(x, y);
        assertEquals(x, vector.getX(), 0);
        assertEquals(y, vector.getY(), 0);
    }

    /*
     * Normalize method
     */
    @Test
    public void normalize() {
        final Vector2D vector = new Vector2D(1, 1);
        vector.normalize();
        assertEquals(1/Math.sqrt(2), vector.getX(), 0);
        assertEquals(1/Math.sqrt(2), vector.getY(), 0);
    }

    /*
     * Dot product method
     */
    @Test
    public void dot () {
        final Vector2D vector = new Vector2D(2, -3);
        final Vector2D vector2 = new Vector2D(-4, 2);
        assertEquals(-14, vector.dotProduct(vector2), 0);
    }

    /*
     * Angle method
     */
    @Test
    public void angle0() {
        final Vector2D vector = new Vector2D(1, 0);
        final Vector2D vector2 = new Vector2D(1, 0);
        assertEquals(0, vector.angle(vector2), 0);
    }

    @Test
    public void angle90() {
        final Vector2D vector = new Vector2D(1, 0);
        final Vector2D vector2 = new Vector2D(0, -1);
        assertEquals(90, vector.angle(vector2), 0);
    }

    @Test
    public void angle180() {
        final Vector2D vector = new Vector2D(0, -1);
        final Vector2D vector2 = new Vector2D(0, 1);
        assertEquals(180, vector.angle(vector2), 0);
    }

    /*
     * Module method
     */
    @Test
    public void module() {
        final Vector2D vector = new Vector2D(1, 1);
        assertEquals(Math.sqrt(2), vector.module(), 0);
    }

    @Test
    public void moduleX() {
        final Vector2D vector = new Vector2D(1, 0);
        assertEquals(1, vector.module(), 0);
    }

    @Test
    public void moduleY() {
        final Vector2D vector = new Vector2D(0, 1);
        assertEquals(1, vector.module(), 0);
    }

    /*
     * Rotate method
     */
    @Test
    public void rotate90X() {
        final Vector2D vector = new Vector2D(1, 0);
        vector.rotateClockwise(90);
        assertEquals(0, vector.getX(), 0.001);
        assertEquals(-1, vector.getY(), 0.001);
    }

    @Test
    public void rotate180X() {
        final Vector2D vector = new Vector2D(1, 0);
        vector.rotateClockwise(180);
        assertEquals(-1, vector.getX(), 0.001);
        assertEquals(0, vector.getY(), 0.001);
    }

    @Test
    public void rotate270X() {
        final Vector2D vector = new Vector2D(1, 0);
        vector.rotateClockwise(270);
        assertEquals(0, vector.getX(), 0.001);
        assertEquals(1, vector.getY(), 0.001);
    }

    @Test
    public void rotatemin90X() {
        final Vector2D vector = new Vector2D(1, 0);
        vector.rotateClockwise(-90);
        assertEquals(0, vector.getX(), 0.001);
        assertEquals(1, vector.getY(), 0.001);
    }

    @Test
    public void rotatemin180X() {
        final Vector2D vector = new Vector2D(1, 0);
        vector.rotateClockwise(-180);
        assertEquals(-1, vector.getX(), 0.001);
        assertEquals(0, vector.getY(), 0.001);
    }

    @Test
    public void rotatemin270X() {
        final Vector2D vector = new Vector2D(1, 0);
        vector.rotateClockwise(-270);
        assertEquals(0, vector.getX(), 0.001);
        assertEquals(-1, vector.getY(), 0.001);
    }

    @Test
    public void rotate45Y() {
        final Vector2D vector = new Vector2D(0, 1);
        vector.rotateClockwise(45);
        assertEquals(1/Math.sqrt(2), vector.getX(), 0.001);
        assertEquals(1/Math.sqrt(2), vector.getY(), 0.001);
    }

    /*
     * Static part
     */
    @Test
    public void getAngle0() {
        double angle = Vector2D.getAngle(1, 0);
        assertEquals(0 , angle, ANGLE_DELTA);
    }

    @Test
    public void getAngle90() {
        double angle = Vector2D.getAngle(0, 1);
        assertEquals(90 , angle, ANGLE_DELTA);
    }

    @Test
    public void getAngle180() {
        double angle = Vector2D.getAngle(-1, 0);
        assertEquals(180 , angle, ANGLE_DELTA);
    }

    @Test
    public void getAngle270() {
        double angle = Vector2D.getAngle(0, -1);
        assertEquals(270 , angle, ANGLE_DELTA);
    }

    @Test
    public void getAngle45() {
        double angle = Vector2D.getAngle(1, 1);
        assertEquals(45 , angle, ANGLE_DELTA);
    }

    @Test
    public void getAngle135() {
        double angle = Vector2D.getAngle(-1, 1);
        assertEquals(135 , angle, ANGLE_DELTA);
    }

    @Test
    public void getAngle225() {
        double angle = Vector2D.getAngle(-1, -1);
        assertEquals(225 , angle, ANGLE_DELTA);
    }

    @Test
    public void getAngle315() {
        double angle = Vector2D.getAngle(1, -1);
        assertEquals(315 , angle, ANGLE_DELTA);
    }

}
