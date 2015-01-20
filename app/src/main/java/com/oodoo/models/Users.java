package com.oodoo.models;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.Session;
import com.facebook.model.GraphUser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by ragar90 on 1/11/15.
 */
public class Users {
    private static String jsonMessage;
    private static User currentUser;
    public static User getCurrentUser() {
        return currentUser;
    }
    public static void closeSession(){
        currentUser = null;
    }
    private static Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            if(volleyError != null) Log.e("GetDevices", volleyError.getMessage());
        }
    };
    private static Response.Listener<JSONObject> newOrOldUserListener = new Response.Listener<JSONObject>(){
        @Override
        public void onResponse(JSONObject json) {
            currentUser = new User(json);
        }
    };
    public static void getOrCreateUser(GraphUser fbUser, String authToken ){
        String url = "";
        JSONObject jsonUser = new JSONObject();
        JSONObject jsonParams = new JSONObject();
        try{
            jsonUser.put("name", fbUser.getName());
            jsonUser.put("lastname", fbUser.getLastName());
            jsonUser.put("facebook_id",authToken);
            jsonUser.put("facebook_token",fbUser.getId());
            jsonParams.put("user", jsonUser);
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,url,jsonParams,newOrOldUserListener,errorListener);
        }
        catch(Exception ex){
            Log.e("UserAuthError", ex.getMessage());
        }
    }

    public static boolean isUserSignedIn(){
        return currentUser != null;
    }
}
