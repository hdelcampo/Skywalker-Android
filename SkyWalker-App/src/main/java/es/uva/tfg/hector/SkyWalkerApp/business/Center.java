package es.uva.tfg.hector.SkyWalkerApp.business;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

import es.uva.tfg.hector.SkyWalkerApp.services.Vector2D;

/**
 * A real world center, equipped with indoor location.
 * @author Héctor Del Campo Pando.
 */
public class Center {

    private final int id;

    private final Vector2D mapNorth;

    private final SparseArray<MapPoint> receivers = new SparseArray<>();

    public static List<Center> centers = new ArrayList<>();

    public Center (int id) {
        this.id = id;
        mapNorth = new Vector2D(0, 1);
        centers.add(this);
    }

    public void addReceivers(List<MapPoint> receivers) {

        for (MapPoint receiver: receivers) {
            this.receivers.put(receiver.getId(), receiver);
        }

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
}
