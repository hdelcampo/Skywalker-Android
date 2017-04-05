package es.uva.tfg.hector.SkyWalkerApp.business;

import android.annotation.TargetApi;
import android.bluetooth.le.AdvertiseSettings;
import android.content.Context;
import android.os.Build;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;

/**
 * An iBeacon frames transmitter,
 * only valid on Lollipop or newer.
 * @author Héctor Del Campo Pando
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class iBeaconTransmitter {

    /**
     * iBeacon parser's layout.
     */
    private static final String IBEACON_LAYOUT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";

    /**
     * The beacon to transmit.
     */
    private Beacon beacon;

    /**
     * The transmitter itself.
     */
    private BeaconTransmitter beaconTransmitter;

    /**
     * Checks whether this device can be used as a transmitter or not.
     * @param context of the App.
     * @return true if can transmit, false otherwise.
     */
    public static boolean canTransmit(Context context) {
        return BeaconTransmitter.SUPPORTED == BeaconTransmitter.checkTransmissionSupported(context);
    }

    /**
     * Creates a new iBeacon transmitter.
     * @param uuid of the frame, must be 16 bytes long.
     * @param major of the frame, must be 2 bytes long.
     * @param minor of the frame, must be 2 bytes long.
     */
    public iBeaconTransmitter(Context context, String uuid, String major, String minor, byte txPower) {

        if (BeaconTransmitter.SUPPORTED != BeaconTransmitter.checkTransmissionSupported(context)) {
            throw new RuntimeException("This device cannot transmit BLE packets");
        }

        beacon = new Beacon.Builder()
                .setId1(uuid)
                .setId2(major)
                .setId3(minor)
                .setManufacturer(0x4c00)
                .setTxPower(txPower)
                .build();

        BeaconParser beaconParser = new BeaconParser()
                .setBeaconLayout(IBEACON_LAYOUT);

        beaconTransmitter = new BeaconTransmitter(context, beaconParser);
        beaconTransmitter.setAdvertiseTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM);
        beaconTransmitter.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);

    }

    /**
     * Starts the transmission.
     */
    public void startTransmission () {
        beaconTransmitter.startAdvertising(beacon);
    }

    /**
     * Stops the transmission.
     */
    public void stopTransmission () {
        beaconTransmitter.stopAdvertising();
    }

}
