package es.uva.tfg.hector.SkyWalkerApp.services;

/**
 * Geometric 2D vector.
 * @author Héctor Del Campo Pando
 */
public class Vector2D implements Vector<Vector2D> {

    /**
     * Vector's components.
     */
    private double x, y;

    /**
     * Retrieves angle in degrees, values from [0, 360).
     * @param x the abscissa coordinate.
     * @param y the ordinate coordinate.
     * @return the angle
     */
    public static double getAngle(double x, double y){
        double angle = Math.toDegrees(Math.atan2(y, x));

        if (angle < 0) {
            // Sum complete circle
            angle += 360;
        }

        return angle;
    }

    /**
     * Constructs a new vector in 2D.
     * @param x component of the vector.
     * @param y component of the vector.
     */
    public Vector2D (double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void normalize() {
        final double length = module();
        x /= length;
        y /= length;
    }

    @Override
    public double dotProduct(Vector2D v) {
        return (x*v.getX()) + (y*v.getY());
    }

    @Override
    public double angle(Vector2D v) {
        final double product = dotProduct(v);
        final double cos = product/(module()*v.module());
        return Math.toDegrees(Math.acos(cos));
    }

    /**
     * Retrieves vector's inner angle with sign.
     * @param v other vector.
     * @return the inner angle in degrees,
     * negative values means parameter is counter clockwise,
     * positive values means clockwise.
     */
    public double angleWithSign(Vector2D v) {
        final double cos = dotProduct(v);
        final double det = x*v.getY() - v.getX()*y;
        return -Math.toDegrees(Math.atan2(det, cos));
    }

    @Override
    public double module() {
        return Math.sqrt((x*x) + (y*y));
    }

    /**
     * Rotates the vector clockwise conserving its module.
     * @param degrees to be rotated.
     */
    public void rotateClockwise (double degrees) {

        final double rad = -Math.toRadians(degrees);
        final double cos = Math.cos(rad);
        final double sin = Math.sin(rad);

        final double newX = x * cos - y * sin;
        final double newY = x * sin + y * cos;

        x = newX;
        y = newY;

    }

    /**
     * Retrieves the X component.
     * @return the X component.
     */
    public double getX() {
        return x;
    }

    /**
     * Retrieves the Y component.
     * @return the Y component.
     */
    public double getY() {
        return y;
    }
}
