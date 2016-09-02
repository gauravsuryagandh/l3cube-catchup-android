package com.l3cube.catchup.ui.activities;

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
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.l3cube.catchup.R;
import com.l3cube.catchup.models.Person;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class NewCatchupActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 8956;
    private static final String TAG = NewCatchupActivity.class.getSimpleName();
    private Button mInviteContacts;
    private List<Person> invitedList = new ArrayList<Person>(); // this list will store the name,number of invited ppl
    private Button createCatchup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_catchup);

        mInviteContacts = (Button) findViewById(R.id.btn_invite_contacts);
        createCatchup = (Button) findViewById(R.id.btn_create_catchup);

        View.OnClickListener inviteContactsListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startInviteContactsFlow();
            }
        };


        mInviteContacts.setOnClickListener(inviteContactsListener);
        createCatchup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create Catchup on Server
                String title = null,inviter = null,place = null,date = null,time = null;
                title = "L3Cube Meet";
                inviter = "Gaurav Suryagandh";
                date = "3rd September";
                time = "4pm";
                place = "The Chaai";
                createCatchupOnServer(title,inviter,date,time,place);
            }
        });

    }

    private void createCatchupOnServer(String title, String inviter, String date, String time, String place) {
        ParseObject catchupParse = new ParseObject("CatchupParse");
        catchupParse.put("title",title);
        catchupParse.put("inviter",inviter);
        catchupParse.put("date",date);
        catchupParse.put("time",time);
        catchupParse.put("place",place);

        catchupParse.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i(TAG, "done: Created CatchupParse");
                    Toast.makeText(NewCatchupActivity.this, "Created Catchup Successfully", Toast.LENGTH_SHORT).show();
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
}
