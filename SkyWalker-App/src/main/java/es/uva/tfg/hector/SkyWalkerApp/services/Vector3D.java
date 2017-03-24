package es.uva.tfg.hector.SkyWalkerApp.services;

/**
 * Geometric 3D vector.
 * @author Héctor Del Campo Pando
 */
public class Vector3D implements Vector<Vector3D> {

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

    @Override
    public void normalize () {
        final double length = Math.sqrt((x*x) + (y*y) + (z*z));
        x /= length;
        y /= length;
        z /= length;
    }

    @Override
    public double dotProduct (Vector3D v) {

        final double result = (x * v.getX())
                + (y * v.getY())
                + (z * v.getZ());

        return result;

    }

    @Override
    public double angle(Vector3D v) {
        final double product = dotProduct(v);
        final double angle = Math.acos(product/(module()*v.module()));
        return Math.toDegrees(angle);
    }

    @Override
    public double module() {
        return Math.sqrt((x*x) + (y*y) + (z*z));
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

}
