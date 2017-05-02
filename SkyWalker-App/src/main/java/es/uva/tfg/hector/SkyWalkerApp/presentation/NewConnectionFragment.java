package es.uva.tfg.hector.SkyWalkerApp.presentation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.List;

import es.uva.tfg.hector.SkyWalkerApp.R;
import es.uva.tfg.hector.SkyWalkerApp.business.Center;
import es.uva.tfg.hector.SkyWalkerApp.business.MapPoint;
import es.uva.tfg.hector.SkyWalkerApp.business.PointOfInterest;
import es.uva.tfg.hector.SkyWalkerApp.business.Token;
import es.uva.tfg.hector.SkyWalkerApp.business.iBeaconFrame;
import es.uva.tfg.hector.SkyWalkerApp.business.iBeaconTransmitter;
import es.uva.tfg.hector.SkyWalkerApp.persistence.ServerFacade;

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
        startActivity(intent);
        getActivity().finish();

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

        ServerFacade.getInstance(getContext().getApplicationContext())
                .getToken(new ServerFacade.OnServerResponse<Token>() {

                    @Override
                    public void onSuccess(Token response) {
                        checkBluetooth(dialog, login);
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
    protected void retrieveReceivers(final ProgressDialog dialog) {

        dialog.setMessage(getString(R.string.connection_recievers));

        Center.centers.add(new Center(0));

        ServerFacade.getInstance(getContext().getApplicationContext())
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
    protected void retrieveTags (final ProgressDialog dialog) {

        dialog.setMessage(getString(R.string.connection_tags));

        ServerFacade.getInstance(getContext().getApplicationContext()).
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

    protected void registerAsBeacon(final ProgressDialog dialog, final String username) {

        dialog.setMessage(getString(R.string.connection_register_beacon));

        ServerFacade.getInstance(getContext().getApplicationContext()).
                registerAsBeacon(new ServerFacade.OnServerResponse<iBeaconFrame>() {

                    @Override
                    public void onSuccess(iBeaconFrame frame) {
                        retrieveReceivers(dialog);
                        iBeaconTransmitter transmitter = iBeaconTransmitter.getInstance(getContext());
                        transmitter.configure(frame, (byte) -59);
                        PointOfInterest.setMySelf(new PointOfInterest(frame.getMinor(), "username"));
                    }

                    @Override
                    public void onError(ServerFacade.Errors error) {
                        dialog.dismiss();
                        showError(error);
                    }

                }, username);

    }

    private void checkBluetooth(final ProgressDialog dialog, final String login) {

        dialog.setMessage(getString(R.string.bluetooth_checking));

        if (iBeaconTransmitter.isBluetoothEnabled()) {
            registerAsBeacon(dialog, login);
        } else {
            dialog.setMessage(getString(R.string.bluetooth_enabling));

            iBeaconTransmitter.enableBluetooth(getContext(), new iBeaconTransmitter.OnEventCallback() {
                @Override
                public void onEnabled() {
                    registerAsBeacon(dialog, login);
                }
            });
        }

    }



    /**
     * Handles showing error to the user.
     * @param error to be shown.
     */
    abstract void showError(ServerFacade.Errors error);

}
