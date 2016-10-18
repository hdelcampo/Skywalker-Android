package es.uva.tfg.hector.SkyWalkerApp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;

/**
 * Created by Hector Del Campo Pando on 18/10/2016.
 */
public class ConnectionActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.connection_activity);
        FragmentTabHost tabHost = (FragmentTabHost)findViewById(R.id.tabHost);

        tabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        tabHost.addTab(tabHost.newTabSpec("QR").setIndicator(getString(R.string.QR)),
                QRConnectionFragment.class, null);

        tabHost.addTab(tabHost.newTabSpec("Manual").setIndicator(getString(R.string.Manual)),
                ManualConnectionFragment.class, null);
    }
}
