package es.uva.tfg.hector.SkyWalkerApp.business;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Points to be shown on the augmented reality overlay.
 * This class also handles the served retrieved points.
 * @author Héctor Del Campo Pando
 */
public class PointOfInterest extends MapPoint implements Parcelable {

    /**
     * The list of server retrieved points.
     */
    private static List<PointOfInterest> points;

    /**
     * Name of the point.
     */
    private String name;

    /**
     * Retrieves a list of points for demo purposes.
     * @return the list of demo points.
     */
    public static List<PointOfInterest> getDemoPoints () {
        List<PointOfInterest> points = new ArrayList<>();
        points.add(new PointOfInterest(0, "Dani"));
        points.get(0).setX(1);
        points.get(0).setY(1);
        points.get(0).setZ(0);

        //points.add(new PointOfInterest(1, "Diego"));
        //points.get(1).setX(-0.25);
        //points.get(1).setY(1);

        return points;
    }

    /**
     * Sets the points the App has access to,
     * creating a new ArrayList and cloning all of its contents.
     * @param newPoints to set as accessible.
     */
    public static void setPoints(List<PointOfInterest> newPoints) {

        points = new ArrayList<>();

        for (PointOfInterest point : newPoints) {
            points.add(point.copy());
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
            returnPoints.add(point.copy());
        }

        return returnPoints;

    }

    /**
     * Creates a new point of interest with standard coordinates (-1, -1, -1).
     * @param id of the point of interest, server side.
     * @param name of the point of interest.
     */
    public PointOfInterest (int id, String name) {
        super(id, -1, -1, -1);
        this.name = name;
    }

    /**
     * Retrieves the name for the point.
     * @return the name.
     */
    public String getName() {
        return name;
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
        super(in.readInt(),
                in.readFloat(),
                in.readFloat(),
                in.readInt());
        name = in.readString();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(getId());
        dest.writeFloat(getX());
        dest.writeFloat(getY());
        dest.writeInt(getZ());
        dest.writeString(name);
    }

    private PointOfInterest copy() {
        PointOfInterest newPoint = new PointOfInterest(this.getId(), this.getName());
        newPoint.setX(this.getX());
        newPoint.setY(this.getY());
        newPoint.setZ(this.getZ());
        return newPoint;
    }
}
