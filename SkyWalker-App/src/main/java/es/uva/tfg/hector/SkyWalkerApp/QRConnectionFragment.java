package es.uva.tfg.hector.SkyWalkerApp;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fragment to handle QR UI connections
 * @author Hector Del Campo Pando
 */
public class QRConnectionFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.qr_connection_layout, container, false);
    }

}
