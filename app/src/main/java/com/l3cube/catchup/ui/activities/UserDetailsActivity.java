package com.l3cube.catchup.ui.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.util.Calendar;

public class UserDetailsActivity extends AppCompatActivity {
    static final int DATE_PICKER_ID = 1111;

    TextView fname;
    TextView lname;
    TextView email;
    TextView birthDate;
    private StringBuilder mDate;
    final Calendar c = Calendar.getInstance();
    int year = c.get(Calendar.YEAR);
    int month = c.get(Calendar.YEAR);
    int day = c.get(Calendar.DAY_OF_MONTH);

            //= (TextView) findViewById(R.id.firstNameLabel);
    //TextView lname = (TextView) findViewById(R.id.lastNameLabel);
    //TextView email = (TextView) findViewById(R.id.emailAddressLabel);
    //TextView birthDate = (TextView) findViewById(R.id.birthDateLabel);
    private static final String TAG = MainActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        fname = (TextView) findViewById(R.id.firstNameLabel);
        lname = (TextView) findViewById(R.id.lastNameLabel);
        email = (TextView) findViewById(R.id.emailAddressLabel);
        birthDate = (TextView) findViewById(R.id.birthDateLabel);



        birthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_PICKER_ID);
            }
        });


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
                user.put("firstName", user.getString("first_name"));
                user.put("lastName", user.getString("last_name"));
                user.put("emailId", user.getString("emailId"));
                user.put("birthDate", user.getString("birthDate"));
                user.put("fbId",user.getString("id"));
                user.saveInBackground();
                startActivity(new Intent(UserDetailsActivity.this, NewCatchupActivity.class));

            }

            @Override
            public void failure(DigitsException error) {
                Log.d("digits", error.getMessage());
            }
        });
    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:

                // open datepicker dialog.
                // set date picker for current date
                // add pickerListener listner to date picker
                return new DatePickerDialog(this, pickerListener, year, month,day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year  = selectedYear;
            month = selectedMonth;
            day   = selectedDay;

            mDate = new StringBuilder()
                    .append(day).append("-").append(month + 1).append("-")
                    .append(year).append(" ");
            // Show selected date
            birthDate.setText(mDate);

        }
    };
}
