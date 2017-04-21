package es.uva.tfg.hector.SkyWalkerApp.presentation;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import es.uva.tfg.hector.SkyWalkerApp.R;

public class PermissionsRequestActivity extends AppCompatActivity {

    /**
     * Permissions IDs.
     */
    private static final int APP_PERMISSIONS = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permissions_request_layout);
    }

    public void askPermissions (View v) {

        List<String> permissions = new ArrayList();

         if (PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)) {
            permissions.add(Manifest.permission.CAMERA);
        }

        if (PackageManager.PERMISSION_DENIED == ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        ActivityCompat.requestPermissions(this, permissions.toArray(new String[permissions.size()]), APP_PERMISSIONS);

    }

    /**
     * Checks App granted permissions.
     */
    public static boolean hasEnoughPermissions(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case APP_PERMISSIONS: {
                if (grantResults.length == 0) {
                    Snackbar.make(findViewById(R.id.container), getString(R.string.needs_permissions), Snackbar.LENGTH_LONG).show();
                    break;
                } else {
                    for (int permission: grantResults) {
                        if ( PackageManager.PERMISSION_DENIED == permission) {
                            Snackbar.make(findViewById(R.id.container), getString(R.string.needs_permissions), Snackbar.LENGTH_LONG).show();
                            return;
                        }
                    }

                    Intent intent = new Intent(this, NewConnectionActivity.class);
                    startActivity(intent);
                    finish();

                }
            }
        }
    }
}
