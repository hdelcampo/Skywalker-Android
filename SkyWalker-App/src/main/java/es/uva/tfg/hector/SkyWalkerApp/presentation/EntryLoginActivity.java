package es.uva.tfg.hector.SkyWalkerApp.presentation;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import es.uva.tfg.hector.SkyWalkerApp.R;

public class EntryLoginActivity extends AppCompatActivity {

    /**
     * Permissions IDs.
     */
    private static final int APP_PERMISSIONS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_login);

        checkPermissions();

    }

    /**
     * Checks App granted permissions.
     */
    private void checkPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    APP_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        switch (requestCode) {
            case APP_PERMISSIONS: {
                if (grantResults.length == 0
                        || grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Snackbar.make(findViewById(R.id.container), getString(R.string.needs_camera_permissions), Snackbar.LENGTH_LONG).show();
                    checkPermissions();
                }
            }
        }
    }
}
