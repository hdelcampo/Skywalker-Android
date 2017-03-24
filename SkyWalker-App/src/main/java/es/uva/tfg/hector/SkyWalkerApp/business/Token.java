package es.uva.tfg.hector.SkyWalkerApp.business;

/**
 * Connection token.
 * @author Hector Del Campo Pando
 */
public class Token {

    /**
     * Server URL.
     */
    private final String URL;

    /**
     * Token given by server.
     */
    private final String token;

    public Token(String URL, String token) {
        this.URL = URL;
        this.token = token;
    }

    /**
     * Returns the token.
     * @return the token.
     */
    public String getToken () {
        return token;
    }

    /**
     * Returns the server's URL.
     * @return the URL.
     */
    public String getURL() {
        return URL;
    }

}
