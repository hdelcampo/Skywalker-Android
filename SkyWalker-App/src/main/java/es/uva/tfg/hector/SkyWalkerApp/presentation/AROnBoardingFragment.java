package es.uva.tfg.hector.SkyWalkerApp.presentation;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.uva.tfg.hector.SkyWalkerApp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AROnBoardingFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.ar_on_boarding_page, container, false);
        return rootView;
    }

}
