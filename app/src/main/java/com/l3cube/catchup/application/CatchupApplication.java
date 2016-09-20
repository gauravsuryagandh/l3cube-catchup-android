package com.l3cube.catchup.application;

import android.app.Application;

import com.digits.sdk.android.Digits;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import io.fabric.sdk.android.Fabric;

/**
 * Created by Aditya Shirole on 6/13/2016.
 */
public class CatchupApplication extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "laOPbaUBAjjmTrqKydBFjnGA8";
    private static final String TWITTER_SECRET = "BR13L0whWOqJGl5jVd6D6m4msmWKHJCLJdie7DQ0hW3vBG5Fju";


    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.

    private static final String serverUrl = "http://catchup.us-east-1.elasticbeanstalk.com/parse";
    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new Digits.Builder().build());

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        Parse.enableLocalDatastore(CatchupApplication.this);

        Parse.Configuration configuration = new Parse.Configuration.Builder(CatchupApplication.this)
                .applicationId("a5ffb6374b9b25d0d43d247b153ff03d")
                .clientKey("catchup_client_key")
                .server(serverUrl)
                .build();
        Parse.initialize(configuration);
        ParseFacebookUtils.initialize(CatchupApplication.this);
    }
}
