package com.l3cube.catchup.application;

import android.app.Application;

import com.digits.sdk.android.Digits;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import java.util.LinkedList;

import io.fabric.sdk.android.Fabric;
import com.uber.sdk.android.core.UberSdk;
import com.uber.sdk.core.auth.Scope;
import com.uber.sdk.rides.client.SessionConfiguration;

import java.sql.Array;
import java.util.Arrays;

/**
 * Created by Aditya Shirole on 6/13/2016.
 */
public class CatchupApplication extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "laOPbaUBAjjmTrqKydBFjnGA8";
    private static final String TWITTER_SECRET = "BR13L0whWOqJGl5jVd6D6m4msmWKHJCLJdie7DQ0hW3vBG5Fju";


    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.

    private static final String serverUrl = "https://parseapi.back4app.com/";



    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new Digits.Builder().build());

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        Parse.enableLocalDatastore(CatchupApplication.this);



        Parse.Configuration configuration = new Parse.Configuration.Builder(CatchupApplication.this)
                .applicationId("lt3KuyWfVfQgjAIIRAvPCXnvb4w5d1Vq6G20myPB")
                .clientKey("ggBr6eMC8PzHTfi8BN0Dhhd6bCr2MYnVDApJHWr3")
                .server(serverUrl)
                .build();
        Parse.initialize(configuration);
        ParseFacebookUtils.initialize(CatchupApplication.this);

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        String userId = ParseUser.getCurrentUser().getObjectId().toString();
        installation.put("GCMSenderId","797163850689");

        installation.put("userId",userId);
        installation.saveInBackground();

        SessionConfiguration sessionConfiguration = new SessionConfiguration.Builder()
                .setClientId("LiXZJlZ-SCsyG5WSiwc-4f6kkean5Ryv")
                .setServerToken("gpxUluDH9ZIWY3PuV7Ujhp9BHTf3cGPW1fkUPFRS")
                .setScopes(Arrays.asList(Scope.RIDE_WIDGETS))
                .setEnvironment(SessionConfiguration.Environment.SANDBOX)
                .build();
        UberSdk.initialize(sessionConfiguration);
    }
}
