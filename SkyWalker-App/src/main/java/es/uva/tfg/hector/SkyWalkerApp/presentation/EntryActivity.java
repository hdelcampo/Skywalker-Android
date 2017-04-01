package es.uva.tfg.hector.SkyWalkerApp.presentation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;

import es.uva.tfg.hector.SkyWalkerApp.R;
import io.fabric.sdk.android.Fabric;

/**
 * Enter point for the App, after the splash one.
 * @author Hector Del Campo Pando
 */
public class EntryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setTheme(android.R.style.Theme_DeviceDefault_Light_NoActionBar);
        setContentView(R.layout.entry_layout);
    }
}
