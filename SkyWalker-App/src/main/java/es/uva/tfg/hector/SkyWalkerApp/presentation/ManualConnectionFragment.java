package es.uva.tfg.hector.SkyWalkerApp.presentation;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.EditText;

import es.uva.tfg.hector.SkyWalkerApp.R;
import es.uva.tfg.hector.SkyWalkerApp.business.Center;
import es.uva.tfg.hector.SkyWalkerApp.business.PointOfInterest;
import es.uva.tfg.hector.SkyWalkerApp.business.User;
import es.uva.tfg.hector.SkyWalkerApp.services.PersistenceOperationDelegate;

/**
 * Fragment to handle Manual UI connections.
 * @author Hector Del Campo Pando
 */
public class ManualConnectionFragment extends NewConnectionFragment implements View.OnClickListener {

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
        rootView.findViewById(R.id.demo_button).setOnClickListener(this);

        ((TextInputEditText) rootView.findViewById(R.id.addr_field)).addTextChangedListener(addrValidator);

        return rootView;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.accept_button:

                hideKeyboard();
                toggleCredentialsError(false);

                final String url = ((EditText) getView().findViewById(R.id.addr_field)).getText().toString();
                final String username = ((EditText) getView().findViewById(R.id.username_field)).getText().toString();
                final String password = ((EditText) getView().findViewById(R.id.password_field)).getText().toString();

                if (!URLUtil.isValidUrl(url)) {
                    toggleInvalidURL(true);
                    return;
                }

                newConnection(url, username, password);
                break;

            case R.id.demo_button:
                PointOfInterest.setPoints(PointOfInterest.getDemoPoints());
                User.getInstance().setCenter(new Center(0));
                User.getInstance().setDemo(getContext().getApplicationContext());
                startAR();
                break;
        }
    }

    @Override
    protected void showError(PersistenceOperationDelegate.Errors error) {
        switch (error) {
            case INVALID_URL:
                toggleInvalidURL(true);
                break;
            case INTERNET_ERROR:
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
            case INVALID_CREDENTIALS:
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
