package es.uva.tfg.hector.SkyWalkerApp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;

/**
 * Activity to handle new connections requests
 * @author RDNest
 */
public class NewConnectionActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tabs_connection_layout);
        FragmentTabHost tabHost = (FragmentTabHost)findViewById(R.id.tabHost);

        tabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        tabHost.addTab(tabHost.newTabSpec("QR").setIndicator(getString(R.string.QR)),
                QRConnectionFragment.class, null);

        tabHost.addTab(tabHost.newTabSpec("Manual").setIndicator(getString(R.string.Manual)),
                ManualConnectionFragment.class, null);
    }

}
