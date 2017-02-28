package com.l3cube.catchup.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
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

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private List<Catchup> mCatchupList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private CatchupListAdapter mCatchupListAdapter;
    private FloatingActionButton mFloatingActionButton;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ParseUser.getCurrentUser() == null) {
            navigateToSignUp();
        } else if (ParseUser.getCurrentUser().getInt("digitsAuth")==0) {
            startActivity(new Intent(MainActivity.this, GetUserDetailsActivity.class));
        } else {
            setupVariables();
            populateCatchups();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    //from here
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_bar:
                EditDetails();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void EditDetails() {
        Intent i = new Intent(MainActivity.this, UserDetailsActivity.class);
        startActivity(i);
    }

    //till here


    private void setupVariables() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_catchup_list);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab_catchup_list);

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigate();
            }
        });

        mCatchupListAdapter = new CatchupListAdapter(mCatchupList, MainActivity.this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(16));
        mRecyclerView.setAdapter(mCatchupListAdapter);
        mSwipeRefreshLayout= (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        mSwipeRefreshLayout.setOnRefreshListener(this );

    }

    private void navigate() {
        Intent intent = new Intent(MainActivity.this, NewCatchupActivity.class);
        intent.putExtra("operation", "new");
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
        mCatchupList.clear();
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Catchup");
        parseQuery.addDescendingOrder("createdAt");
        parseQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> catchupParses, ParseException e) {
                if (e == null) {
                    Log.i(TAG, "done: Found Objects = " + catchupParses.size());
                    if (catchupParses.size() > 0) {
                        Catchup catchup;
                        ParseUser inviter;
                        List<ParseObject> invited;
                        Boolean userInvited;
                        for (int i = 0; i < catchupParses.size(); i++) {
                            userInvited = false;
                            invited = (ArrayList<ParseObject>) catchupParses.get(i).get("invited");
                            inviter = (ParseUser) catchupParses.get(i).get("inviter");
                            try {
                                if (inviter.fetchIfNeeded().getObjectId().equals(ParseUser.getCurrentUser().getObjectId()))
                                    userInvited = true;
                                else {
                                    for (ParseObject invitedPerson : invited){
                                        try {
                                            if(invitedPerson.fetchIfNeeded().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
                                                userInvited = true;
                                                break;
                                            }
                                        } catch (ParseException e2) {
                                            e2.printStackTrace();
                                        }
                                    }
                                }
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            if (!userInvited)
                                continue;
                            catchup = new Catchup(
                                    R.drawable.image,
                                    catchupParses.get(i).getString("title"),
                                    catchupParses.get(i).getParseUser("inviter"),
                                    catchupParses.get(i).getString("place"),
                                    catchupParses.get(i).getString("date").concat(" @ ").concat(catchupParses.get(i).getString("time")),
                                    catchupParses.get(i).getObjectId()
                            );
                            mCatchupList.add(catchup);
                        }
                        notifyCatchupsAdapter();
                    }

                } else {
                    Log.e(TAG, "done: Error: " + e.getMessage());
                }
            }
        });
    }

    private void notifyCatchupsAdapter() {
        mCatchupListAdapter.notifyDataSetChanged();


    }




    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onRefresh() {
        addCatchupsFromParse();
        Toast.makeText(getApplicationContext(),"Refreshed", Toast.LENGTH_SHORT).show();
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
