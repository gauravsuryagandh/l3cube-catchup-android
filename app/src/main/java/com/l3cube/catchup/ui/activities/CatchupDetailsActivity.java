package com.l3cube.catchup.ui.activities;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.l3cube.catchup.R;
import com.l3cube.catchup.models.Person;
import com.l3cube.catchup.ui.adapters.ExpandableListAdapter;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.uber.sdk.android.rides.RideRequestButton;

import static android.widget.Toast.LENGTH_SHORT;

public class CatchupDetailsActivity extends AppCompatActivity {

    ExpandableListView mExpandableListView;
    ExpandableListAdapter mExpandableListAdapter;
    List<String> mExpandableListTitle;
    HashMap<String, ArrayList<ParseObject>> mExpandableListDetail;
    TextView mCatchupTitle;
    TextView mCatchupTime;
    TextView mCatchupDate;
    TextView mCatchupPlace;
    List<Person> mContactList;
    private FloatingActionButton mFloatingActionButton;
    RideRequestButton requestButton;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catchup_details);
//       ActionBar actionBar = getActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
        setupVariables();
        setupData();


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CatchupDetailsActivity.this, MainActivity.class));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.catchupdetails_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.delete_from_details:
                AlertDialog dialog_box =AskOption();
                dialog_box.show();

                return true;

            case R.id.det_overflow_menu:
                View menuItemView = findViewById(R.id.det_overflow_menu);
                PopupMenu popupMenu = new PopupMenu(this, menuItemView);
                popupMenu.inflate(R.menu.overflow_menu);
                popupMenu.show();
                return true;
//            case R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
//                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private AlertDialog AskOption()
    {
        AlertDialog myQuittingDialogBox =new AlertDialog.Builder(this)
                //set message, title, and icon
                .setTitle("Delete")
                .setMessage("Are you sure you want to Delete the Catch-Up?")
//                .setIcon(R.drawable.delete)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Catchup");
                        query.whereEqualTo("objectId",getIntent().getStringExtra("objectId"));
                        query.getFirstInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject object, ParseException e) {
                                if (e == null){
                                    object.deleteInBackground(new DeleteCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if(e == null){
                                                Toast.makeText(getApplicationContext(),"Deleted", LENGTH_SHORT).show();

                                            } else {
                                                Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), LENGTH_SHORT).show();
                                }
                            }
                        });

                        Intent intent = new Intent(CatchupDetailsActivity.this, MainActivity.class);
                        startActivity(intent);


                        dialog.dismiss();
                    }

                })



                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                })
                .create();
        return myQuittingDialogBox;

    }

    private void setupVariables() {
        mExpandableListView = (ExpandableListView) findViewById(R.id.elv_catchup_details);
        mCatchupTitle = (TextView) findViewById(R.id.tv_catchup_details_title);
        mCatchupDate = (TextView) findViewById(R.id.tv_catchup_details_date);
        mCatchupTime = (TextView) findViewById(R.id.discount_offered2);
        mCatchupPlace = (TextView) findViewById(R.id.validitytill2);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab_update_catchup);
        requestButton = new RideRequestButton(CatchupDetailsActivity.this);
        layout = (LinearLayout) findViewById(R.id.ll_catchup_details);
    }

    private void setupData() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Catchup");
        query.whereEqualTo("objectId",getIntent().getStringExtra("objectId"));
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject object, ParseException e) {
                if(e == null){
                    mCatchupTitle.setText(object.getString("title"));
                    mCatchupTime.setText(object.getString("time"));
                    mCatchupDate.setText(object.getString("date"));
                    mCatchupPlace.setText(object.getString("placeName"));
                    mExpandableListDetail = setELVData((ArrayList<ParseObject>) object.get("invited"), (ArrayList<ParseObject>) object.get("going"), (ArrayList<ParseObject>) object.get("notGoing"), object);
                    mExpandableListTitle = new ArrayList<String>(mExpandableListDetail.keySet());
                    mExpandableListTitle.remove(mExpandableListTitle.size()-1);
                    mExpandableListTitle.remove(mExpandableListTitle.size()-1);
                    mExpandableListTitle.remove(mExpandableListTitle.size()-1);
                    mExpandableListAdapter = new ExpandableListAdapter(CatchupDetailsActivity.this, mExpandableListTitle, mExpandableListDetail);
                    try {
                        if (String.valueOf(object.getParseUser("inviter").fetchIfNeeded().getObjectId()).equals(ParseUser.getCurrentUser().getObjectId())){
                            mFloatingActionButton.show();
                            mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(CatchupDetailsActivity.this, NewCatchupActivity.class);
                                    intent.putExtra("operation", "update");
                                    intent.putExtra("objectId", object.getObjectId());
                                    startActivity(intent);
                                }
                            });
                        }
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    mExpandableListView.setAdapter(mExpandableListAdapter);
//                    mExpandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
//                        @Override
//                        public void onGroupExpand(int groupPosition) {
//                            Toast.makeText(getApplicationContext(), mExpandableListTitle.get(groupPosition) + " List Expanded.", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                    mExpandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
//                        @Override
//                        public void onGroupCollapse(int groupPosition) {
//                            Toast.makeText(getApplicationContext(), mExpandableListTitle.get(groupPosition) + " List Collapsed.", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                    mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
//                        @Override
//                        public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {
//                            Toast.makeText(
//                                    getApplicationContext(),
//                                    mExpandableListTitle.get(groupPosition)
//                                            + " -> "
//                                            + mExpandableListDetail.get(
//                                            mExpandableListTitle.get(groupPosition)).get(
//                                            childPosition), Toast.LENGTH_SHORT
//                            ).show();
//                            return false;
//                        }
//                    });
                    mExpandableListView.expandGroup(0);
                }else{
                    Log.d("CatchupDetails", "Error: " + e.getMessage());
                }
            }
        });
        layout.addView(requestButton);
    }

    private HashMap<String, ArrayList<ParseObject>> setELVData(ArrayList<ParseObject> invited, ArrayList<ParseObject> going, ArrayList<ParseObject> notGoing, ParseObject object) {
        HashMap<String, ArrayList<ParseObject>> expandableListDetail = new HashMap<>();

//        ArrayList<ParseObject> football = new ArrayList<>();
//        football.add("Brazil");
//        football.add("Spain");
//        football.add("Germany");
//        football.add("Netherlands");
//        football.add("Italy");
//
//        ArrayList<ParseObject> basketball = new ArrayList<>();
//        basketball.add("United States");
//        basketball.add("Spain");
//        basketball.add("Argentina");
//        basketball.add("France");
//        basketball.add("Russia");

        expandableListDetail.put("Invited Contacts", invited);
        expandableListDetail.put("RSVPed Yes", going);
        expandableListDetail.put("RSVPed No", notGoing);
        ArrayList<ParseObject> catchup = new ArrayList<ParseObject>();
        try {
            catchup.add(object.fetchIfNeeded());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "val:"+catchup.size(), Toast.LENGTH_SHORT).show();
        expandableListDetail.put("Object", catchup);
//        expandableListDetail.put("Choose pickedPlace", football);
//        expandableListDetail.put("Choose time", basketball);
        return expandableListDetail;
    }
}