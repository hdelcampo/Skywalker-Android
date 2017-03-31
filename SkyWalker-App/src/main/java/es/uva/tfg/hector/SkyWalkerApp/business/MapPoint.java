package es.uva.tfg.hector.SkyWalkerApp.business;

/**
 * A Map point, with coordinates, number of floor and ID.
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

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

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

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || !(o instanceof MapPoint)) return false;

        MapPoint that = (MapPoint) o;

        return getId() == that.getId();

    }

}
