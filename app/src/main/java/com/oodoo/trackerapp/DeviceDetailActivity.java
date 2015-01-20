package com.oodoo.trackerapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.*;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.oodoo.models.Device;
import com.oodoo.models.Devices;
import com.oodoo.utils.VolleyHelper;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * An activity representing a single Device detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link DeviceListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link DeviceDetailFragment}.
 */
public class DeviceDetailActivity extends ActionBarActivity {
    private GoogleMap lastMapPosition;
    private boolean mapZoomed = false;
    private Button trackingRouteBtn;
    private Switch lockSwitch;
    private int deviceId;
    private ProgressDialog pd;
    private static final String DEVICE_ID = "DEVICE_ID";
    private boolean isStarting = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            deviceId = getIntent().getIntExtra(DeviceDetailFragment.ARG_ITEM_ID,0);
            Bundle arguments = new Bundle();
            arguments.putInt(DeviceDetailFragment.ARG_ITEM_ID,deviceId);
            DeviceDetailFragment fragment = new DeviceDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.device_detail_container, fragment)
                    .commit();
        }
        else{
            deviceId = savedInstanceState.getInt(DEVICE_ID);
        }
        trackingRouteBtn = (Button) findViewById(R.id.trackingRoutesBtn);
        trackingRouteBtn.setOnClickListener(trackingRouteBtnListener);
        lockSwitch = (Switch) findViewById(R.id.lockSwitch);
        lockSwitch.setOnCheckedChangeListener(lockSwitchListener);
        Device device = Devices.getDevice(deviceId);
        lockSwitch.setChecked(device.isLock());
        isStarting = false;
        setUpMapIfNeeded();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putInt(DEVICE_ID, deviceId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, DeviceListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private OnClickListener trackingRouteBtnListener = new Button.OnClickListener(){
        @Override
        public void onClick(View v){
            Intent trackingRoutesIntent = new Intent(DeviceDetailActivity.this, TrackActivity.class);
            trackingRoutesIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            trackingRoutesIntent.putExtra(TrackActivity.DEVICE_ID,deviceId);
            DeviceDetailActivity.this.startActivity(trackingRoutesIntent);
        }
    };
    private OnCheckedChangeListener lockSwitchListener = new OnCheckedChangeListener(){

        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param buttonView The compound button view whose state has changed.
         * @param isChecked  The new checked state of buttonView.
         */
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(!isStarting){
                String subPath = isChecked ? "lock" : "unlock";
                lockUnlockDevice(subPath);
                Device device = Devices.getDevice(deviceId);
                device.setLock(isChecked);
            }
        }
    };


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (lastMapPosition == null) {
            // Try to obtain the map from the SupportMapFragment.
            lastMapPosition = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.lastPositionMap))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (lastMapPosition != null) {
                setUpMap();
            }
        }
    }
    private void setUpMap() {
        lastMapPosition.clear();
        MarkerOptions markerOpt = new MarkerOptions();
        LatLng position = new LatLng(47.7127748,-122.2866323);
        markerOpt.position(position);
        markerOpt.title("My Car");
        markerOpt.snippet("Current Position");
        markerOpt.icon(BitmapDescriptorFactory.fromResource(R.drawable.car));
        lastMapPosition.addMarker(markerOpt);
        if(!mapZoomed){
            lastMapPosition.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 13));
            mapZoomed = !mapZoomed;
        }
    }

    private void lockUnlockDevice(String subPath){
        String id = Integer.toString(this.deviceId);
        String url = "http://oodoo.herokuapp.com/devices/" + id + "/" + subPath + ".json";
        final String subPath2 = subPath;
        Response.Listener<JSONObject> lockDeviceListListener = new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject json) {
                String jsonMessage = "" ;
                try{
                    jsonMessage = json.getString("message");
                }
                catch(JSONException ex){
                    jsonMessage = "Imposible to " + subPath2 + " device";
                }
                Toast toast = Toast.makeText(DeviceDetailActivity.this, jsonMessage, Toast.LENGTH_SHORT);
                toast.show();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if(volleyError != null){
                    Toast toast = Toast.makeText(DeviceDetailActivity.this, "Something Happened with the server connection", Toast.LENGTH_SHORT);
                    toast.show();
                    Log.e("TrackingRoutes", volleyError.toString());
                }
            }
        };
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST,url,null,lockDeviceListListener,errorListener);
        VolleyHelper.getInstance(this).addToRequestQueue(getRequest);
    }
}
