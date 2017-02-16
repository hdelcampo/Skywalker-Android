package es.uva.tfg.hector.SkyWalkerApp;

/**
 * Connection token as singleton
 * @author Hector Del Campo Pando
 */
public class Token {

    /**
     * Token given by server
     */
    private String token;

    /**
     * Singleton instance
     */
    private static Token ourInstance = new Token();

    public static Token getInstance() {
        return ourInstance;
    }

    private Token() {
    }

    /**
     * Returns the token
     * @return the token
     */
    public String getToken () {
        return token;
    }

    /**
     * Sets the connection token
     * @param token the new token
     */
    public void setToken (String token) {
        this.token = token;
    }

}
