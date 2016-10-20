package es.uva.tfg.hector.SkyWalkerApp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hector Del Campo Pando on 14/07/2016.
 */
public class PointOfInterest {

    /**
     * Coordinates
     */
    private int x,
         y,
         z;

    /**
     * Name of the point.
     */
    private String name;


    /**
     * Retrieves a list of all points
     * @return the list of points
     */
    public static List<PointOfInterest> getPoints() {
        //TODO stub
        List<PointOfInterest> points = new ArrayList<>();
        points.add(new PointOfInterest("Wally", 0, 0, 0));    //TODO demo
        points.add(new PointOfInterest("Robin", 135, 0, 45));
        return points;
    }

    /**
     * Creates a new point of interest in a 3 axes plane.
     * @param x
     * @param y
     * @param z
     */
    public PointOfInterest(String name, int x, int y, int z){
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }


    public String getID() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof PointOfInterest)) {
            return false;
        }
        return getID().equals(((PointOfInterest)obj).getID());
    }
}
