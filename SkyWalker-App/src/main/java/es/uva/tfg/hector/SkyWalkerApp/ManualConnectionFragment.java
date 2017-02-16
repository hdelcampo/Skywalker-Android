package es.uva.tfg.hector.SkyWalkerApp;


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

        return rootView;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.accept_button:

                final String uri = ((EditText) getView().findViewById(R.id.addr_field)).getText().toString();
                final String username = ((EditText) getView().findViewById(R.id.username_field)).getText().toString();
                final String password = ((EditText) getView().findViewById(R.id.password_field)).getText().toString();

                if (uri.equals("demo")) {
                    startAR();
                } else {
                    newConnection (uri, username, password);
                }

                break;

            case R.id.cancel_button:
                getActivity().finish();
                break;
        }
    }


    /**
     * Starts the Augmented Reality interface on a new activity if caller is not a dialog.
     */
    private void startAR() {

        if (getActivity() instanceof MainActivity) {
            Intent intent = new Intent(getContext(), AugmentedRealityActivity.class);
            startActivity(intent);
        }

        getActivity().finish();

    }

    /**
     * Establishes a new connection to the given server.
     * @param uri of the server, must be a valid URI,
     *            otherwise function will return and a message will be shown to the user.
     * @param login of the client
     * @param password of the client
     */
    private void newConnection(final String uri, final String login, final String password) {

        if (!URLUtil.isValidUrl(uri)) {
            Toast.makeText(getContext(), getString(R.string.invalid_url), Toast.LENGTH_LONG).show();
            return;
        }

        final ProgressDialog dialog =
                ProgressDialog.show(getContext(), null, getString(R.string.connection_in_progress), true, false);

        ServerHandler.getToken(getContext(), new ServerHandler.OnServerResponse() {

            @Override
            public void onSuccess(String response) {
                dialog.dismiss();
                Token.getInstance().setToken(response);
                startAR();
            }

            @Override
            public void onError(ServerHandler.Errors error) {
                dialog.dismiss();
                Toast.makeText(getContext(), getString(R.string.invalid_login_data), Toast.LENGTH_LONG).show();
            }
        }, uri, login, password);

    }
}
