package com.l3cube.catchup.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.l3cube.catchup.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SignupActivity.this, MainActivity.class));
    }

    private void loginWithFacebook() {
        List<String> permissions = Arrays.asList("public_profile", "email");
        ParseFacebookUtils.logInWithReadPermissionsInBackground(this, permissions, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (err == null) {
                    if (user == null) {
                        Log.e(TAG, "User was null");
                    } else if (user.isNew()) {
                        Log.d(TAG, "User signed up and logged in through Facebook!");
                        setUserData(user);
                    } else {
                        Log.d(TAG, "User logged in through Facebook!");
                        if (ParseUser.getCurrentUser().getInt("digitsAuth")==0) {
                            startActivity(new Intent(SignupActivity.this, GetUserDetailsActivity.class));
                        } else {
                            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                } else {
                    Log.e(TAG, "done: Error is " + err.getMessage()  );
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);

    }

    private void setUserData(final ParseUser user) {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback(){
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            // Log.d("fb", response.toString()+" "+object.get("email"));
                            user.put("emailId",object.getString("email"));
                            user.put("firstName",object.getString("first_name"));
                            user.put("lastName",object.getString("last_name"));
                            user.put("profilePicture",object.getJSONObject("picture").getJSONObject("data").getString("url"));
                            user.put("fbId",object.getString("id"));
                            user.put("birthDate",object.getString("birthday"));
                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e==null){
                                        Intent intent = new Intent(SignupActivity.this, GetUserDetailsActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        Bundle parameters = new Bundle();
        parameters.putString(
                "fields",
                "first_name, last_name, email, picture.width(480), id, birthday"
        );
        request.setParameters(parameters);
        request.executeAsync();
    }
}
