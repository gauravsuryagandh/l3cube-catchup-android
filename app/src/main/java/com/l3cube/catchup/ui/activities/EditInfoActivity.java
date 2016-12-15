package com.l3cube.catchup.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.l3cube.catchup.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class EditInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
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
    }
}
