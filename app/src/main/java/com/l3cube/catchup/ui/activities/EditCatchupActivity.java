package com.l3cube.catchup.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.l3cube.catchup.R;
import com.l3cube.catchup.ui.adapters.ExpandableListAdapter;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.l3cube.catchup.R.id.time;


public class EditCatchupActivity extends AppCompatActivity {

    private TextView mCatchupTitle,mCatchupDate,mCatchupTime,mCatchupPlace;
    private ExpandableListView mExpandableListView;
    ExpandableListAdapter mExpandableListAdapter;
    List<String> mExpandableListTitle;
    HashMap<String, List<String>> mExpandableListDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_catchup);


        mExpandableListView = (ExpandableListView) findViewById(R.id.elv_catchup_details);
        mCatchupTitle = (TextView) findViewById(R.id.catchup_details_title);
        mCatchupDate = (TextView) findViewById(R.id.catchup_details_date);
        mCatchupTime = (TextView) findViewById(R.id.catchup_details_time);
        mCatchupPlace = (TextView) findViewById(R.id.catchup_details_place);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("CatchupParse");
        query.whereEqualTo("objectId",getIntent().getStringExtra("objectId"));
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null){
                    mCatchupTitle.setText(object.getString("title"));
                    mCatchupTime.setText(object.getString("time"));
                    mCatchupDate.setText(object.getString("date"));
                    mCatchupPlace.setText(object.getString("place"));
                    mExpandableListDetail = setELVData((ArrayList<String>) object.get("invited"));
                    mExpandableListTitle = new ArrayList<String>(mExpandableListDetail.keySet());
                    mExpandableListAdapter = new ExpandableListAdapter(EditCatchupActivity.this, mExpandableListTitle, mExpandableListDetail);
                    mExpandableListView.setAdapter(mExpandableListAdapter);
                    mExpandableListView.expandGroup(0);
                }
                else{
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




public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_catchup_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_bar:
                SaveChanges();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void SaveChanges() {

        //ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        //query.whereEqualTo("objectId",getIntent().getStringExtra("objectId"));
        //query.put("title",mCatchupTitle.getText().toString());
        //query.put("inviter",inviter);
        //catchupParse.put("date",date);
        //catchupParse.put("time",time);
        //catchupParse.put("place",place);

        Intent i = new Intent(EditCatchupActivity.this, CatchupDetailsActivity.class);
        startActivity(i);
    }




}
