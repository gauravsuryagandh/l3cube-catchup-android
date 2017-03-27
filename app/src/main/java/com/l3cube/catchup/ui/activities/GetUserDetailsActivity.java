package com.l3cube.catchup.ui.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.l3cube.catchup.R;
import com.parse.ParseUser;

import java.util.Calendar;

public class GetUserDetailsActivity extends AppCompatActivity {
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
                TextView firstName = (TextView) findViewById(R.id.firstNameLabel);
                TextView lastName = (TextView) findViewById(R.id.lastNameLabel);
                TextView emailId = (TextView) findViewById(R.id.emailAddressLabel);
                TextView birthDay = (TextView) findViewById(R.id.birthDateLabel);
                ParseUser user = ParseUser.getCurrentUser();
                user.put("mobileNumber", phoneNumber);
                user.put("digitsAuth",session.hashCode());
                user.put("firstName", firstName.getText().toString());
                user.put("lastName", lastName.getText().toString());
                user.put("emailId", emailId.getText().toString());
                user.put("birthDate", birthDay.getText().toString());
                user.saveInBackground();
                startActivity(new Intent(GetUserDetailsActivity.this, MainActivity.class));

            }

            @Override
            public void failure(DigitsException error) {
                Log.d("digits", error.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(GetUserDetailsActivity.this, SignupActivity.class));
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
