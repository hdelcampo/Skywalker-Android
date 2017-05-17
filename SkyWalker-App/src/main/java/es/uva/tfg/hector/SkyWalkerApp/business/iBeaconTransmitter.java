package es.uva.tfg.hector.SkyWalkerApp.business;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;

/**
 * An iBeacon frames transmitter, as singleton;
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
     * The data to transmit.
     */
    private iBeaconFrame frame;

    /**
     * Checks whether this device can be used as a transmitter or not.
     * @param context of the App.
     * @return true if can transmit, false otherwise.
     */
    public static boolean canTransmit(Context context) {
        return BeaconTransmitter.SUPPORTED == BeaconTransmitter.checkTransmissionSupported(context);
    }

    /**
     * Checks whether bluetooth is enabled or not.
     * @return true if bluetooth is enabled, false otherwise.
     */
    public static boolean isBluetoothEnabled() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        return adapter.isEnabled();
    }

    /**
     * Enables bluetooth, and executes callback.
     * If bluetooth is already enable it does nothing.
     * @param context of the activity.
     * @param callback to execute, can be null if nothing has to be executed.
     */
    public static void enableBluetooth (Context context, final OnEventCallback callback) {

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        if (adapter.isEnabled()) {
            return;
        }

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case BluetoothAdapter.ACTION_STATE_CHANGED:

                        if (BluetoothAdapter.STATE_ON == intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)) {
                            if (null != callback) {
                                callback.onEnabled();
                            }
                            context.unregisterReceiver(this);
                        }
                        break;
                }
            }
        };

        context.registerReceiver(broadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        adapter.enable();

    }

    /**
     * Configures a new iBeacon transmitter.
     * @param context of the App.
     */
    public void init(Context context) {

        if (!canTransmit(context)) {
            throw new RuntimeException("Cannot transmit iBeacon frames");
        }

        BeaconParser beaconParser = new BeaconParser()
                .setBeaconLayout(IBEACON_LAYOUT);

        beaconTransmitter = new BeaconTransmitter(context, beaconParser);
        beaconTransmitter.setAdvertiseTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM);
        beaconTransmitter.setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED);

    }

    /**
     * Set ups the data to be transmitted, note that for this to take effect,
     * any ongoing transmission must be stopped before configuring.
     * @param frame to transmit.
     * @param txPower to transmit.
     */
    public void configure(iBeaconFrame frame, byte txPower) {

        this.frame = frame;

        beacon = new Beacon.Builder()
                .setId1(frame.getUUID())
                .setId2(String.valueOf(frame.getMajor()))
                .setId3(String.valueOf(frame.getMinor()))
                .setManufacturer(0x4c00)
                .setTxPower(txPower)
                .build();

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

    /**
     * Interface for Bluetooth events callbacks.
     */
    public interface OnEventCallback {

        void onEnabled ();
    }

}
