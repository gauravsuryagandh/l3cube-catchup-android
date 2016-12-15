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

        if (ParseUser.getCurrentUser() == null) {
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
                navigate();
            }
        });

        mCatchupListAdapter = new CatchupListAdapter(mCatchupList,MainActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(16));
        mRecyclerView.setAdapter(mCatchupListAdapter);


    }

    private void navigate() {
        //navigate to Digits Signup if user number not verified
        if (ParseUser.getCurrentUser().getInt("digitsAuth")==0){
            startActivity(new Intent(MainActivity.this, SignupActivity.class));
        }else{
            startActivity(new Intent(MainActivity.this, NewCatchupActivity.class));
        }
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
                                  catchupParses.get(i).getString("inviter"),
                                  catchupParses.get(i).getString("place"),
                                  catchupParses.get(i).getString("date").concat(" @ ").concat(catchupParses.get(i).getString("time")),
                                  catchupParses.get(i).getObjectId()
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



}
