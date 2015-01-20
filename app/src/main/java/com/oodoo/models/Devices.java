package com.oodoo.models;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.oodoo.trackerapp.R;
import com.oodoo.utils.GsonRequest;
import com.oodoo.utils.UrlBuilder;
import com.oodoo.utils.VolleyHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class Devices {
    private static List<Device> devices;
    private static String path = "/devices";
    private static GsonRequest gsonRequest;
    private static HashMap<String ,Device > mappedDevices;
    private static String jsonMessage;
    private static HashMap<String, TrackingRoute> mappedTrackingRoutes;

    /**
     * This is the Response ErrorListener used for the @link GsonRequest
     * instance that is in charge of received and parse the
     * json response for GET /devices route.
     * <p/>
     */
    private static Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            if(volleyError != null) Log.e("GetDevices", volleyError.getMessage());
        }
    };


    //Get Method for a device
    public static Device getDevice(int id){
        String key = Integer.toString(id);
        Device device = mappedDevices.get(key);
        return device;
    }

    public static void updateDevice(Device device){
        String key = Integer.toString(device.getId());
        mappedDevices.put(key,device);
    }

    // GET /devices route
    /**
     * This is the Response Listener used for the @link GsonRequest
     * instance that is in charge of received and parse the
     * json response for GET /devices route.
     * <p/>
     */
    private static Response.Listener<JSONArray> deviceListListener = new Response.Listener<JSONArray>(){

        @Override
        public void onResponse(JSONArray response) {
            mappedDevices = new HashMap<String, Device>();
            String stringObject = response.toString();
            Gson parser = new Gson();
            Device[] devicesArr = parser.fromJson(stringObject, Device[].class );
            devices = new ArrayList<Device>(Arrays.asList(devicesArr));
            for (Device device : devices){
                String key = Integer.toString(device.getId());
                mappedDevices.put(key, device);
            }
        }
    };

    public static void loadDevices(Context context){
        String url = "https://oodoo.herokuapp.com/devices.json";
        JsonArrayRequest getRequest = new JsonArrayRequest(url,deviceListListener, errorListener);
        VolleyHelper.getInstance(context).addToRequestQueue(getRequest);
    }

    //Get Method for a list of devices
    public static List<Device> getDevices(){
        if(devices == null){
            return new ArrayList<Device>();
        }
        else{
            return devices;
        }
    }

    public static void setDevices(List<Device> devicesList){
        devices = devicesList;
        mappedDevices = new HashMap<String, Device>();
        for (Device device : devices){
            String key = Integer.toString(device.getId());
            mappedDevices.put(key, device);
        }
    }


    private static Response.Listener<JSONObject> lockDeviceListListener = new Response.Listener<JSONObject>(){

        @Override
        public void onResponse(JSONObject json) {
            try{
                jsonMessage = json.getString("message");
            }
            catch(JSONException ex){
                jsonMessage = "";
            }

        }
    };

    public static void lockDevice(int id, Context context){
        String keyId = Integer.toString(id);
        String memberUrl = UrlBuilder.buildMemberUrl(Devices.class,keyId,context);
        String lockUrl = memberUrl.concat("lock");
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,lockUrl,null,
                lockDeviceListListener,errorListener);
        VolleyHelper.getInstance(context).addToRequestQueue(jsonRequest);
    }

    public static void unlockDevice(int id, Context context){
        String keyId = Integer.toString(id);
        String memberUrl = UrlBuilder.buildMemberUrl(Devices.class,keyId,context);
        String unlockUrl = memberUrl.concat("unlock");
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,unlockUrl,null,
                lockDeviceListListener,errorListener);
        VolleyHelper.getInstance(context).addToRequestQueue(jsonRequest);

    }


    private static Response.Listener<JSONObject> deviceTrackingRouteListener = new Response.Listener<JSONObject>(){

        @Override
        public void onResponse(JSONObject jsonObject) {
            TrackingRoute route = new TrackingRoute(jsonObject);
            String deviceId = Integer.toString(route.getId());
            if(mappedTrackingRoutes == null){
                mappedTrackingRoutes = new HashMap<String, TrackingRoute>();
            }
            mappedTrackingRoutes.put(deviceId, route);
        }
    };

    public TrackingRoute getLastTrackingRoute(int id, Context context){
        String keyId = Integer.toString(id);
        String memberUrl = UrlBuilder.buildMemberUrl(Devices.class, keyId, context);
        String trackingRoute = memberUrl.concat("tracking_route");
        if(mappedTrackingRoutes == null){
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,trackingRoute,null, deviceTrackingRouteListener, errorListener);
        }
        String deviceId = Integer.toString(id);
        return mappedTrackingRoutes.get(deviceId);
    }

}
