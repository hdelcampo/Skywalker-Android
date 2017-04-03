package es.uva.tfg.hector.SkyWalkerApp.presentation;


import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import org.json.JSONException;
import org.json.JSONObject;

import es.uva.tfg.hector.SkyWalkerApp.R;
import es.uva.tfg.hector.SkyWalkerApp.persistence.ServerFacade;

/**
 * Fragment to handle QR UI connections.
 * @author Hector Del Campo Pando.
 */
@SuppressWarnings("MissingPermission")
public class QRConnectionFragment extends NewConnectionFragment {

    /**
     * Size for QR camera's preview.
     */
    private static final int WIDTH = 1024, HEIGHT = 768;

    /**
     * Error snackbar instance.
     */
    private Snackbar snackbar = null;

    /**
     * Connection status.
     */
    private boolean connecting = false;

    /**
     * Camera's preview controller.
     */
    private CameraPreview camera;

    /**
     * Camera's preview target.
     */
    private TextureView cameraPreview;

    /**
     * Barcode detector.
     */
    private BarcodeDetector barcodeDetector;

    /**
     * Thread that handles QR detection.
     */
    private QRDetectorThread detectorThread;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.qr_connection_layout, container, false);

        cameraPreview = (TextureView) rootView.findViewById(R.id.camera_view);
        camera = new CameraPreview(cameraPreview, getActivity(), WIDTH, HEIGHT);

        barcodeDetector = new BarcodeDetector.Builder(getContext())
                            .setBarcodeFormats(Barcode.QR_CODE)
                            .build();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        camera.start();
        detectorThread = new QRDetectorThread();
        detectorThread.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        camera.stop();
        try {
            detectorThread.interrupt();
            detectorThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            detectorThread = null;
        }
    }

    @Override
    void showError(ServerFacade.Errors error) {

        connecting = false;

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
     * Treats a QR content.
     * @param content to be treated.
     */
    private void treatQR(String content) {

        if (connecting) {
            return;
        }

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

    /**
     * Thread to handle QR detection,
     * it gets the camera's image,
     * checks for QR using a barcode detector and sleeps until next frame.
     */
    private class QRDetectorThread extends Thread {

        /**
         * Thread's sleeping time.
         */
        private static final long SLEEP_TIME = 1000 / 30; //1 sec / 30 fps

        /**
         * Indicator for thread state.
         */
        private volatile boolean running = true;

        @Override
        public void run() {
            while (running) {
                Bitmap bitmap = cameraPreview.getBitmap();
                if (bitmap == null) {
                    try {
                        sleep(SLEEP_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                Frame frame = new Frame.Builder()
                        .setBitmap(bitmap)
                        .build();

                SparseArray<Barcode> results = barcodeDetector.detect(frame);

                for (int i = 0; i < results.size(); i++) {
                    final String content = results.valueAt(i).displayValue;
                    if (isXtremeLocQR(content)) {
                        treatQR(content);
                    } else {
                        showError(ServerFacade.Errors.INVALID_QR);
                    }
                }

                try {
                    sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }

        @Override
        public void interrupt() {
            super.interrupt();
            running = false;
        }
    }
}
