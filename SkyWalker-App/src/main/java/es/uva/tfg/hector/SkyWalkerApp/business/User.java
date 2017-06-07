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

    /**
     * The user's token.
     */
    private Token token;

    /**
     * The user's username.
     */
    private String username;

    /**
     * The BLE transmitter.
     */
    private final iBeaconTransmitter transmitter = new iBeaconTransmitter();

    /**
     * Position on map.
     */
    private MapPoint position = new MapPoint(0, 0.5f, 0.5f, 0);

    /**
     * Associated center.
     */
    private Center center;

    /**
     * Singleton instance.
     */
    private static final User instance = new User();

    /**
     * Returns the singleton instance.
     * @return the instance.
     */
    public static User getInstance() {
        return instance;
    }

    /**
     * Logins a user in a server.
     * @param context to login.
     * @param server where to login.
     * @param username of the user.
     * @param password of the user.
     * @param delegate callback for success or error.
     */
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
                        center = new Center(0, 128, 80);
                        User.this.username = username;
                        if (null != delegate) {
                            delegate.onSuccess();
                        }
                    }

                    @Override
                    public void onError(ServerFacade.Errors error) {
                        PersistenceOperationDelegate.Errors errorToBack;

                        switch (error) {
                            case INVALID_USERNAME_OR_PASSWORD:
                                errorToBack = PersistenceOperationDelegate.Errors.INVALID_CREDENTIALS;
                                break;
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

    /**
     * Logouts the user, settnig it's token to null.
     */
    public void logout() {
        token = null;
    }

    /**
     * Checks if user is logged in a server.
     * @return true if logged, false otherwise.
     */
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

    /**
     * Sets the user's center.
     * @param center to set.
     */
    public void setCenter(Center center) {
        this.center = center;
    }

    /**
     * Retrieves the user's token.
     * @return the token.
     */
    public Token getToken() {
        return token;
    }

    /**
     * Retrieves the user's center.
     * @return the center.
     */
    public Center getCenter () {
        return center;
    }

    /**
     * Sets demo mode.
     * @param context to use.
     */
    public void setDemo(Context context) {
        token = new Token(context.getString(R.string.demo), null);
    }

    /**
     * Retrieves the BLE transmitter.
     * @return the transmitter.
     */
    public iBeaconTransmitter getTransmitter() {
        return transmitter;
    }

    /**
     * Retrieves the user's position.
     * @return the position.
     */
    public MapPoint getPosition() {
        return position;
    }

    /**
     * Registers this device as the user's beacon.
     * @param context to use.
     * @param delegate callback for success or errors.
     */
    public void registerBeacon (final Context context, final PersistenceOperationDelegate delegate) {

        ServerFacade.getInstance(context).
                registerAsBeacon(new ServerFacade.OnServerResponse<iBeaconFrame>() {

                    @Override
                    public void onSuccess(iBeaconFrame frame) {
                        transmitter.init(context);
                        transmitter.configure(frame, (byte) -59);
                        position = new MapPoint(frame.getMinor(), -1, -1, -1);
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
