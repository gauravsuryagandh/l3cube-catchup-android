package com.l3cube.catchup.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.l3cube.catchup.R;
import com.l3cube.catchup.models.Catchup;
import com.l3cube.catchup.ui.adapters.CatchupListAdapter;
import com.l3cube.catchup.ui.decorators.SpacesItemDecoration;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private List<Catchup> mCatchupList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private CatchupListAdapter mCatchupListAdapter;
    private FloatingActionButton mFloatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ParseUser.getCurrentUser() == null && false) {
            navigateToSignUp();
        } else {
            setupVariables();
            populateCatchups();
        }
    }

    private void setupVariables() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_catchup_list);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab_catchup_list);

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToNewCatchup();
            }
        });

        mCatchupListAdapter = new CatchupListAdapter(mCatchupList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(16));
        mRecyclerView.setAdapter(mCatchupListAdapter);


    }

    private void navigateToNewCatchup() {
        Intent intent = new Intent(MainActivity.this, NewCatchupActivity.class);//verify
        startActivity(intent);
    }

    private void navigateToSignUp() {
        Intent intent = new Intent(MainActivity.this, SignupActivity.class);
        startActivity(intent);
    }

    private void populateCatchups() {
        // Use this function to add Hard Coded Catchups
        //addHardCodedCatchups();
        //notifyCatchupsAdapter();

        // Adding from Parse
        addCatchupsFromParse();

    }

    private void addCatchupsFromParse() {
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("CatchupParse");
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> catchupParses, ParseException e) {
                if (e == null) {
                    Log.i(TAG, "done: Found Objects = " + catchupParses.size());
                    if (catchupParses.size() > 0) {
                        Catchup catchup;
                        for (int i = 0; i < catchupParses.size(); i++) {
                            catchup = new Catchup(
                                  R.drawable.image,
                                  catchupParses.get(i).getString("title"),
                                  "Aditya Shirole",
                                  "Cafe Goodluck",
                                  "Today @ 4pm"
                            );
                            mCatchupList.add(catchup);
                        }
                        notifyCatchupsAdapter();
                    }

                } else {
                    Log.e(TAG, "done: Error: " + e.getMessage() );
                }
            }
        });
    }

    private void notifyCatchupsAdapter() {
        mCatchupListAdapter.notifyDataSetChanged();
    }

    private void addHardCodedCatchups() {
        Catchup catchup = new Catchup(R.drawable.image,"L3Cube Meet","Aditya Shirole","The Chaai, F.C. Road","Today @ 4:30pm");
        mCatchupList.add(catchup);

        catchup = new Catchup(R.drawable.image,"Dinner Party","Shrunoti","Goodluck Cafe, F.C. Road","Tomorrow @ 8:30pm");
        mCatchupList.add(catchup);

        catchup = new Catchup(R.drawable.image,"Pokemon Hunt","Sejal Abhangrao","BVP ground, Dhankawadi","12th September @ 4:30pm");
        mCatchupList.add(catchup);
    }


}