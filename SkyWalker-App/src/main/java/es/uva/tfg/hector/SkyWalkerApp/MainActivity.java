package es.uva.tfg.hector.SkyWalkerApp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Entry point for App
 * @author Hector Del Campo Pando
 */
public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_layout);

        Fabric.with(this, new Crashlytics());

    }

}