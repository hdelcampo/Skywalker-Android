package es.uva.tfg.hector.SkyWalkerApp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Entry point for App showing a splash screen.
 * @author Hector Del Campo Pando
 */
public class SplashActivity extends AppCompatActivity{

    /**
     * Constant indicating for how long screen will be shown.
     */
    private static final long SPLASH_SCREEN_TIME = 750;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());

        try {
            Thread.sleep(SPLASH_SCREEN_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent (this, EntryActivity.class);
        startActivity(intent);
        finish();

    }

}