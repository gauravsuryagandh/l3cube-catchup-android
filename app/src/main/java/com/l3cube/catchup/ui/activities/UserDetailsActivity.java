package com.l3cube.catchup.ui.activities;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.l3cube.catchup.R;
import com.l3cube.catchup.models.Person;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class UserDetailsActivity extends AppCompatActivity {

    TextView fname = (TextView) findViewById(R.id.firstNameLabel);
    TextView lname = (TextView) findViewById(R.id.lastNameLabel);
    TextView email = (TextView) findViewById(R.id.emailAddressLabel);
    TextView birthDate = (TextView) findViewById(R.id.birthDateLabel);
    private static final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        fname = (TextView) findViewById(R.id.firstNameLabel);
        lname = (TextView) findViewById(R.id.lastNameLabel);
        email = (TextView) findViewById(R.id.emailAddressLabel);
        birthDate = (TextView) findViewById(R.id.birthDateLabel);
        lname.setText(ParseUser.getCurrentUser().getString("lastName"));
        email.setText(ParseUser.getCurrentUser().getString("emailId"));
        birthDate.setText(ParseUser.getCurrentUser().getString("birthDate"));
        fname.setText(ParseUser.getCurrentUser().getString("firstName"));

        DigitsAuthButton digitsAuthButton = (DigitsAuthButton) findViewById(R.id.btn_digits_auth);
        digitsAuthButton.setCallback(new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                ParseUser user = ParseUser.getCurrentUser();
                user.put("mobileNumber", phoneNumber);
                user.put("digitsAuth",session.hashCode());
                user.put("firstName", fname.getText());
                user.put("lastName", lname.getText());
                user.put("emailId", email.getText());
                user.put("birthDate", birthDate.getText());
                user.saveInBackground();
                startActivity(new Intent(UserDetailsActivity.this, NewCatchupActivity.class));

            }

            @Override
            public void failure(DigitsException error) {
                Log.d("digits", error.getMessage());
            }
        });
    }
}
