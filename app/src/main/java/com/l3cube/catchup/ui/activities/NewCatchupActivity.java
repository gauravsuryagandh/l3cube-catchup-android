package com.l3cube.catchup.ui.activities;

import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.l3cube.catchup.R;
import com.l3cube.catchup.models.Person;
import com.l3cube.catchup.ui.adapters.InvitedListAdapter;
import com.l3cube.catchup.ui.decorators.SpacesItemDecoration;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.Calendar;

public class NewCatchupActivity extends AppCompatActivity {
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private StringBuilder mDate;
    private StringBuilder mTime;
    static final int DATE_PICKER_ID = 1111;
    static final int TIME_DIALOG_ID = 2222;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 8956;
    private static final String TAG = NewCatchupActivity.class.getSimpleName();
    private Button mInviteContacts;
    private List<Person> invitedList = new ArrayList<Person>(); // this list will store the name,number of invited ppl
    private Button createCatchup, createTimePoll, createPlacePoll;
    private TextView selectDate;
    private TextView selectTime;
    private EditText mTitle;
    private EditText mPlace;
    private RecyclerView mRecyclerView;
    private InvitedListAdapter mInvitedListAdapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_catchup);

        setupVariables();
        Toast.makeText(getApplicationContext(), getIntent().getStringExtra("operation"), Toast.LENGTH_SHORT).show();
        if(getIntent().getStringExtra("operation").equals("update")){
            createCatchup.setText("Update");
            ParseQuery<ParseObject> query = ParseQuery.getQuery("CatchupParse");
            query.whereEqualTo("objectId", getIntent().getStringExtra("objectId"));
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(final ParseObject object, ParseException e) {
                    if(e == null){
                        Toast.makeText(getApplicationContext(), object.getString("title"), Toast.LENGTH_SHORT).show();
                        TextView title = (TextView) findViewById(R.id.et_new_catchup_title);
                        title.setText(object.getString("title"));
                        selectDate.setText(object.getString("date"));
                        selectTime.setText(object.getString("time"));
                        TextView place = (TextView) findViewById(R.id.et_new_catchup_place);
                        place.setText(object.getString("place"));
                        List<String> invitedNos = (ArrayList<String>) object.get("invited");
                        for (int i=0; i<invitedNos.size();i++){
                            invitedList.add(new Person("", invitedNos.get(i)));
                            showCurrentInvitedList();
                            notifyInvitedListAdapter();
                        }
                        setupListeners();
                    }else{
                        Log.d("CatchupDetails", "Error: " + e.getMessage());
                    }
                }
            });
        } else {
            setupListeners();
        }
    }

    private void setupListeners() {
        selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_PICKER_ID);
            }
        });

        selectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(TIME_DIALOG_ID);
            }
        });

        View.OnClickListener inviteContactsListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startInviteContactsFlow();
            }
        };

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(16));
        mRecyclerView.setAdapter(mInvitedListAdapter);

        mInviteContacts.setOnClickListener(inviteContactsListener);
        createCatchup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTitle =(EditText) findViewById(R.id.et_new_catchup_title);
                mPlace = (EditText) findViewById(R.id.et_new_catchup_place);
                String title = null,inviter = null,place = null,date = null,time = null;
                if (getIntent().getStringExtra("operation").equals("update")) {
                    ParseQuery<ParseObject> query = ParseQuery.getQuery("CatchupParse");
                    query.getInBackground(getIntent().getStringExtra("objectId"), new GetCallback<ParseObject>() {
                        @Override
                        public void done(final ParseObject object, ParseException e) {
                            if (e==null){
                                object.put("title", mTitle.getText().toString());
                                if (!String.valueOf(mDate).equals("null"))
                                    object.put("date", mDate.toString());
                                if (!String.valueOf(mTime).equals("null"))
                                    object.put("time", mTime.toString());
                                object.put("place", mPlace.getText().toString());
                                String[] invitedIds = new String[invitedList.size()];
                                int i = 0;
                                for (final Person person: invitedList){
                                    invitedIds[i++] = person.getPhone();
                                }
                                object.put("invited", Arrays.asList(invitedIds));
                                object.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e==null){
                                            Intent intent = new Intent(NewCatchupActivity.this, CatchupDetailsActivity.class);
                                            intent.putExtra("objectId", object.getObjectId());
                                            startActivity(intent);
                                        }
                                    }
                                });
                            }
                        }
                    });
                } else {
                    // Create Catchup on Server
                    title = mTitle.getText().toString();
                    if (ParseUser.getCurrentUser()!=null) {
                        inviter = new String(ParseUser.getCurrentUser().getString("firstName")
                                .concat(" ")
                                .concat(ParseUser.getCurrentUser().getString("lastName"))
                        );
                    } else {
                        inviter = "Gaurav Suryagandh";
                    }
                    date = String.valueOf(mDate);
                    time = String.valueOf(mTime);
                    place = mPlace.getText().toString();
                    createCatchupOnServer(title,inviter,date,time,place);
                }
            }
        });

        createTimePoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int type = 1;
                Intent intent = new Intent(NewCatchupActivity.this, VotingActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setupVariables() {

        selectDate = (TextView) findViewById(R.id.tv_new_catchup_date);
        selectTime = (TextView) findViewById(R.id.tv_new_catchup_time);
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        mInviteContacts = (Button) findViewById(R.id.btn_invite_contacts);
        createCatchup = (Button) findViewById(R.id.btn_create_catchup);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_invited_contacts_list);
        mInvitedListAdapter = new InvitedListAdapter(invitedList);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        createPlacePoll = (Button) findViewById(R.id.btn_place_poll);
        createTimePoll = (Button) findViewById(R.id.btn_time_poll);

    }

    private void createCatchupOnServer(String title, String inviter, String date, String time, String place) {
        String[] invitedIds = new String[invitedList.size()];
        int i =0;
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
        final ParseObject catchupParse = new ParseObject("CatchupParse");

        catchupParse.put("title",title);
        catchupParse.put("inviter",inviter);
        catchupParse.put("inviterId", ParseUser.getCurrentUser().getObjectId());
        catchupParse.put("date",date);
        catchupParse.put("time",time);
        catchupParse.put("place",place);

        for (final Person person: invitedList){
            invitedIds[i++] = person.getPhone();
        }

        catchupParse.put("invited", Arrays.asList(invitedIds));
        catchupParse.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i(TAG, "done: Created CatchupParse");
                    Toast.makeText(NewCatchupActivity.this, "Created Catchup Successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(NewCatchupActivity.this, CatchupDetailsActivity.class);
                    intent.putExtra("objectId", catchupParse.getObjectId());
                    startActivity(intent);
                } else {
                    Log.e(TAG, "done: Error: " + e.getMessage() );
                }
            }
        });
    }

    private void startInviteContactsFlow() {

        Activity thisActivity = NewCatchupActivity.this;
        if (ContextCompat.checkSelfPermission(thisActivity,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                    Manifest.permission.READ_CONTACTS)) {
                //Toast.makeText(NewCatchupActivity.this, "Should show request rationale", Toast.LENGTH_SHORT).show();

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                //Toast.makeText(NewCatchupActivity.this, "Directly ask for permissions", Toast.LENGTH_SHORT).show();
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(NewCatchupActivity.this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            Toast.makeText(NewCatchupActivity.this, "Already granted permissions", Toast.LENGTH_SHORT).show();
            pickContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i("TAG", "onRequestPermissionsResult: onRequestPermissionsResult called");
            switch (requestCode) {
                case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.i(TAG, "onRequestPermissionsResult: Granted");

                        // permission was granted, yay! Do the
                        // contacts-related task you need to do.
                        pickContacts();

                    } else {
                        Log.i(TAG, "onRequestPermissionsResult: Not Granted");

                        // permission denied, boo! Disable the
                        // functionality that depends on this permission.
                    }
                }

                // other 'case' lines to check for other
                // permissions this app might request
            }
    }

    private void pickContacts() {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(pickContactIntent,2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2) {
         //show contact picked
            Log.i(TAG, "onActivityResult: Picked Contact was" + data.toString());
            Person personToBeAdded = extractPersonFromData(data);
            if (personToBeAdded != null) {
                addPersonToInvitedList(personToBeAdded);
            }
        }
    }

    private void addPersonToInvitedList(Person personToBeAdded) {
        invitedList.add(personToBeAdded);
        showCurrentInvitedList();
        notifyInvitedListAdapter();
    }

    private void notifyInvitedListAdapter() {
        mInvitedListAdapter.notifyDataSetChanged();
    }

    private void showCurrentInvitedList() {
        for (int personIndex = 0; personIndex < invitedList.size(); personIndex++) {
            Log.i(TAG, "showCurrentInvitedList: InvitedList index " + personIndex + " is "
                    + invitedList.get(personIndex).getName() + " : " + invitedList.get(personIndex).getPhone());
        }
    }

    private Person extractPersonFromData(Intent data) {
        Cursor cursor = null;
        try {
            String phoneNo = null;
            String name = null;
            byte[] photo = null;

            // getData() method will have the Content Uri of the selected contact
            Uri uri = data.getData();
            //Query the content uri
            cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                // column index of the phone number
                int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                // column index of the contact name
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

                phoneNo = cursor.getString(phoneIndex);
                name = cursor.getString(nameIndex);

                Log.i(TAG, "extractPersonFromData: Phone = " + phoneNo);
                Log.i(TAG, "extractPersonFromData: Name = " + name);

                return new Person(name,phoneNo);

            } else {
                Toast.makeText(NewCatchupActivity.this, "An error occured. Please try again.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "extractPersonFromData: Cursor was null");

                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
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
            case TIME_DIALOG_ID:

                // set time picker as current time
                return new TimePickerDialog(this, timePickerListener, hour, minute,false);
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
            selectDate.setText(mDate);

        }
    };

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {


        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            // TODO Auto-generated method stub
            hour   = hourOfDay;
            minute = minutes;

            mTime = new StringBuilder().append(utilTime(hour))
                    .append(":").append(utilTime(minute));
            // set current time into output textview
            selectTime.setText(mTime);
        }

    };

    private static String utilTime(int value) {

        if (value < 10)
            return "0" + String.valueOf(value);
        else
            return String.valueOf(value);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.newcatchup_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.new_overflow_menu:
                View menuItemView = findViewById(R.id.new_overflow_menu);
                PopupMenu popupMenu = new PopupMenu(this, menuItemView);
                popupMenu.inflate(R.menu.overflow_menu);
                popupMenu.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
