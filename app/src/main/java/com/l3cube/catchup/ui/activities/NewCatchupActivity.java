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
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.l3cube.catchup.R;
import com.l3cube.catchup.models.Person;
import com.l3cube.catchup.ui.CatchupDetailsAlternateActivity;
import com.l3cube.catchup.ui.adapters.InvitedListAdapter;
import com.l3cube.catchup.ui.decorators.SpacesItemDecoration;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.Random;

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
    private Button createCatchup;
    private TextView selectDate;
    private TextView selectTime;
    private TextView mTitle;
    private EditText mEnterPlace;
    private Button mPlace;
    private RecyclerView mRecyclerView;
    private InvitedListAdapter mInvitedListAdapter;
    RecyclerView.LayoutManager layoutManager;
    private final int PLACE_PICKER_REQUEST = 4223;
    Place pickedPlace = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_catchup);

        setupVariables();

        if(getIntent().getStringExtra("operation").equals("update")){
            createCatchup.setText("Update");
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Catchup");
            query.whereEqualTo("objectId", getIntent().getStringExtra("objectId"));
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(final ParseObject object, ParseException e) {
                    if(e == null){
                        TextView title = (TextView) findViewById(R.id.et_new_catchup_title);
                        title.setText(object.getString("title"));
                        selectDate.setText(object.getString("date"));
                        selectTime.setText(object.getString("time"));
                        mEnterPlace.setText(object.getString("placeName"));
                        List<ParseObject> invited = (ArrayList<ParseObject>) object.get("invited");
                        for (int i=0; i<invited.size();i++){
                            try {
                                invitedList.add(new Person(invited.get(i).fetchIfNeeded().getString("firstName") + " " + invited.get(i).fetchIfNeeded().getString("lastName"), invited.get(i).getString("mobileNumber")));
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(getIntent().getStringExtra("operation").equals("update")) {
            Intent intent = new Intent(NewCatchupActivity.this, CatchupDetailsAlternateActivity.class);
            intent.putExtra("objectId", getIntent().getStringExtra("objectId"));
            startActivity(intent);
        } else
            startActivity(new Intent(NewCatchupActivity.this, MainActivity.class));
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

        mPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(NewCatchupActivity.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e){

                } catch (GooglePlayServicesNotAvailableException e){

                }
            }
        });
        mInviteContacts.setOnClickListener(inviteContactsListener);

        createCatchup.setOnClickListener(new View.OnClickListener() {
            public String title = mTitle.getText().toString() ;

            @Override
            public void onClick(View view) {
                mTitle =(EditText) findViewById(R.id.et_new_catchup_title);
                if (mTitle.getText().toString().trim().equals("")) {
                    Toast.makeText(getApplicationContext(), "Title should not be empty", Toast.LENGTH_SHORT).show();

                } else if (selectDate.getText().toString().equals("Select Date")) {
                    Toast.makeText(getApplicationContext(), "Select a Date ", Toast.LENGTH_SHORT).show();
                } else if (selectTime.getText().toString().trim().equals("Select Time")) {
                    Toast.makeText(getApplicationContext(), "Select a Time", Toast.LENGTH_SHORT).show();
                } else {
                    String date = null,time = null;
                    if (getIntent().getStringExtra("operation").equals("update")) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Catchup");
                        query.getInBackground(getIntent().getStringExtra("objectId"), new GetCallback<ParseObject>() {
                            @Override
                            public void done(final ParseObject object, ParseException e) {
                                if (e==null){
                                    JSONArray jsonArray = new JSONArray();
                                    JSONObject place = new JSONObject();
                                    JSONArray timesJSONArray = new JSONArray();
                                    JSONObject pickedTime = new JSONObject();
                                    Random random = new Random();
                                    String randomId = String.valueOf(100000 + random.nextInt(900000));

                                    object.put("title", mTitle.getText().toString());
                                    if (!String.valueOf(mDate).equals("null"))
                                        object.put("date", mDate.toString());
                                    if (!String.valueOf(mTime).equals("null"))
                                        object.put("time", mTime.toString());
                                    object.put("placeName", mEnterPlace.getText().toString());
                                    try {
                                        if (!String.valueOf(mTime).equals("null"))
                                            pickedTime.put("name", mTime.toString());
                                        pickedTime.put("id", randomId);
                                        pickedTime.put("votes", new JSONArray());

                                        timesJSONArray.put(timesJSONArray.length(), pickedTime);
                                        object.put("timesJSONArray", timesJSONArray);
                                    } catch (JSONException e6) {
                                        e6.printStackTrace();
                                    }
                                    if (pickedPlace!=null) {
                                        try {
                                            place.put("name",mEnterPlace.getText().toString());
                                            place.put("latitude",pickedPlace.getLatLng().latitude);
                                            place.put("longitude",pickedPlace.getLatLng().longitude);
                                            place.put("address",pickedPlace.getAddress());
                                            place.put("id",pickedPlace.getId());
                                            place.put("votes", new JSONArray());

                                            jsonArray.put(jsonArray.length(),place);
                                            object.put("placesJSONArray",jsonArray);
                                        } catch (JSONException e4) {
                                            e4.printStackTrace();
                                        }
                                    } else {
                                        try {
                                            place.put("name",mEnterPlace.getText().toString());
                                            place.put("latitude",0.0);
                                            place.put("longitude",0.0);
                                            place.put("address","NA");
                                            place.put("id", "NA");
                                            place.put("votes", new JSONArray());

                                            jsonArray.put(jsonArray.length(),place);
                                            object.put("placesJSONArray",jsonArray);
                                        } catch (JSONException e5) {
                                            e5.printStackTrace();
                                        }
                                    }
                                    ParseQuery<ParseObject> queryPerson = ParseQuery.getQuery("Person");
                                    List<ParseObject> invited = new ArrayList<ParseObject>();
                                    for ( final Person person: invitedList) {
                                        ParseObject invitedPerson = null;
                                        String cleanedPhone = person.getPhone().replaceAll("\\s", "");
                                        cleanedPhone = cleanedPhone.substring(cleanedPhone.length() - 10);
                                        ParseQuery<ParseObject> userQuery = ParseQuery.getQuery("_User");
                                        String mobileNumber = "+91".concat(cleanedPhone);
                                        userQuery.whereEqualTo("mobileNumber", mobileNumber);
                                        try {
                                            invitedPerson = userQuery.getFirst();
                                            String userId = invitedPerson.getObjectId().toString();
                                            String inviter = ParseUser.getCurrentUser().getString("firstName");

                                            sendnotification(userId, title, inviter);
                                        } catch (ParseException e2) {
                                            e2.printStackTrace();
                                            queryPerson.whereEqualTo("mobileNumber", mobileNumber);
                                            try {
                                                invitedPerson = queryPerson.getFirst();
                                            } catch (ParseException e1) {
                                                e1.printStackTrace();
                                                ParseObject temp = new ParseObject("Person");
                                                temp.put("firstName", person.getName().split(" ")[0]);
                                                if (person.getName().split(" ").length > 1) {
                                                    temp.put("lastName", person.getName().split(" ")[1]);
                                                }
                                                temp.put("mobileNumber", mobileNumber);
                                                try {
                                                    temp.save();
                                                    invitedPerson = temp;

                                                } catch (ParseException e3) {
                                                    e3.printStackTrace();
                                                }
                                            }
                                        }
                                        invited.add(invitedPerson);
                                    }
                                    object.put("invited", invited);
                                    object.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e==null){
                                                Intent intent = new Intent(NewCatchupActivity.this, CatchupDetailsAlternateActivity.class);
                                                intent.putExtra("title", mTitle.getText().toString());
                                                intent.putExtra("inviterId",ParseUser.getCurrentUser().getObjectId());
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
                        date = String.valueOf(mDate);
                        time = String.valueOf(mTime);
                        createCatchupOnServer(title,date,time);
                    }
                }
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
        mPlace = (Button) findViewById(R.id.btn_new_catchup_place);
        mEnterPlace = (EditText) findViewById(R.id.tv_enter_a_place);
        mTitle = (TextView) findViewById(R.id.tv_new_catchup_title);
    }

    private void createCatchupOnServer(String title, String date, String time){
        final ParseObject newCatchup = ParseObject.create("Catchup");
        List<ParseObject> invited = new ArrayList<ParseObject>();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        ParseQuery<ParseObject> queryPerson = ParseQuery.getQuery("Person");
        JSONArray jsonArray = new JSONArray();
        JSONObject place = new JSONObject();
        JSONArray timesJSONArray = new JSONArray();
        JSONObject pickedTime = new JSONObject();
        Random random = new Random();
        String randomId = String.valueOf(100000 + random.nextInt(900000));

        newCatchup.put("title", title);
        newCatchup.put("inviter", ParseUser.getCurrentUser());
        newCatchup.put("date", date);
        newCatchup.put("time", time);
        try {
            Toast.makeText(this, time, Toast.LENGTH_SHORT).show();
            pickedTime.put("name", time);
            pickedTime.put("id", randomId);
            pickedTime.put("votes", new JSONArray());

            timesJSONArray.put(timesJSONArray.length(), pickedTime);
            newCatchup.put("timesJSONArray", timesJSONArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        newCatchup.put("placeName", mEnterPlace.getText().toString());
        if (pickedPlace!=null) {
            try {
                place.put("name",mEnterPlace.getText().toString());
                place.put("latitude",pickedPlace.getLatLng().latitude);
                place.put("longitude",pickedPlace.getLatLng().longitude);
                place.put("address",pickedPlace.getAddress());
                place.put("id",pickedPlace.getId());
                place.put("votes",new JSONArray());

                jsonArray.put(jsonArray.length(),place);
                newCatchup.put("placesJSONArray",jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                place.put("name",mEnterPlace.getText().toString());
                place.put("latitude",0.0);
                place.put("longitude",0.0);
                place.put("address","NA");
                place.put("id", "NA");
                place.put("votes",new JSONArray());

                jsonArray.put(jsonArray.length(),place);
                newCatchup.put("placesJSONArray",jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        for ( final Person person: invitedList){
            ParseObject invitedPerson = null;
            String cleanedPhone = person.getPhone().replaceAll("\\s","");
            cleanedPhone = cleanedPhone.substring(cleanedPhone.length() - 10);
            query.whereEqualTo("mobileNumber", "+91".concat(cleanedPhone));
            try {
                invitedPerson = query.getFirst();
                String userId = invitedPerson.getObjectId().toString();
                String inviter = ParseUser.getCurrentUser().getString("firstName");

                sendnotification(userId ,title,inviter);
            } catch (ParseException e) {
                e.printStackTrace();
                queryPerson.whereEqualTo("mobileNumber", "+91".concat(cleanedPhone));
                try {
                    invitedPerson = queryPerson.getFirst();
                } catch (ParseException e1) {
                    e1.printStackTrace();
                    ParseObject temp = new ParseObject("Person");
                    temp.put("firstName", person.getName().split(" ")[0]);
                    if (person.getName().split(" ").length > 1) {
                        temp.put("lastName", person.getName().split(" ")[1]);
                    }
                    temp.put("mobileNumber", "+91".concat(cleanedPhone));
                    try {
                        temp.save();
                        invitedPerson = temp;

                    } catch (ParseException e2) {
                        e2.printStackTrace();
                    }
                }
            }
            invited.add(invitedPerson);
        }
        newCatchup.put("invited", invited);
        newCatchup.put("going", new ArrayList<ParseObject>());
        newCatchup.put("notGoing", new ArrayList<ParseObject>());

        newCatchup.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null){
                    Log.i(TAG, "done: Created CatchupParse");
                    Toast.makeText(NewCatchupActivity.this, "Created Catchup Successfully", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(NewCatchupActivity.this, CatchupDetailsAlternateActivity.class);
                    intent.putExtra("title", mTitle.getText().toString());
                    intent.putExtra("inviterId",ParseUser.getCurrentUser().getObjectId());
                    intent.putExtra("objectId", newCatchup.getObjectId());
                    startActivity(intent);
                } else {
                    Log.e(TAG, "done: Error: " + e.getMessage() );
                }
            }
        });
    }

    private void sendnotification(String objectId, String title, String inviter)
    {

        ParsePush parsePush = new ParsePush();
        String message = "You are invited to " + title.toUpperCase() +" by " + inviter;
        parsePush.setMessage(message);
        ParseQuery <ParseInstallation> installquery = ParseInstallation.getQuery();

        installquery.whereEqualTo("userId",objectId);
        parsePush.setQuery(installquery);
        parsePush.sendInBackground();
    }

    private void startInviteContactsFlow() {

        Activity thisActivity = NewCatchupActivity.this;
        if (ContextCompat.checkSelfPermission(thisActivity,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                    Manifest.permission.READ_CONTACTS)) {

            } else {
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(NewCatchupActivity.this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
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
        } else if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                pickedPlace = PlacePicker.getPlace(data, this);
                mEnterPlace.setText(pickedPlace.getName());
                String toastMsg = String.format("Place: %s", pickedPlace.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, pickerListener, year, month,day);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
                return datePickerDialog;

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
            String dday = null;
            String mmonth = month(month);

            if(day == 1 || day == 21 || day == 31)
            {
                dday = day + "st";
            }

            else if(day == 2 || day == 22)
            {
                dday = day + "nd";
            }

            else if(day == 3 || day == 23)
            {
                dday = day + "rd";
            }

            else
            {
                dday = day + "th";
            }


            mDate = new StringBuilder()
                    .append(dday).append("  ").append(mmonth).append("  ")
                    .append(year).append(" ");
            // Show selected date
            selectDate.setText(mDate);


        }
    };

    public String month(int month)
    {
        String mmonth=null;
        switch (month)
        {
            case 0:
                mmonth = "January";
                return mmonth;
            case 1:
                mmonth = "February";
                return mmonth;
            case 2:
                mmonth = "March";
                return mmonth;
            case 3:
                mmonth = "April";
                return mmonth;
            case 4:
                mmonth = "May";
                return mmonth;
            case 5:
                mmonth = "June";
                return mmonth;
            case 6:
                mmonth = "July";
                return mmonth;
            case 7:
                mmonth = "August";
                return mmonth;
            case 8:
                mmonth = "September";
                return mmonth;
            case 9:
                mmonth = "October";
                return mmonth;
            case 10:
                mmonth = "November";
                return mmonth;
            default:
                mmonth = "December";
                return mmonth;
        }

    }

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {


        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            // TODO Auto-generated method stub
            hour   = hourOfDay;
            minute = minutes;

            if (hour < 12)
            {
                mTime = new StringBuilder().append(utilTime(hour))
                        .append(":").append(utilTime(minute)).append(" AM");
            }

            else if(hour>12)
            {
                hour = hour-12;
                mTime = new StringBuilder().append(utilTime(hour))
                        .append(":").append(utilTime(minute)).append(" PM");
            }
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
