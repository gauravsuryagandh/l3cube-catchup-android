package com.l3cube.catchup.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.l3cube.catchup.R;
import com.l3cube.catchup.models.Catchup;
import com.l3cube.catchup.models.Person;
import com.l3cube.catchup.ui.adapters.ContactListAdapter;
import com.l3cube.catchup.ui.decorators.SpacesItemDecoration;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CatchupDetailsActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private TextView mCatchupTitle, mCatchupDate, mCatchupTime;
    private ContactListAdapter mContactListAdapter;
    private List<Person> mContactList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catchupdetails);
        setupVariables();
        populateContacts();
    }

    private void setupVariables() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_contacts_list);
        mCatchupTitle = (TextView) findViewById(R.id.tv_catchup_detail_title);
        mCatchupDate = (TextView) findViewById(R.id.tv_catchup_detail_date);
        mCatchupTime = (TextView) findViewById(R.id.tv_catchup_detail_time);
        mContactListAdapter = new ContactListAdapter(mContactList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(16));
        mRecyclerView.setAdapter(mContactListAdapter);
    }

    private void populateContacts() {
        //K0sglFv6Ix
        ParseQuery<ParseObject> query = ParseQuery.getQuery("CatchupParse");
        query.whereEqualTo("objectId",getIntent().getStringExtra("objectId"));
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject object, ParseException e) {
                if(e == null){
                    Log.d("K0sglFv6Ix", object.get("invited").toString());
                    ArrayList<String> invited = (ArrayList<String>) object.get("invited");
                    Person contact;
                    for (int i = 0; i < invited.size(); i++) {
                        contact = new Person(
                                "name",
                                invited.get(i)
                        );
                        mContactList.add(contact);
                    }
                    notifyContactListAdapter();
                    mCatchupTitle.setText(object.getString("title"));
                    mCatchupTime.setText(object.getString("time"));
                    mCatchupDate.setText(object.getString("date"));
                }else{
                    Log.d("K0sglFv6Ix", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void notifyContactListAdapter() {
        mContactListAdapter.notifyDataSetChanged();
    }
}
