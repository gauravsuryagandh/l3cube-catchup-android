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

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);

        TextView fname = (TextView) findViewById(R.id.firstNameLabel);
        TextView lname = (TextView) findViewById(R.id.lastNameLabel);
        TextView email = (TextView) findViewById(R.id.emailAddressLabel);
        TextView birthDate = (TextView) findViewById(R.id.birthDateLabel);
        lname.setText(ParseUser.getCurrentUser().getString("lastName"));
        email.setText(ParseUser.getCurrentUser().getString("emailId"));
        birthDate.setText(ParseUser.getCurrentUser().getString("birthDate"));
        fname.setText(ParseUser.getCurrentUser().getString("firstName"));
    }

}
