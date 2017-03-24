package es.uva.tfg.hector.SkyWalkerApp.services;

/**
 * Interface for geometric vectors.
 */
public interface Vector <T extends Vector> {

    /**
     * Normalizes the vector.
     */
    void normalize ();

    /**
     * Mathematical vector's dot product.
     * @param v other multiplier vector.
     * @return the dot product result.
     */
    double dotProduct (T v);

    /**
     * Finds the inner angle between vectors.
     * @param v other vector.
     * @return the angle in degrees.
     */
    double angle (T v);

    /**
     * Finds the vector's length.
     * @return the vector's length.
     */
    double module ();

}
