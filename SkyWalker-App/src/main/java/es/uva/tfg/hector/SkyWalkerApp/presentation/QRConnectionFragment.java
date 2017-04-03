package es.uva.tfg.hector.SkyWalkerApp.presentation;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import es.uva.tfg.hector.SkyWalkerApp.R;
import es.uva.tfg.hector.SkyWalkerApp.persistence.ServerFacade;

/**
 * Fragment to handle QR UI connections.
 * @author Hector Del Campo Pando.
 */
@SuppressWarnings("MissingPermission")
public class QRConnectionFragment extends NewConnectionFragment {

    /**
     * Camera's preview holder.
     */
    private SurfaceHolder holder;

    /**
     * Error snackbar instance.
     */
    private Snackbar snackbar = null;

    /**
     * Camera device.
     */
    private CameraSource camera;

    /**
     * Surface state callback handler.
     */
    private final SurfaceHolder.Callback surfaceListener = new SurfaceHolder.Callback() {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {

            QRConnectionFragment.this.holder = holder;

            try {
                camera.start(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            QRConnectionFragment.this.holder = null;
            camera.stop();
        }

    };

    /**
     * QR detection handler.
     */
    private final Detector.Processor<Barcode> qrDetector = new Detector.Processor<Barcode>() {

        private boolean connecting = false;

        @Override
        public void release() {}

        @Override
        public void receiveDetections(Detector.Detections<Barcode> detections) {

            if (connecting) {
                return;
            }

            final SparseArray<Barcode> barcodes = detections.getDetectedItems();

            for (int i = 0; i < barcodes.size(); i++) {
                final String content = barcodes.valueAt(i).displayValue;
                if (isXtremeLocQR(content)) {

                    connecting = true;

                    new AsyncTask<String, Void, String[]>() {
                        @Override
                        protected String[] doInBackground(String... params) {
                            try {
                                String[] result = new String[3];

                                final JSONObject json = new JSONObject(params[0]);

                                result[0] = json.getString("url");

                                if (json.has("username")) {
                                    result[1] = json.getString("username");
                                }

                                if (json.has("password")) {
                                    result[2] = json.getString("password");
                                }

                                return result;
                            } catch (Exception e) {
                                showError(ServerFacade.Errors.INVALID_JSON);
                                e.printStackTrace();
                            }

                            return null;
                        }

                        @Override
                        protected void onPostExecute(String[] s) {
                            super.onPostExecute(s);
                            newConnection(s[0], s[1], s[2]);
                        }
                    }.execute(content);

                } else {
                    showError(ServerFacade.Errors.INVALID_QR);
                }
            }

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.qr_connection_layout, container, false);

        final SurfaceView cameraView = (SurfaceView) rootView.findViewById(R.id.camera_view);
        cameraView.getHolder().addCallback(surfaceListener);

        final BarcodeDetector barcodeDetector =
                    new BarcodeDetector.Builder(getContext())
                            .setBarcodeFormats(Barcode.QR_CODE)
                            .build();

        barcodeDetector.setProcessor(qrDetector);

        camera = new CameraSource
                .Builder(getContext(), barcodeDetector)
                .setAutoFocusEnabled(true)
                .build();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (holder != null) {
            try {
                camera.start(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        camera.stop();
    }

    @Override
    void showError(ServerFacade.Errors error) {

        switch (error) {
            case INVALID_QR:

                if(snackbar != null) {
                    return;
                }

                snackbar = Snackbar.make(getView(), getString(R.string.invalid_qr), Snackbar.LENGTH_LONG);
                snackbar.addCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                snackbar = null;
                            }
                        });
                snackbar.show();
                break;
            case INVALID_URL:
                Snackbar.make(getView(), getString(R.string.invalid_qr), Snackbar.LENGTH_LONG).show();
                break;
            case INVALID_USERNAME_OR_PASSWORD:
                Snackbar.make(getView(), getString(R.string.invalid_login_data), Snackbar.LENGTH_LONG).show();
                break;
            default:
                Snackbar.make(getView(), getString(R.string.server_bad_connection), Snackbar.LENGTH_LONG).show();
                break;
        }

    }

    /**
     * Checks wheter a QR code is XtremeLoc schemed or not.
     * @param qr to check.
     * @return true if XtremeLoc scheme, false otherwise.
     */
    private boolean isXtremeLocQR(final String qr) {

        if (qr == null || qr.isEmpty()) {
            return false;
        }

        try {
            final JSONObject json = new JSONObject(qr);
            if (!json.has("scheme") && json.has("url")) return false;
            if (json.getString("scheme").equals("XtremeLoc")) return true;
        } catch (JSONException e) {
            return false;
        }

        return false;
    }

}
