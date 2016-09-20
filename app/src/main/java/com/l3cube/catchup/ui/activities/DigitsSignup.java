package com.l3cube.catchup.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.digits.sdk.android.SessionListener;
import com.l3cube.catchup.R;
import com.parse.ParseUser;

public class DigitsSignup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_digits_signup);

        DigitsAuthButton digitsAuthButton = (DigitsAuthButton) findViewById(R.id.btn_digits_auth);
        digitsAuthButton.setCallback(new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                ParseUser user = ParseUser.getCurrentUser();
                user.put("mobileNumber", phoneNumber);
                user.put("digitsAuth",session.hashCode());
                user.saveInBackground();
                startActivity(new Intent(DigitsSignup.this, NewCatchupActivity.class));

            }

            @Override
            public void failure(DigitsException error) {
                Log.d("digits", error.getMessage());
            }
        });
    }
}
