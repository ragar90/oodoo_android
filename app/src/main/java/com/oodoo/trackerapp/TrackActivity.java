package com.oodoo.trackerapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.oodoo.models.Position;
import com.oodoo.models.TrackingRoute;
import com.oodoo.utils.VolleyHelper;

import org.json.JSONObject;

import java.util.*;

public class TrackActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    public static final String DEVICE_ID = "device_id";
    public static final String POSITION = "position";
    public static final String LATITUDE_KEY = "lat";
    public static final String LONGITUDE_KEY = "lon";
    public static final String TOTAL_TRACKING_POSITION = "track_pos";
    private LinkedList<LatLng> track;
    private boolean mapZoomed = false;
    private ProgressDialog pd;
    private static HashMap<String, TrackingRoute> routes;
    private TrackingRoute trackingRoute;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        Intent intent = getIntent();
        int deviceId = intent.getIntExtra(DEVICE_ID, 0);
        routes = new HashMap<String, TrackingRoute>();
        getTrackingRouteForDevice(deviceId);
        if(savedInstanceState != null){
            int trackSize = savedInstanceState.getInt(TOTAL_TRACKING_POSITION);
            double[] lat = savedInstanceState.getDoubleArray(LATITUDE_KEY);
            double[] lon = savedInstanceState.getDoubleArray(LONGITUDE_KEY);
            track = new LinkedList<LatLng>();
            for(int i = 0; i< trackSize; i++){
                LatLng position =  new LatLng(lat[i],lon[i]);
                track.add(position);
            }
        }
        else{
            track = new LinkedList<LatLng>();
        }
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState){
        int trackPositionSize = track.size();
        double[] lat = new double[trackPositionSize];
        double[] lon = new double[trackPositionSize];

        for(int i = 0; i < trackPositionSize; i++ ){
            LatLng position = track.get(i);
            lat[i] = position.latitude;
            lon[i] = position.longitude;
        }
        outState.putInt(TOTAL_TRACKING_POSITION,trackPositionSize);
        outState.putDoubleArray(LATITUDE_KEY, lat);
        outState.putDoubleArray(LONGITUDE_KEY, lon);
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null && track.size() > 0) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        if(track.size() > 0 ){
            mMap.clear();
            PolylineOptions options = new PolylineOptions();
            for(LatLng position : track){
                options.add(position);
            }
            mMap.addPolyline(options);
            MarkerOptions markerOpt = new MarkerOptions();
            markerOpt.position(track.getLast());
            markerOpt.title("My Car");
            markerOpt.snippet("Current Position");
            markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.car));
            mMap.addMarker(markerOpt);
            if(!mapZoomed){
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(track.getLast(), 13));
                mapZoomed = !mapZoomed;
            }

        }
        else{
            String size  = Integer.toString(track.size());
            Log.e("TrackingRoutes", "track size: " + size);
        }
    }

    private void addNewPositionsToTrack(){
        for(Position p : trackingRoute.getPositions()){
            LatLng latLng = new LatLng(p.getLatitude(),p.getLongitude());
            track.add(latLng);
        }
    }

    private void getTrackingRouteForDevice(int deviceId){
        pd = ProgressDialog.show(this, "Please wait...", "Please wait...");
        try {
            Thread.sleep(2000);
        } catch (Exception e) {
            Log.i("Error message: ", e.toString());
        }
        String deviceIdKey = Integer.toString(deviceId);
        trackingRoute = routes.get(deviceIdKey);
        if(trackingRoute != null){
            pd.dismiss();
        }
        else{
            Response.Listener<JSONObject> trackingRouteListener = new Response.Listener<JSONObject>(){

                @Override
                public void onResponse(JSONObject response) {
                    trackingRoute = new TrackingRoute(response);
                    String idKey = Integer.toString(trackingRoute.getDeviceId());
                    routes.put(idKey, trackingRoute);
                    addNewPositionsToTrack();
                    setUpMap();
                    pd.dismiss();
                }
            };
            Response.ErrorListener errorListener = new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    if(volleyError != null) Log.e("TrackingRoutes", volleyError.getMessage());
                }
            };
            String url = "http://oodoo.herokuapp.com/devices/" + deviceIdKey + "/tracking_route.json";
            JsonObjectRequest getRequest = new JsonObjectRequest(url,null,trackingRouteListener,errorListener);
            VolleyHelper.getInstance(this).addToRequestQueue(getRequest);
        }

    }
}
