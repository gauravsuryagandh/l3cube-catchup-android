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
    private static final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);
        String user = String.valueOf(ParseUser.getCurrentUser().getObjectId());
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.whereEqualTo("objectId",user);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
               public void done(ParseObject object, ParseException e) {
                  if (e == null) {
                      TextView fname = (TextView) findViewById(R.id.firstNameLabel);
                      TextView lname = (TextView) findViewById(R.id.lastNameLabel);
                      TextView email = (TextView) findViewById(R.id.emailAddressLabel);
                      TextView birthDate = (TextView) findViewById(R.id.birthDateLabel);
                      String first = object.getString("firstName");
                      fname.setText(first);
                      lname.setText(object.getString("lastName"));
                      email.setText(object.getString("emailId"));
                      birthDate.setText(object.getString("birthDate"));
                  } else {
                      Log.d("K0sglFv6Ix", "Error: " + e.getMessage());
                  }
            }

        });



      //  String currentUser = user.getUsername();
        //String emailid = user.getEmail();

        //fname.setText(user.getString("first_name"));
       // fname.setText(currentUser);


        //lname.setText(user.getString("last_name"));


        //email.setText( user.getString("email"));
         //email.setText(emailid);

       // birthDate.setText(user.getString("birthday"));

       // String email = (String) user1.get("email");
        //Log.d(TAG,"Email" + email);
        //Toast.makeText(UserDetailsActivity.this, "Email" + email, Toast.LENGTH_SHORT).show();



        DigitsAuthButton digitsAuthButton = (DigitsAuthButton) findViewById(R.id.btn_digits_auth);
        digitsAuthButton.setCallback(new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                ParseUser user = ParseUser.getCurrentUser();
                user.put("mobileNumber", phoneNumber);
                user.put("digitsAuth",session.hashCode());
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