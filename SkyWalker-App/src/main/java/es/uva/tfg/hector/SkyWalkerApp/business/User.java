package es.uva.tfg.hector.SkyWalkerApp.business;

import android.content.Context;

import es.uva.tfg.hector.SkyWalkerApp.R;
import es.uva.tfg.hector.SkyWalkerApp.persistence.ServerFacade;
import es.uva.tfg.hector.SkyWalkerApp.services.PersistenceOperationDelegate;

/**
 * A XtremeLoc user.
 * @author Héctor Del Campo Pando.
 */
public class User {

    private Token token;

    private String username;

    private static User instance = new User();

    public static User getInstance() {
        return instance;
    }

    public void login(Context context,
                      String server,
                      final String username,
                      String password,
                      final PersistenceOperationDelegate delegate) {

        ServerFacade.getInstance(context)
                .getToken(new ServerFacade.OnServerResponse<Token>() {

                    @Override
                    public void onSuccess(Token response) {
                        token = response;
                        User.this.username = username;
                        if (null != delegate) {
                            delegate.onSuccess();
                        }
                    }

                    @Override
                    public void onError(ServerFacade.Errors error) {
                        PersistenceOperationDelegate.Errors errorToBack;

                        switch (error) {
                            case NO_CONNECTION: case TIME_OUT:
                                errorToBack = PersistenceOperationDelegate.Errors.INTERNET_ERROR;
                                break;
                            default:
                                errorToBack = PersistenceOperationDelegate.Errors.SERVER_ERROR;
                                break;
                        }

                        if (null != delegate) {
                            delegate.onError(errorToBack);
                        }
                    }

                }, server, username, password);
    }

    public void logout() {
        token = null;
    }

    public boolean isLogged() {
        return token != null;
    }

    /**
     * Retrieves whether connection is in demo mode or not.
     * @return true if demo mode, false otherwise.
     */
    public boolean isDemo(Context context) {
        return token.getURL().equals(context.getString(R.string.demo));
    }

    public Token getToken() {
        return token;
    }

    /**
     * Sets demo mode.
     */
    public void setDemo(Context context) {
        token = new Token(context.getString(R.string.demo), null);
    }

    public void registerBeacon (final Context context, final PersistenceOperationDelegate delegate) {

        ServerFacade.getInstance(context).
                registerAsBeacon(new ServerFacade.OnServerResponse<iBeaconFrame>() {

                    @Override
                    public void onSuccess(iBeaconFrame frame) {
                        iBeaconTransmitter transmitter = iBeaconTransmitter.getInstance(context);
                        transmitter.configure(frame, (byte) -59);
                        PointOfInterest.setMySelf(new PointOfInterest(frame.getMinor(), username));
                        if (null != delegate) {
                            delegate.onSuccess();
                        }
                    }

                    @Override
                    public void onError(ServerFacade.Errors error) {
                        PersistenceOperationDelegate.Errors errorToBack;

                        switch (error) {
                            case NO_CONNECTION: case TIME_OUT:
                                errorToBack = PersistenceOperationDelegate.Errors.INTERNET_ERROR;
                                break;
                            default:
                                errorToBack = PersistenceOperationDelegate.Errors.SERVER_ERROR;
                                break;
                        }

                        if (null != delegate) {
                            delegate.onError(errorToBack);
                        }
                    }

                }, username);
    }

}
