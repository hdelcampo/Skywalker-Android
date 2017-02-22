package es.uva.tfg.hector.SkyWalkerApp;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handler for server petitions, as there can only be one connection per time,
 * this singleton also manages the connection token.
 * @author Hector Del Campo Pando
 */

public class ServerHandler {

    /**
     * Enum for server errors.
     */
    public enum Errors {
        INVALID_USERNAME_OR_PASSWORD, INVALID_JSON, TIME_OUT, UNKNOWN
    }

    /**
     * Singleton instance.
     */
    private static ServerHandler instance;

    /**
     * Requests queue.
     */
    private final RequestQueue requestQueue;

    /**
     * Connection token.
     */
    private Token token;

    /**
     * Retrieves the singleton instance.
     * @param context of the App to make petitions.
     * @return the singleton instance.
     */
    public static ServerHandler getInstance (Context context) {

        if (instance == null) {
            instance = new ServerHandler(context);
        }

        return instance;

    }

    /**
     * Creates a new instance of the requests queue.
     * @param context of the App.
     */
    private ServerHandler (Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    /**
     * Retrieves a new connection, generating a new {@code Token}.
     * @param responseListener that will handle responses.
     * @param url of the server.
     * @param username of the user.
     * @param password of the user.
     */
    public void getToken (final OnServerResponse <Token> responseListener,
                                        final String url, final String username, final String password) {

        final String apiURL = url.concat("/api/authentication");

        JSONObject params = new JSONObject();
        try {
            params.put("login", username);
            params.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonRequest<String> request = new JsonRequest<String>(Request.Method.POST, apiURL, params.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                token = new Token(url, response);
                responseListener.onSuccess(token);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Errors errorEnum = getServerError(error);
                responseListener.onError(errorEnum);
            }
        }) {
            @Override
            protected Response parseNetworkResponse(NetworkResponse response) {
                String parsed;
                try {
                    parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
                } catch (UnsupportedEncodingException e) {
                    parsed = new String(response.data);
                }
                return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
            }
        };

        requestQueue.add(request);

    }

    /**
     * Retrieves all avaliable tags for a given token.
     * @param responseListener that will handle responses.
     */
    public void getAvaliableTags (final OnServerResponse <List<PointOfInterest>> responseListener) {

        if (null == token) {
            throw new IllegalStateException("Cannot retrieve tags without a established connection");
        }

        //TODO center real
        String url = token.getURL().concat("/api/centers/0/tags");

        JsonRequest<JSONArray> request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<PointOfInterest> points = new ArrayList<>(response.length());
                try {

                    for (int i = 0; i < response.length(); i++) {
                        JSONObject json = response.getJSONObject(i);
                        PointOfInterest point =
                                new PointOfInterest(json.getInt("id"),
                                                json.getString("name"));
                        points.add(point);
                    }
                    responseListener.onSuccess(points);

                } catch (JSONException e) {
                    e.printStackTrace();
                    responseListener.onError(Errors.INVALID_JSON);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Errors errorNum = getServerError(error);
                responseListener.onError(errorNum);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token.getToken());
                return headers;
            }
        };

        requestQueue.add(request);

    }

    /**
     * Retrieves the last known position for a given tag.
     * @param responseListener that will handle responses.
     * @param tag to ask for.
     */
    public void getLastPosition (final OnServerResponse <PointOfInterest> responseListener, final PointOfInterest tag) {

        if (null == token) {
            throw new IllegalStateException("Cannot retrieve tags without a established connection");
        }

        String url = token.getURL().concat("/api/centers/0/tags/" + tag.getId() + "/position");

        JsonRequest<JSONObject> request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    tag.setX(response.getInt("x"));
                    tag.setY(response.getInt("y"));
                    tag.setZ(response.getInt("z"));
                    responseListener.onSuccess(tag);
                } catch (JSONException e) {
                    e.printStackTrace();
                    responseListener.onError(Errors.INVALID_JSON);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Errors errorNum = getServerError(error);
                responseListener.onError(errorNum);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token.getToken());
                return headers;
            }
        };

        requestQueue.add(request);

    }

    /**
     * Retrieves actual server error reason
     * @param error given by server
     * @return an enum for the error
     */
    private static Errors getServerError(VolleyError error) {

        Errors errorEnum;

        if (error.getClass().equals(TimeoutError.class)) {
            errorEnum = Errors.TIME_OUT;
        } else if (error == null || error.networkResponse == null) {
            errorEnum = Errors.UNKNOWN;
        } else {
            switch (error.networkResponse.statusCode) {
                case 401:
                    errorEnum = Errors.INVALID_USERNAME_OR_PASSWORD;
                    break;
                default:
                    errorEnum = Errors.UNKNOWN;
                    break;
            }
        }

        return errorEnum;

    }


    /**
     * Interface that must be implemented by caller in order to recieve responses
     * @author Hector Del Campo Pando
     */
    public interface OnServerResponse <T> {

        /**
         * Callback for success petition
         * @param response of the server
         */
        void onSuccess (T response);

        /**
         * Callback for error
         * @param error given by server
         */
        void onError (Errors error);

    }

}
