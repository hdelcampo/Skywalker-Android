package es.uva.tfg.hector.SkyWalkerApp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Enter point for the App, after the splash one.
 * @author Hector Del Campo Pando
 */
public class EntryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_layout);
    }
}
