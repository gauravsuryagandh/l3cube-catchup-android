package com.l3cube.catchup.ui.activities;

import android.content.DialogInterface;
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

import static android.widget.Toast.LENGTH_SHORT;

public class CatchupDetailsActivity extends AppCompatActivity {

    ExpandableListView mExpandableListView;
    ExpandableListAdapter mExpandableListAdapter;
    List<String> mExpandableListTitle;
    HashMap<String, List<String>> mExpandableListDetail;
    TextView mCatchupTitle;
    TextView mCatchupTime;
    TextView mCatchupDate;
    TextView mCatchupPlace;
    List<Person> mContactList;
    private FloatingActionButton mFloatingActionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catchup_details);

        setupVariables();
        setupData();
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
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("CatchupParse");
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
        mCatchupTime = (TextView) findViewById(R.id.catchup_details_time);
        mCatchupPlace = (TextView) findViewById(R.id.tv_catchup_details_place);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab_update_catchup);
    }

    private void setupData() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("CatchupParse");
        query.whereEqualTo("objectId",getIntent().getStringExtra("objectId"));
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(final ParseObject object, ParseException e) {
                if(e == null){
                    mCatchupTitle.setText(object.getString("title"));
                    mCatchupTime.setText(object.getString("time"));
                    mCatchupDate.setText(object.getString("date"));
                    mCatchupPlace.setText(object.getString("place"));
                    mExpandableListDetail = setELVData((ArrayList<String>) object.get("invited"));
                    mExpandableListTitle = new ArrayList<String>(mExpandableListDetail.keySet());
                    mExpandableListAdapter = new ExpandableListAdapter(CatchupDetailsActivity.this, mExpandableListTitle, mExpandableListDetail);
                    if (object.getString("inviterId").equals(ParseUser.getCurrentUser().getObjectId())){
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
    }

    private HashMap<String, List<String>> setELVData(ArrayList<String> invited) {
        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();

        List<String> football = new ArrayList<String>();
        football.add("Brazil");
        football.add("Spain");
        football.add("Germany");
        football.add("Netherlands");
        football.add("Italy");

        List<String> basketball = new ArrayList<String>();
        basketball.add("United States");
        basketball.add("Spain");
        basketball.add("Argentina");
        basketball.add("France");
        basketball.add("Russia");

        expandableListDetail.put("Invited Contacts", invited);
        expandableListDetail.put("Choose place", football);
        expandableListDetail.put("Choose time", basketball);
        return expandableListDetail;
    }


}