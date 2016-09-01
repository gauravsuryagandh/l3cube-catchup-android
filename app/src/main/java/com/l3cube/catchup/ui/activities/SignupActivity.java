package com.l3cube.catchup.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.l3cube.catchup.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

public class SignupActivity extends AppCompatActivity {
    protected Button mLoginButton;
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mLoginButton = (Button) findViewById(R.id.btn_fb_login);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginWithFacebook();
            }
        });


    }

    private void loginWithFacebook() {
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, null, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (err == null) {
                    if (user == null) {
                        Log.e(TAG, "User was null");
                    } else if (user.isNew()) {
                        Log.d(TAG, "User signed up and logged in through Facebook!");
                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Log.d(TAG, "User logged in through Facebook!");
                        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Log.e(TAG, "done: Error is " + err.getMessage()  );
                }
            }
        });

//        ParseUser user = new ParseUser();
//        user.setUsername("Push");
//        user.setPassword("pass");
//        user.signUpInBackground(new SignUpCallback() {
//            public void done(ParseException e) {
//                if (e == null) {
//                    // Show a simple Toast message upon successful registration
//                    Toast.makeText(getApplicationContext(),
//                            "Successfully Signed up, please log in.",
//                            Toast.LENGTH_LONG).show();
//                    Intent intent = new Intent(SignupActivity.this, MainActivity.class);
//                    startActivity(intent);
//                } else {
//                    Toast.makeText(getApplicationContext(),
//                            "Sign up Error", Toast.LENGTH_LONG)
//                            .show();
//                }
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);

    }
}
