package com.oodoo.trackerapp;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.oodoo.utils.VolleyHelper;

import org.json.JSONException;
import org.json.JSONObject;


public class RegisterDeviceActivity extends ActionBarActivity {
    private EditText deviceAliasNameTxt;
    private EditText devicePhoneNumberTxt;
    private Button registerDeviceBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_device);
        deviceAliasNameTxt = (EditText)   findViewById(R.id.deviceAliasNameTxt);
        devicePhoneNumberTxt = (EditText) findViewById(R.id.deviceNumberTxt);
        registerDeviceBtn = (Button) findViewById(R.id.registerDeviceBtn);
        registerDeviceBtn.setOnClickListener(registerDeviceBtnListener);
    }

    public View.OnClickListener registerDeviceBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            registerDevice();
        }
    };

    private void registerDevice() {
        String url = "http://oodoo.herokuapp.com/devices.json";
        Response.Listener<JSONObject> registerDeviceListener = new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject json) {
                String jsonMessage = "" ;
                try{
                    jsonMessage = json.getString("message");
                }
                catch(JSONException ex){
                    jsonMessage = "Imposible to register device";
                }
                Toast toast = Toast.makeText(RegisterDeviceActivity.this, jsonMessage, Toast.LENGTH_LONG);
                toast.show();
            }
        };
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if(volleyError != null){
                    Toast toast = Toast.makeText(RegisterDeviceActivity.this, "Something Happened with the server connection", Toast.LENGTH_SHORT);

                    //NOTE: For modify the toast apparence use a custom view and a custom layout file
                    //toast.setView(getLayoutInflater().inflate(R.layout.some_toast_layout,null));

                    toast.show();
                    Log.e("TrackingRoutes", volleyError.toString());
                }
            }
        };
        JSONObject postRequestJSONObject = new JSONObject();
        String devicePhoneNumber = "+1" + devicePhoneNumberTxt.getText().toString();
        String deviceAliasName = deviceAliasNameTxt.getText().toString();
        try {
            postRequestJSONObject.put("device_number", devicePhoneNumber);
            postRequestJSONObject.put("alias_name", deviceAliasName);
        } catch (JSONException e) {
            Toast toast = Toast.makeText(RegisterDeviceActivity.this, "Something Happened with the server connection", Toast.LENGTH_SHORT);
            toast.show();
            e.printStackTrace();
        }
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.POST,url,postRequestJSONObject,registerDeviceListener,errorListener);
        VolleyHelper.getInstance(this).addToRequestQueue(getRequest);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_device, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
