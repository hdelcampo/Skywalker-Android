package es.uva.tfg.hector.SkyWalkerApp.services;

/**
 * Geometric 3D vector.
 * @author Héctor Del Campo Pando
 */
public class Vector3D {

    /**
     * Vector components.
     */
    private double x, y, z;

    /**
     * Constructs a new 3D vector.
     * @param x component of the vector.
     * @param y component of the vector.
     * @param z component of the vector.
     */
    public Vector3D (double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Normalizes the vector.
     */
    public void normalize () {
        final double length = Math.sqrt((x*x) + (y*y) + (z*z));
        x /= length;
        y /= length;
        z /= length;
    }

    /**
     * Geometric's dot product.
     * @param v other vector.
     * @return the dot product result.
     */
    public double dotProduct (Vector3D v) {

        return (x * v.getX())
                + (y * v.getY())
                + (z * v.getZ());

    }

    /**
     * Returns the inner angle between vectors
     * @param v other vector.
     * @return the inner angle in degrees.
     */
    public double angle(Vector3D v) {
        final double product = dotProduct(v);
        final double angle = Math.acos(product/(module()*v.module()));
        return Math.toDegrees(angle);
    }

    /**
     * Returns the vector's length.
     * @return vector's length.
     */
    public double module() {
        return Math.sqrt((x*x) + (y*y) + (z*z));
    }

    /**
     * Returns the X value.
     * @return the X value.
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the Y value.
     * @return the Y value.
     */
    public double getY() {
        return y;
    }

    /**
     * Returns the Z value.
     * @return the Z value.
     */
    public double getZ() {
        return z;
    }

}
