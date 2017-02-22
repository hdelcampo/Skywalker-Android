package es.uva.tfg.hector.SkyWalkerApp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Points to be shown on the augmented reality overlay.
 * This class also handles the served retrieved points.
 * @author Héctor Del Campo Pando
 */
public class PointOfInterest implements Parcelable {

    /**
     * The list of server retrieved points.
     */
    private static List<PointOfInterest> points;

    /**
     * Coordinates
     */
    private int x,
         y,
         z;

    /**
     * Id of the point
     */
    private final int id;

    /**
     * Name of the point.
     */
    private String name;

    /**
     * Sets the points the App has access to,
     * creating a new ArrayList and cloning all of its contents.
     * @param newPoints to set as accessible.
     */
    public static void setPoints(List<PointOfInterest> newPoints) {

        points = new ArrayList<>();

        for (PointOfInterest point : newPoints) {
            points.add(new PointOfInterest(point.getId(), point.getName()));
        }

    }

    /**
     * Retrieves a new list of all points,
     * the retrieved list is modification safe.
     * @return the list of points
     */
    public static List<PointOfInterest> getPoints() {

        List<PointOfInterest> returnPoints = new ArrayList<>();

        for (PointOfInterest point : points) {
            returnPoints.add(new PointOfInterest(point.getId(), point.getName()));
        }

        return returnPoints;

    }

    /**
     * Creates a new point of interest with standard coordinates (0, 0, 0).
     * @param id of the point of interest, server side.
     * @param name of the point of interest.
     */
    public PointOfInterest (int id, String name) {
        this.id = id;
        this.name = name;
        x = y = z = 0;
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

    /**
     * Retrieves the ID for the point.
     * @return the ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Retrieves the name for the point.
     * @return the name.
     */
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PointOfInterest that = (PointOfInterest) o;

        return id == that.getId();

    }

    @Override
    public String toString() {
        return name;
    }

    public static final Parcelable.Creator<PointOfInterest> CREATOR
            = new Parcelable.Creator<PointOfInterest>() {
        public PointOfInterest createFromParcel(Parcel in) {
            return new PointOfInterest(in);
        }

        public PointOfInterest[] newArray(int size) {
            return new PointOfInterest[size];
        }
    };

    private PointOfInterest(Parcel in) {
        id = in.readInt();
        name = in.readString();
        x = in.readInt();
        y = in.readInt();
        z = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(x);
        dest.writeInt(y);
        dest.writeInt(z);
    }

}
