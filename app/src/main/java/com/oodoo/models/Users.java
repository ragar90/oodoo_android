package com.oodoo.models;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.oodoo.utils.VolleyHelper;

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
    public static void setCurrentUser(User user){currentUser = user;}
    public static void closeSession(){
        currentUser = null;
    }
    public static void getOrCreateUser(GraphUser fbUser, String authToken, Context context, Response.Listener<JSONObject> listener){
        String url = "http://oodoo.herokuapp.com/sessions.json";
        JSONObject jsonUser = new JSONObject();
        JSONObject jsonParams = new JSONObject();
        try{
            jsonUser.put("name", fbUser.getName());
            jsonUser.put("lastname", fbUser.getLastName());
            jsonUser.put("facebook_token",authToken);
            jsonUser.put("facebook_id",fbUser.getId());
            jsonParams.put("user", jsonUser);
            VolleyHelper.getJsonObjectRequest(Request.Method.POST,url,jsonParams,listener);
            JsonObjectRequest request = VolleyHelper.getJsonObjectRequest(Request.Method.POST,url,jsonParams,listener);
            VolleyHelper.getInstance(context).addToRequestQueue(request);
        }
        catch(Exception ex){
            Log.e("UserAuthError", ex.getMessage());
        }
    }

    public static boolean isUserSignedIn(){
        return currentUser != null;
    }
}
