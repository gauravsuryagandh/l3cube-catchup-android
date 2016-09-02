package com.l3cube.catchup.ui.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import com.l3cube.catchup.R;

import java.util.Calendar;

public class NewcatchupActivity extends AppCompatActivity {
    private EditText mDateObj;
    private int year;
    private int month;
    private int day;
    private StringBuilder mDate;
    static final int DATE_PICKER_ID = 1111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_catchup);

        final Calendar c = Calendar.getInstance();
        year  = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day   = c.get(Calendar.DAY_OF_MONTH);

        mDateObj = (EditText) findViewById(R.id.TFdate);
        mDateObj.setText(mDate);
        mDateObj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_PICKER_ID);
            }
        });
    }

    public void onButtonClick(View v)
    {
        if(v.getId()== R.id.Bcreatedone){
            EditText titleObj =(EditText) findViewById(R.id.TFtitle);
            mDateObj = (EditText) findViewById(R.id.TFdate);
            EditText timeObj = (EditText) findViewById(R.id.TFtime);

            String title = titleObj.getText().toString();
            String time = timeObj.getText().toString();

            Intent i = new Intent(NewcatchupActivity.this,CatchupDetailsActivity.class);
            i.putExtra("title",title);
            i.putExtra("date", (CharSequence) mDate);
            i.putExtra("time",time);
            startActivity(i);
        }
    }

    @Override
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
            mDateObj.setText(mDate);

        }
    };

}
