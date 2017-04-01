package es.uva.tfg.hector.SkyWalkerApp.presentation;


import android.os.AsyncTask;
import android.os.Bundle;
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
public class QRConnectionFragment extends NewConnectionFragment {

    /**
     * Camera device.
     */
    private CameraSource camera;

    /**
     * Surface state callback handler.
     */
    private final SurfaceHolder.Callback surfaceListener = new SurfaceHolder.Callback() {

        @SuppressWarnings("MissingPermission")
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
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

            for (int i = 0; i < barcodes.size(); i++){
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
                .setRequestedPreviewSize(640, 480)
                .build();

        return rootView;
    }

    @Override
    void showError(ServerFacade.Errors error) {

    }

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
