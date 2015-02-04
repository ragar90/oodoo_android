package com.oodoo.utils;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by ragar90 on 12/23/14.
 */
public class VolleyHelper {
    private static String HEROKU_APP_TOKEN_ID = "eac0b1c4da4cce3880a1999e9742a0e89857c8a19804fd4a558ffe908a47e5a0";
    private static String facebookUserSessionToken;
    private static String facebookUserId;
    private static VolleyHelper INSTANCE;
    private RequestQueue requestQueue;
    private Context context;

    private VolleyHelper(Context context){
        this.context = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized VolleyHelper getInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = new VolleyHelper(context);
        }
        return INSTANCE;
    }

    public RequestQueue getRequestQueue(){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public static JsonArrayRequest getJsonArrayRequest(String url,Response.Listener<JSONArray> arrayListener){
        JsonArrayRequest request = new com.android.volley.toolbox.JsonArrayRequest(url,arrayListener,errorListener){
            @Override
            public HashMap<String, String> getParams(){
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("app-token",HEROKU_APP_TOKEN_ID);
                params.put("facebook-user-id",facebookUserId);
                params.put("facebook-token",facebookUserSessionToken);
                return params;
            }
        };
        return request;
    }

    public static JsonObjectRequest getJsonObjectRequest(int method, String url, JSONObject params, Response.Listener<JSONObject> listener){
        JsonObjectRequest request = new JsonObjectRequest(method,url,params,listener,errorListener){
            @Override
            public HashMap<String, String> getParams(){
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("app-token",HEROKU_APP_TOKEN_ID);
                if(facebookUserId != null && facebookUserSessionToken != null){
                    params.put("facebook-user-id",facebookUserId);
                    params.put("facebook-token",facebookUserSessionToken);
                }
                return params;
            }
        };
        return request;
    }

    private static Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            if(volleyError != null) Log.e("GetDevices", volleyError.getMessage());
        }
    };

    public static void setFacebookSessionVariables(String userId, String sessionToken){
        facebookUserId = userId;
        facebookUserSessionToken = sessionToken;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

}
