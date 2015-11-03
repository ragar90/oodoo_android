package com.oodoo.trackerapp;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.oodoo.models.User;
import com.oodoo.models.Users;
import com.oodoo.utils.VolleyHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by ragar90 on 1/11/15.
 */
public class LoginFacebookFragment extends Fragment {
    private static final String TAG = LoginFacebookFragment.class.getSimpleName();

    private UiLifecycleHelper uiHelper;
    private TextView mSaludo;
    public LoginFacebookFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
        User user = Users.getCurrentUser();
        if(user == null){
            Log.e("Session Error", "No Session Found");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        mSaludo = (TextView)rootView.findViewById(R.id.txtSaludo);
        LoginButton authButton = (LoginButton) rootView.findViewById(R.id.login_button);
        authButton.setFragment(this);
        authButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        return rootView;

    }

    @Override
    public void onResume() {
        super.onResume();
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState(), null);
        }

        uiHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    private void onSessionStateChange(Session session, SessionState state,
                                      Exception exception) {
        if (state.isOpened()) {
            if(!Users.isUserSignedIn()){
                final String authToken = session.getAccessToken();
                if(Users.getCurrentUser() == null){
                    Request.newMeRequest(session, new Request.GraphUserCallback() {
                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                            if (user != null) {
                                signInServer(user,authToken);
                            }
                        }
                    }).executeAsync();
                }
                else{
                    goToDeviceList();
                }

            }
        } else if (state.isClosed()) {
            Users.closeSession();
        }
    }
    private void signInServer(GraphUser fbUser, String authToken){
        VolleyHelper.setFacebookSessionVariables(fbUser.getId(), authToken);
        com.android.volley.Response.Listener<JSONObject> userListener = new com.android.volley.Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject json) {
                User user = new User(json);
                Users.setCurrentUser(user);
                goToDeviceList();
            }
        };
        Users.getOrCreateUser(fbUser,authToken,this.getActivity(),userListener);
    }

    private void goToDeviceList(){
        Intent deviceListActivityIntent = new Intent(LoginFacebookFragment.this.getActivity(), DeviceListActivity.class);
        deviceListActivityIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        LoginFacebookFragment.this.getActivity().startActivity(deviceListActivityIntent);
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
}
