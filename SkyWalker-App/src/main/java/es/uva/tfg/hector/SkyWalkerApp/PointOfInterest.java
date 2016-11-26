package es.uva.tfg.hector.SkyWalkerApp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hector Del Campo Pando on 14/07/2016.
 */
public class PointOfInterest implements Parcelable {

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PointOfInterest that = (PointOfInterest) o;

        return name.equals(that.name);

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
        dest.writeString(name);
        dest.writeInt(x);
        dest.writeInt(y);
        dest.writeInt(z);
    }

}
