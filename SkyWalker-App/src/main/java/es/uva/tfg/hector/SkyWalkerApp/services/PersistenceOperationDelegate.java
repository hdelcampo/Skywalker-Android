package es.uva.tfg.hector.SkyWalkerApp.services;

/**
 * Interface for operation's delegates.
 * @author Hector Del Campo Pando
 */
public interface PersistenceOperationDelegate {

    /**
     * Enum for server errors.
     */
    enum Errors {
        INTERNET_ERROR, SERVER_ERROR, INVALID_URL, INVALID_CREDENTIALS
    }

    /**
     * Callback for success.
     */
    void onSuccess ();

    /**
     * Callback for error.
     * @param error given by operation.
     */
    void onError (Errors error);

}
