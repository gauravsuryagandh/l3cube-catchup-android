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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.l3cube.catchup.R;
import com.l3cube.catchup.models.Catchup;
import com.l3cube.catchup.ui.adapters.CatchupListAdapter;
import com.l3cube.catchup.ui.decorators.SpacesItemDecoration;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private List<Catchup> mCatchupList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private CatchupListAdapter mCatchupListAdapter;
    private FloatingActionButton mFloatingActionButton;
    RelativeLayout mLayout;
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
        mLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

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

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        String userId="";
        try {
            userId = ParseUser.getCurrentUser().getObjectId();
        } catch (NullPointerException e) {
            Log.d(TAG, "didNotSignUp: "+e.getMessage());
        }
        installation.put("GCMSenderId","797163850689");

        installation.put("userId",userId);
        installation.saveInBackground();
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
            case R.id.view_discount:
                ViewDiscounts();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void EditDetails() {
        Intent i = new Intent(MainActivity.this, UserDetailsActivity.class);
        startActivity(i);
    }

    private void ViewDiscounts() {
        Intent i = new Intent(MainActivity.this, DiscountActivity.class);
        startActivity(i);
    }
    //till here

    private void setupVariables() {

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        String userId = ParseUser.getCurrentUser().getObjectId().toString();
        installation.put("GCMSenderId","797163850689");

        installation.put("userId",userId);
        installation.saveInBackground();

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
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userId", ParseUser.getCurrentUser().getObjectId());
        ParseCloud.callFunctionInBackground("catchups_list", params, new FunctionCallback<ArrayList<ParseObject>>() {
            public void done(ArrayList<ParseObject> catchups, ParseException e){
                if (e==null){
                    Toast.makeText(getApplicationContext(), String.valueOf(catchups.size()).concat(" CatchUps found."), Toast.LENGTH_SHORT).show();
                    Catchup newFromServer;
                    for (ParseObject catchup: catchups){
                        newFromServer = new Catchup(
                                R.drawable.image,
                                catchup.getString("title"),
                                catchup.getParseUser("inviter"),
                                catchup.getString("placeName"),
                                catchup.getString("date").concat(" @ ").concat(catchup.getString("time")),
                                catchup.getObjectId()
                        );
                        mCatchupList.add(newFromServer);
                    }
                    notifyCatchupsAdapter();
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
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
