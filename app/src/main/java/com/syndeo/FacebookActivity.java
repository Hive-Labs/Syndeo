package com.syndeo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;


public class FacebookActivity extends Activity {
    // Other app specific specialization
    CallbackManager callbackManager;
    ProfileTracker mProfileTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_facebook);
        AppEventsLogger.activateApp(this);
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");
        callbackManager = CallbackManager.Factory.create();
        Profile profile;
        if((profile = Profile.getCurrentProfile()) != null){
            String firstName = profile.getFirstName();
            String lastName = profile.getLastName();
            Uri profilePicUri = profile.getProfilePictureUri(500, 500);
            String facebookID = profile.getId();
            Intent mainActivity = new Intent(FacebookActivity.this, MainActivity.class);
            mainActivity.putExtra("firstName", firstName);
            mainActivity.putExtra("lastName", lastName);
            mainActivity.putExtra("profilePicUri", profilePicUri.toString());
            mainActivity.putExtra("facebookID", facebookID);
            startActivity(mainActivity);
            // profile is the new profile
            Log.v("facebook - profile", profile.getFirstName());
        }
        mProfileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                if(profile2 == null) return;
                String firstName = profile2.getFirstName();
                String lastName = profile2.getLastName();
                Uri profilePicUri = profile2.getProfilePictureUri(1000, 800);
                String facebookID = profile2.getId();
                Intent mainActivity = new Intent(FacebookActivity.this, MainActivity.class);
                mainActivity.putExtra("firstName", firstName);
                mainActivity.putExtra("lastName", lastName);
                mainActivity.putExtra("profilePicUri", profilePicUri.toString());
                mainActivity.putExtra("facebookID", facebookID);
                startActivity(mainActivity);
                // profile2 is the new profile
                Log.v("facebook - profile", profile2.getFirstName());
                mProfileTracker.stopTracking();
            }
        };
        mProfileTracker.startTracking();
        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.d("onSuccess: ", loginResult.getRecentlyGrantedPermissions().toString());
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d("onError: ", exception.getStackTrace().toString());
            }
        });
    }

    @Override
    protected void onStop() {
        mProfileTracker.stopTracking();
        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
