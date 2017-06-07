package es.uva.tfg.hector.SkyWalkerApp.business;

import android.content.Context;

import es.uva.tfg.hector.SkyWalkerApp.persistence.ServerFacade;
import es.uva.tfg.hector.SkyWalkerApp.services.PersistenceOperationDelegate;

/**
 * A Map point, with coordinates, number of floor and ID.
 * @author Héctor Del Campo Pando
 */
public class MapPoint {

    /**
     * Coordinates on 3D world.
     */
    private float
            x,
            y;

    /**
     * Number of floor.
     */
    private int
            z;

    /**
     * Id of the point.
     */
    private final int id;

    /**
     * Creates a new Map point.
     * @param id of the point.
     * @param x coordinate.
     * @param y coordinate.
     * @param z number of floor.
     */
    public MapPoint(int id, float x, float y, int z) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Retrieves the X coordinate.
     * @return the X coordinate
     */
    public float getX() {
        return x;
    }

    /**
     * Changes the X coordinate.
     * @param x to set.
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Retrieves the Y coordinate.
     * @return the Y coordinate.
     */
    public float getY() {
        return y;
    }

    /**
     * Sets the Y coordinate.
     * @param y to set.
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Gets the Z coordinate.
     * @return the Z coordinate.
     */
    public int getZ() {
        return z;
    }

    /**
     * Changes the Z coordinate.
     * @param z to set.
     */
    public void setZ(int z) {
        this.z = z;
    }

    /**
     * Decides whether a point has a defined position or not.
     * @return true if point has no position, false otherwise.
     */
    public boolean isUndefined() {
        return (x == -1 && y == -1);
    }

    /**
     * Retrieves the ID for the point.
     * @return the ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Updates the position of the point, asking the persistence system the new one.
     * @param context to use.
     * @param delegate callback for success or error events.
     */
    public void updatePosition (Context context, final PersistenceOperationDelegate delegate) {
        ServerFacade.getInstance(context).
                getLastPosition(new ServerFacade.OnServerResponse<MapPoint>() {
                    @Override
                    public void onSuccess(MapPoint newPosition) {

                                setX(newPosition.getY());
                                setY(newPosition.getX());
                                setZ(newPosition.getZ());

                        if (null != delegate) {
                            delegate.onSuccess();
                        }

                    }

                    @Override
                    public void onError(ServerFacade.Errors error) {
                        PersistenceOperationDelegate.Errors errorToBack;

                        switch (error) {
                            case NO_CONNECTION: case TIME_OUT:
                                errorToBack = PersistenceOperationDelegate.Errors.INTERNET_ERROR;
                                break;
                            default:
                                errorToBack = PersistenceOperationDelegate.Errors.SERVER_ERROR;
                                break;
                        }

                        if (null != delegate) {
                            delegate.onError(errorToBack);
                        }
                    }
                }, this);
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || !(o instanceof MapPoint)) return false;

        MapPoint that = (MapPoint) o;

        return getId() == that.getId();

    }

}
