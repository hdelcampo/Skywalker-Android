package es.uva.tfg.hector.SkyWalkerApp.presentation;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import es.uva.tfg.hector.SkyWalkerApp.R;
import es.uva.tfg.hector.SkyWalkerApp.business.PointOfInterest;
import es.uva.tfg.hector.SkyWalkerApp.business.Token;
import es.uva.tfg.hector.SkyWalkerApp.persistence.ServerFacade;

/**
 * Fragment to handle Manual UI connections.
 * @author Hector Del Campo Pando
 */
public class ManualConnectionFragment extends Fragment implements View.OnClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.manual_connection_layout, container, false);

        rootView.findViewById(R.id.accept_button).setOnClickListener(this);
        rootView.findViewById(R.id.cancel_button).setOnClickListener(this);
        rootView.findViewById(R.id.demo_button).setOnClickListener(this);

        return rootView;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.accept_button:

                final String url = ((EditText) getView().findViewById(R.id.addr_field)).getText().toString();
                final String username = ((EditText) getView().findViewById(R.id.username_field)).getText().toString();
                final String password = ((EditText) getView().findViewById(R.id.password_field)).getText().toString();

                newConnection(url, username, password);
                break;

            case R.id.cancel_button:
                getActivity().finish();
                break;
            case R.id.demo_button:
                PointOfInterest.setPoints(PointOfInterest.getDemoPoints());
                startAR();
                break;
        }
    }


    /**
     * Starts the Augmented Reality interface on a new activity if caller is not a dialog.
     */
    private void startAR() {

        if (getActivity() instanceof EntryActivity) {
            Intent intent = new Intent(getContext(), AugmentedRealityActivity.class);
            startActivity(intent);
        }

        getActivity().finish();

    }

    /**
     * Establishes a new connection to the given server.
     * @param url of the server, must be a valid URL,
     *            otherwise function will return and a message will be shown to the user.
     * @param login of the client
     * @param password of the client
     */
    private void newConnection(final String url, final String login, final String password) {

        if (!URLUtil.isValidUrl(url)) {
            Toast.makeText(getContext(), getString(R.string.invalid_url), Toast.LENGTH_LONG).show();
            return;
        }

        final ProgressDialog dialog =
                ProgressDialog.show(getContext(), null, getString(R.string.connection_in_progress), true, false);

        ServerFacade.getInstance(getActivity().getApplicationContext())
                .getToken(new ServerFacade.OnServerResponse<Token>() {

            @Override
            public void onSuccess(Token response) {
                retrieveTags(dialog);
            }

            @Override
            public void onError(ServerFacade.Errors error) {
                dialog.dismiss();
                Toast.makeText(getContext(), getString(R.string.invalid_login_data), Toast.LENGTH_LONG).show();
            }

        }, url, login, password);

    }

    /**
     * Retrieves the tags avaliable for the connection token.
     * @param dialog to handle.
     */
    private void retrieveTags (final ProgressDialog dialog) {

        ServerFacade.getInstance(getActivity().getApplicationContext()).
                getAvaliableTags(new ServerFacade.OnServerResponse<List<PointOfInterest>>() {
            @Override
            public void onSuccess(List<PointOfInterest> response) {
                PointOfInterest.setPoints(response);
                dialog.dismiss();
                startAR();
            }

            @Override
            public void onError(ServerFacade.Errors error) {
                dialog.dismiss();
                Toast.makeText(getContext(), getString(R.string.server_bad_connection), Toast.LENGTH_LONG).show();
            }
        });

    }
}
