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

    private final int id;

    private final float scale = 128;

    private final Vector2D mapNorth;

    private final SparseArray<MapPoint> receivers = new SparseArray<>();

    private List<PointOfInterest> points;

    public Center (int id) {
        this.id = id;
        mapNorth = new Vector2D(0, 1);
    }

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

    public List<PointOfInterest> getPoints() {
        return points;
    }

    public MapPoint getReceiver(final int id) {
        return receivers.get(id);
    }

    public int getId () {
        return id;
    }

    public Vector2D getMapNorth () {
        return mapNorth;
    }

    public float getScale() {
        return scale;
    }

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

        }, this);
    }

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
