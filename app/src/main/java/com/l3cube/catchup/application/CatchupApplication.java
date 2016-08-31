package com.l3cube.catchup.application;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;

/**
 * Created by Aditya Shirole on 6/13/2016.
 */
public class CatchupApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        Parse.enableLocalDatastore(CatchupApplication.this);



        Parse.Configuration configuration = new Parse.Configuration.Builder(CatchupApplication.this)
                .applicationId("a5ffb6374b9b25d0d43d247b153ff03d")
                .clientKey("catchup_client_key")
                .server("http://192.168.0.105:8080/parse/")
                .build();
        Parse.initialize(configuration);
        ParseFacebookUtils.initialize(CatchupApplication.this);
    }
}
