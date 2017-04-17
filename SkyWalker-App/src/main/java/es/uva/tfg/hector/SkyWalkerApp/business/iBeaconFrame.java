package es.uva.tfg.hector.SkyWalkerApp.business;

/**
 * iBeacon frame data.
 * @author Héctor Del Campo Pando.
 */
public class iBeaconFrame {

    /**
     * UUID of the frame.
     */
    private final String UUID;

    /**
     * Major of the frame.
     */
    private final int major;

    /**
     * Minor of the frame.
     */
    private final int minor;

    /**
     * Constructs a new iBeacon frame.
     * @param UUID of the frame.
     * @param major of the frame.
     * @param minor of the frame.
     */
    public iBeaconFrame(String UUID, int major, int minor) {
        this.UUID = UUID;
        this.major = major;
        this.minor = minor;
    }

    /**
     * Retrieves the UUID.
     * @return the UUID.
     */
    public String getUUID() {
        return UUID;
    }

    /**
     * Retrieves the major.
     * @return the major.
     */
    public int getMajor() {
        return major;
    }

    /**
     * Retrieves the minor.
     * @return the minor.
     */
    public int getMinor() {
        return minor;
    }

}
