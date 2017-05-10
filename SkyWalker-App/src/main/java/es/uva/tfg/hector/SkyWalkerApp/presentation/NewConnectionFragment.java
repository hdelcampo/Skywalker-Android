package es.uva.tfg.hector.SkyWalkerApp.presentation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;

import es.uva.tfg.hector.SkyWalkerApp.R;
import es.uva.tfg.hector.SkyWalkerApp.business.Center;
import es.uva.tfg.hector.SkyWalkerApp.business.User;
import es.uva.tfg.hector.SkyWalkerApp.business.iBeaconTransmitter;
import es.uva.tfg.hector.SkyWalkerApp.services.PersistenceOperationDelegate;

/**
 * Base fragment for new connection fragments.
 * @author Héctor Del Campo Pando.
 */
public abstract class NewConnectionFragment extends Fragment{

    /**
     * Starts the Augmented Reality interface on a new activity.
     */
    protected void startAR() {

        Intent intent = new Intent(getActivity(), AugmentedRealityActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }

    /**
     * Establishes a new connection to the given server.
     * @param url of the server, must be a valid URL,
     *            otherwise function will return and a message will be shown to the user.
     * @param login of the client
     * @param password of the client
     */
    protected void newConnection(final String url, final String login, final String password) {

        final ProgressDialog dialog =
                ProgressDialog.show(getContext(), null, getString(R.string.connection_started), true, false);

        User.getInstance().login(getContext().getApplicationContext(),
                url,
                login,
                password,
                new PersistenceOperationDelegate() {
                    @Override
                    public void onSuccess() {
                        checkBluetooth(dialog);
                    }

                    @Override
                    public void onError(Errors error) {
                        dialog.dismiss();
                        showError(error);
                    }
                });

    }

    /**
     * Retrieves the receivers for the center.
     * @param dialog to control.
     */
    private void retrieveReceivers(final ProgressDialog dialog) {

        dialog.setMessage(getString(R.string.connection_recievers));

        Center.centers.add(new Center(0));

        Center.centers.get(0).loadReceivers(getActivity().getApplicationContext(), new PersistenceOperationDelegate() {
            @Override
            public void onSuccess() {
                retrieveTags(dialog);
            }

            @Override
            public void onError(Errors error) {
                dialog.dismiss();
                showError(error);
            }
        });

    }


    /**
     * Retrieves the tags available for the connection token.
     * @param dialog to handle.
     */
    private void retrieveTags (final ProgressDialog dialog) {

        dialog.setMessage(getString(R.string.connection_tags));

        Center.centers.get(0).loadTags(getContext().getApplicationContext(),
                new PersistenceOperationDelegate() {
                    @Override
                    public void onSuccess() {
                        dialog.dismiss();
                        startAR();
                    }

                    @Override
                    public void onError(Errors error) {
                        dialog.dismiss();
                        showError(error);
                    }
                });

    }

    /**
     * Registers this device as a beacon.
     * @param dialog to handle.
     */
    private void registerAsBeacon(final ProgressDialog dialog) {

        dialog.setMessage(getString(R.string.connection_register_beacon));

        User.getInstance().registerBeacon(getContext().getApplicationContext(), new PersistenceOperationDelegate() {
            @Override
            public void onSuccess() {
                retrieveReceivers(dialog);
            }

            @Override
            public void onError(Errors error) {
                dialog.dismiss();
                showError(error);
            }
        });

    }

    /**
     * Checks if bluetooth is enabled or not.
     * @param dialog to handle.
     */
    private void checkBluetooth(final ProgressDialog dialog) {

        dialog.setMessage(getString(R.string.bluetooth_checking));

        if (iBeaconTransmitter.isBluetoothEnabled()) {
            registerAsBeacon(dialog);
        } else {
            dialog.setMessage(getString(R.string.bluetooth_enabling));

            iBeaconTransmitter.enableBluetooth(getContext(), new iBeaconTransmitter.OnEventCallback() {
                @Override
                public void onEnabled() {
                    registerAsBeacon(dialog);
                }
            });
        }

    }



    /**
     * Handles showing error to the user.
     * @param error to be shown.
     */
    abstract void showError(PersistenceOperationDelegate.Errors error);

}
