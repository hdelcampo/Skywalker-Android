package es.uva.tfg.hector.SkyWalkerApp.presentation;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.EditText;

import java.util.List;

import es.uva.tfg.hector.SkyWalkerApp.R;
import es.uva.tfg.hector.SkyWalkerApp.business.Center;
import es.uva.tfg.hector.SkyWalkerApp.business.MapPoint;
import es.uva.tfg.hector.SkyWalkerApp.business.PointOfInterest;
import es.uva.tfg.hector.SkyWalkerApp.business.Token;
import es.uva.tfg.hector.SkyWalkerApp.persistence.ServerFacade;

/**
 * Fragment to handle Manual UI connections.
 * @author Hector Del Campo Pando
 */
public class ManualConnectionFragment extends Fragment implements View.OnClickListener {

    /**
     * Address form validator,
     * this will show error and keep the ok button disabled until a valid URL has been inserted.
     */
    private TextWatcher addrValidator = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            final String url = editable.toString();
            toggleInvalidURL(!URLUtil.isValidUrl(url));

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.manual_connection_layout, container, false);

        rootView.findViewById(R.id.accept_button).setOnClickListener(this);
        rootView.findViewById(R.id.cancel_button).setOnClickListener(this);
        rootView.findViewById(R.id.demo_button).setOnClickListener(this);

        ((TextInputEditText) rootView.findViewById(R.id.addr_field)).addTextChangedListener(addrValidator);

        return rootView;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.accept_button:

                hideKeyboard();

                final String url = ((EditText) getView().findViewById(R.id.addr_field)).getText().toString();
                final String username = ((EditText) getView().findViewById(R.id.username_field)).getText().toString();
                final String password = ((EditText) getView().findViewById(R.id.password_field)).getText().toString();

                if (!URLUtil.isValidUrl(url)) {
                    toggleInvalidURL(true);
                    return;
                }

                newConnection(url, username, password);
                break;

            case R.id.cancel_button:
                getActivity().finish();
                break;
            case R.id.demo_button:
                PointOfInterest.setPoints(PointOfInterest.getDemoPoints());
                ServerFacade.getInstance(getActivity().getApplicationContext()).setDemo();
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

        toggleCredentialsError(false);

        final ProgressDialog dialog =
                ProgressDialog.show(getContext(), null, getString(R.string.connection_started), true, false);

        ServerFacade.getInstance(getActivity().getApplicationContext())
                .getToken(new ServerFacade.OnServerResponse<Token>() {

            @Override
            public void onSuccess(Token response) {
                retrieveReceivers(dialog);
            }

            @Override
            public void onError(ServerFacade.Errors error) {
                dialog.dismiss();
                showError(error);
            }

        }, url, login, password);

    }

    /**
     * Retrieves the receivers for the center.
     * @param dialog to control.
     */
    private void retrieveReceivers(final ProgressDialog dialog) {

        dialog.setMessage(getString(R.string.connection_recievers));

        Center.centers.add(new Center(0));

        ServerFacade.getInstance(getActivity().getApplicationContext())
                .getCenterReceivers(new ServerFacade.OnServerResponse<List<MapPoint>>() {

                    @Override
                    public void onSuccess(List<MapPoint> receivers) {
                        Center.centers.get(0).addReceivers(receivers);
                        retrieveTags(dialog);
                    }

                    @Override
                    public void onError(ServerFacade.Errors error) {
                        dialog.dismiss();
                        showError(error);
                    }

                }, Center.centers.get(0)); //TODO center real

    }


    /**
     * Retrieves the tags available for the connection token.
     * @param dialog to handle.
     */
    private void retrieveTags (final ProgressDialog dialog) {

        dialog.setMessage(getString(R.string.connection_tags));

        ServerFacade.getInstance(getActivity().getApplicationContext()).
                getAvailableTags(new ServerFacade.OnServerResponse<List<PointOfInterest>>() {

            @Override
            public void onSuccess(List<PointOfInterest> points) {
                PointOfInterest.setPoints(points);
                dialog.dismiss();
                startAR();
            }

            @Override
            public void onError(ServerFacade.Errors error) {
                dialog.dismiss();
                showError(error);
            }

        });

    }

    /**
     * Handles showing error to the user.
     * @param error to be shown.
     */
    private void showError(ServerFacade.Errors error) {
        switch (error) {
            case INVALID_URL:
                toggleInvalidURL(true);
                break;
            case NO_CONNECTION: case TIME_OUT:
                Snackbar.make(getView(), R.string.no_internet, Snackbar.LENGTH_LONG)
                        .setAction(R.string.retry, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                newConnection(
                                        ((EditText) getView().findViewById(R.id.addr_field)).getText().toString(),
                                        ((EditText) getView().findViewById(R.id.username_field)).getText().toString(),
                                        ((EditText) getView().findViewById(R.id.password_field)).getText().toString());
                            }
                        }).show();
                break;
            case INVALID_USERNAME_OR_PASSWORD:
                toggleCredentialsError(true);
                break;
            default:
                Snackbar.make(getView(), getString(R.string.server_bad_connection), Snackbar.LENGTH_LONG).show();
                break;
        }
    }

    /**
     * Hides the keyboard.
     */
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    /**
     * Toggles URL error messages.
     * @param state whether error must be shown or not.
     */
    private void toggleInvalidURL(boolean state) {

        final TextInputLayout addressLayout =
                (TextInputLayout) getView().findViewById(R.id.addr_container_layout);

        if (state) {
            addressLayout.setError(getText(R.string.invalid_url));
            getView().findViewById(R.id.accept_button).setEnabled(false);
        } else {
            addressLayout.setError(null);
            getView().findViewById(R.id.accept_button).setEnabled(true);
        }

    }

    /**
     * Toggles the credentials error messages.
     * @param state whether error must be shown or not.
     */
    private void toggleCredentialsError(boolean state) {

        final TextInputLayout usernameLayout =
                (TextInputLayout) getView().findViewById(R.id.password_container_layout);
        final TextInputLayout passwordLayout =
                (TextInputLayout) getView().findViewById(R.id.username_container_layout);

        if (state) {
            usernameLayout.setError(getText(R.string.invalid_login_data));
            passwordLayout.setError(getText(R.string.invalid_login_data));
        } else {
            usernameLayout.setError(null);
            passwordLayout.setError(null);
        }

    }
}
