package es.uva.tfg.hector.SkyWalkerApp;

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
     * Stub for direction in angles.
     */
    private float direction;
    private int modifier;

    /**
     * Creates a new point of interest in a 3 axes plane.
     * @param x
     * @param y
     * @param z
     */
    public PointOfInterest(int x, int y, int z){
        this.x = x;
        this.y = y;
        this.z = z;
        direction = 0;
        modifier = 1;
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
     * Gets direction of the point. </br>
     * THIS IS A STUB FOR NOW!
     * @return direction angle.
     */
    public float getDirection(){
        //TODO stub for demo
        float value = (float) ((System.currentTimeMillis()*0.1) % 140);
        if (value >= 70) return 140 - value - 25 ;
        else return value - 25;
    }
}
