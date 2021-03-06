package es.uva.tfg.hector.SkyWalkerApp.business;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Points to be shown on the augmented reality overlay.
 * @author Héctor Del Campo Pando
 */
public class PointOfInterest extends MapPoint implements Parcelable {

    /**
     * Name of the point.
     */
    private final String name;

    /**
     * Retrieves a list of points for demo purposes.
     * @return the list of demo points.
     */
    public static List<PointOfInterest> getDemoPoints () {

        List<PointOfInterest> points = new ArrayList<>();

        points.add(new PointOfInterest(0, "Dani"));
        points.get(0).setY(0.5f);
        points.get(0).setX(0);
        points.get(0).setZ(0);

        points.add(new PointOfInterest(1, "Diego"));
        points.get(1).setY(0);
        points.get(1).setX(0.5f);
        points.get(1).setZ(0);

        points.add(new PointOfInterest(2, "Ana"));
        points.get(2).setY(1);
        points.get(2).setX(0.5f);
        points.get(2).setZ(0);

        points.add(new PointOfInterest(3, "Sergio"));
        points.get(3).setY(0.5f);
        points.get(3).setX(1);
        points.get(3).setZ(0);

        return points;
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
        return name + " (" + getId() + ")";
    }

    /**
     * Parcelable creator
     */
    public static final Parcelable.Creator<PointOfInterest> CREATOR
            = new Parcelable.Creator<PointOfInterest>() {
        public PointOfInterest createFromParcel(Parcel in) {
            return new PointOfInterest(in);
        }

        public PointOfInterest[] newArray(int size) {
            return new PointOfInterest[size];
        }
    };

    /**
     * Constructor for parcelable calls.
     * @param in old values to use.
     */
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

    /**
     * Creates an exact copy of this.
     * @return the copy of this.
     */
    PointOfInterest copy() {
        PointOfInterest newPoint = new PointOfInterest(this.getId(), this.getName());
        newPoint.setX(this.getX());
        newPoint.setY(this.getY());
        newPoint.setZ(this.getZ());
        return newPoint;
    }
}
