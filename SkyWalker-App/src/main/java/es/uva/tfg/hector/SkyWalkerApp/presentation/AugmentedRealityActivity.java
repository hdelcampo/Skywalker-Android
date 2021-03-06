package es.uva.tfg.hector.SkyWalkerApp.presentation;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import java.util.List;

import es.uva.tfg.hector.SkyWalkerApp.R;
import es.uva.tfg.hector.SkyWalkerApp.business.PointOfInterest;
import es.uva.tfg.hector.SkyWalkerApp.business.User;

/**
 * Augmented reality activity
 * @author Héctor Del Campo Pando
 */
public class AugmentedRealityActivity extends FragmentActivity
        implements AugmentedRealityControlsFragment.AugmentedRealityControls {

    /**
     * Broadcast receiver for system events.
     */
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:

                    if (BluetoothAdapter.STATE_ON == intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)) {

                        User.getInstance().getTransmitter().startTransmission();

                    } else if (BluetoothAdapter.STATE_OFF == intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)) {

                        User.getInstance().getTransmitter().stopTransmission();

                        new AlertDialog.Builder(AugmentedRealityActivity.this)
                                .setTitle(R.string.bluetooth_disconnected_title)
                                .setMessage(R.string.bluetooth_disconnected_msg)
                                .setCancelable(false)
                                .setPositiveButton(R.string.bluetooth_enable, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
                                        adapter.enable();
                                    }
                                })
                                .setNegativeButton(R.string.disconnect, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        AugmentedRealityActivity.this.finish();
                                    }
                                })
                                .show();
                    }
                break;
            }
        }
    };

    /**
     * Broadcast receiver's filter.
     */
    private final IntentFilter intentFilter = new IntentFilter();

    {
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
    }

    @SuppressLint("InlinedApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.augmented_reality_activity_layout);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!User.getInstance().isDemo(this)) {
            registerReceiver(broadcastReceiver, intentFilter);
            User.getInstance().getTransmitter().startTransmission();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!User.getInstance().isDemo(this)) {
            User.getInstance().getTransmitter().stopTransmission();
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        User.getInstance().logout();
    }

    @Override
    public List<PointOfInterest> getActivePoints () {
        return ((AugmentedRealityFragment) getFragmentManager().findFragmentById(R.id.augmented_reality_fragment)).getActivePoints();
    }

    @Override
    public void setActivePoints (List<PointOfInterest> points) {
        ((AugmentedRealityFragment) getFragmentManager().findFragmentById(R.id.augmented_reality_fragment)).setActivePoints(points);
    }

}
