package es.uva.tfg.hector.SkyWalkerApp.business;

import android.content.Context;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

import es.uva.tfg.hector.SkyWalkerApp.persistence.ServerFacade;
import es.uva.tfg.hector.SkyWalkerApp.services.PersistenceOperationDelegate;
import es.uva.tfg.hector.SkyWalkerApp.services.Vector2D;

/**
 * A real world center, equipped with indoor location.
 * @author Héctor Del Campo Pando.
 */
public class Center {

    /**
     * The center's id.
     */
    private final int id;

    /**
     * The center's scale
     */
    private final float scale;

    /**
     * The center's north in XtremeLoc coordinates.
     */
    private final Vector2D mapNorth;

    /**
     * The center's receivers.
     */
    private final SparseArray<MapPoint> receivers = new SparseArray<>();

    /**
     * The center's points.
     */
    private List<PointOfInterest> points;

    /**
     * Creates a new center.
     * @param id of the center.
     * @param scale center's real length.
     */
    public Center (int id, float scale) {
        this.scale = scale;
        this.id = id;
        mapNorth = new Vector2D(0, 1);
    }

    /**
     * Adds new receivers.
     * @param receivers to be added.
     */
    private void addReceivers(List<MapPoint> receivers) {

        for (MapPoint receiver: receivers) {
            this.receivers.put(receiver.getId(), receiver);
        }

    }

    /**
     * Sets the points the App has access to,
     * creating a new ArrayList and cloning all of its contents.
     * @param newPoints to set as accessible.
     */
    public void setPoints(List<PointOfInterest> newPoints) {

        points = new ArrayList<>(newPoints.size());

        for (PointOfInterest point : newPoints) {
            points.add(point.copy());
        }

    }

    /**
     * Retrieves the list of points.
     * @return the list of points.
     */
    public List<PointOfInterest> getPoints() {
        return points;
    }

    /**
     * Retrieves a receivier by its id.
     * @param id of the receiver.
     * @return the receiver, or null if there is none with the given id.
     */
    public MapPoint getReceiver(final int id) {
        return receivers.get(id);
    }

    /**
     * The center's id.
     * @return the id.
     */
    public int getId () {
        return id;
    }

    /**
     * Returns the map's north.
     * @return the map's north.
     */
    public Vector2D getMapNorth () {
        return mapNorth;
    }

    /**
     * Returns the real length.
     * @return the center's length in meters.
     */
    public float getScale() {
        return scale;
    }

    /**
     * Retrieves the center's receivers from the persistence system.
     * @param context to use.
     * @param delegate callback for success or error events.
     */
    public void loadReceivers (Context context, final PersistenceOperationDelegate delegate) {
        ServerFacade.getInstance(context).getCenterReceivers(new ServerFacade.OnServerResponse<List<MapPoint>>() {

            @Override
            public void onSuccess(List<MapPoint> receivers) {
                addReceivers(receivers);
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

        }, id);
    }

    /**
     * Retrieves the center's tags from the persistence system.
     * @param context to use.
     * @param delegate callback for success or error events.
     */
    public void loadTags (Context context, final PersistenceOperationDelegate delegate) {

        ServerFacade.getInstance(context).
                getAvailableTags(new ServerFacade.OnServerResponse<List<PointOfInterest>>() {

                    @Override
                    public void onSuccess(List<PointOfInterest> points) {

                        MapPoint userPoint = User.getInstance().getPosition();
                        //noinspection SuspiciousMethodCalls
                        points.remove(userPoint);

                        Center.this.points = points;

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

                });

    }
}
