package es.uva.tfg.hector.SkyWalkerApp;

/**
 * A real world center, equiped with indoor location.
 * @author Héctor Del Campo Pando.
 */
public class Center {

    private Vector2D mapNorth = new Vector2D(1, -1);

    private static Center instance = new Center();

    public static Center getInstance () {
        return instance;
    }

    public Vector2D getMapNorth () {
        return mapNorth;
    }
}
