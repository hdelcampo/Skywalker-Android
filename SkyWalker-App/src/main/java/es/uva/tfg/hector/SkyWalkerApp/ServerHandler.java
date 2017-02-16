package es.uva.tfg.hector.SkyWalkerApp;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Handler for server petitions
 * @author Hector Del Campo Pando
 */

public class ServerHandler {

    public enum Errors {
        INVALID_USERNAME_OR_PASSWORD, UNKNOWN
    }

    public static void getToken (final Context context, final OnServerResponse onResponseListener,
                                        String url, final String username, final String password) {

        url = url.concat("/api/authentication");

        RequestQueue queue = Volley.newRequestQueue(context);

        JSONObject params = new JSONObject();
        try {
            params.put("login", username);
            params.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonRequest<String> request = new JsonRequest<String>(Request.Method.POST, url, params.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                onResponseListener.onSuccess(response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Errors errorEnum = getServerError(error);
                onResponseListener.onError(errorEnum);
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

        queue.add(request);

    }

    /**
     * Retrieves actual server error reason
     * @param error given by server
     * @return an enum for the error
     */
    private static Errors getServerError(VolleyError error) {

        Errors errorEnum;

        if (error == null || error.networkResponse == null) {
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
    public interface OnServerResponse {

        /**
         * Callback for success petition
         * @param response of the server
         */
        void onSuccess (String response);

        /**
         * Callback for error
         * @param error given by server
         */
        void onError (Errors error);

    }

}
