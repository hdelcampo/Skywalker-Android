package es.uva.tfg.hector.SkyWalkerApp.presentation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import es.uva.tfg.hector.SkyWalkerApp.R;

/**
 * Enter point for the App, after the splash one.
 * @author Hector Del Campo Pando
 */
public class EntryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_DeviceDefault_Light_NoActionBar);
        setContentView(R.layout.entry_layout);
    }
}
