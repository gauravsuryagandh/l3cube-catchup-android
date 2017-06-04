package com.l3cube.catchup.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.l3cube.catchup.R;
import com.l3cube.catchup.models.CatchupPlace;
import com.l3cube.catchup.models.ParseCatchup;
import com.l3cube.catchup.ui.activities.MainActivity;
import com.l3cube.catchup.ui.activities.NewCatchupActivity;
import com.l3cube.catchup.ui.adapters.InviteeAdapter;
import com.l3cube.catchup.ui.adapters.PlacesAdapter;
import com.l3cube.catchup.ui.adapters.TimesAdapter;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.uber.sdk.android.rides.RideRequestButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CatchupDetailsAlternateActivity extends AppCompatActivity {
    private static final int PLACE_PICKER_REQUEST = 2;
    private static final String TAG = CatchupDetailsAlternateActivity.class.getSimpleName();
    private RecyclerView rvInvitees;
    private RecyclerView rvPlaces;
    private RecyclerView rvTimes;
    private FloatingActionButton fabUpdate;
    private Button btnDeleteCatchup;
    private String title;
    private ParseCatchup currentCatchup;
    private List<CatchupPlace> placesInCatchup;
    private List<CatchupPlace> timesInCatchup;
    private ArrayList<ParseObject> invitedList;
    private ArrayList<ParseObject> notGoingList;
    private ArrayList<ParseObject> goingList;
    RideRequestButton requestButton;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catchup_details_alternate);

        title = getIntent().getStringExtra("title");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        collapsingToolbarLayout.setTitle(title);
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));
        toolbar.setTitle(title);


        rvInvitees = (RecyclerView) findViewById(R.id.rv_invitees);
        rvInvitees.canScrollVertically(0);

        invitedList = new ArrayList<ParseObject>();
        goingList = new ArrayList<ParseObject>();
        notGoingList = new ArrayList<ParseObject>();
        rvInvitees.setLayoutManager(new LinearLayoutManager(CatchupDetailsAlternateActivity.this,LinearLayoutManager.HORIZONTAL,false));
        rvInvitees.setAdapter(new InviteeAdapter(invitedList, goingList, notGoingList, currentCatchup, CatchupDetailsAlternateActivity.this));

        rvPlaces = (RecyclerView) findViewById(R.id.rv_places);
        rvTimes = (RecyclerView) findViewById(R.id.rv_times);

        placesInCatchup = new ArrayList<>();
        timesInCatchup = new ArrayList<>();
        rvPlaces.canScrollVertically(0);
        rvTimes.canScrollVertically(0);

        rvPlaces.setLayoutManager(new LinearLayoutManager(CatchupDetailsAlternateActivity.this,LinearLayoutManager.HORIZONTAL,false));
        rvPlaces.setAdapter(new PlacesAdapter(getIntent().getStringExtra("objectId"),placesInCatchup,CatchupDetailsAlternateActivity.this,CatchupDetailsAlternateActivity.this));

        rvTimes.setLayoutManager(new LinearLayoutManager(CatchupDetailsAlternateActivity.this,LinearLayoutManager.HORIZONTAL,false));
        rvTimes.setAdapter(new TimesAdapter(getIntent().getStringExtra("objectId"),timesInCatchup,CatchupDetailsAlternateActivity.this,CatchupDetailsAlternateActivity.this));

        requestButton = new RideRequestButton(CatchupDetailsAlternateActivity.this);
        layout = (LinearLayout) findViewById(R.id.cl_catchup_details);

        ParseQuery<ParseCatchup> query = ParseQuery.getQuery(ParseCatchup.class);

        query.getInBackground(getIntent().getStringExtra("objectId"), new GetCallback<ParseCatchup>() {
            @Override
            public void done(ParseCatchup catchup, ParseException e) {
                if (e == null) {
                    if (catchup != null) {
                        if (ParseUser.getCurrentUser().getObjectId().equals(String.valueOf(catchup.getParseUser("inviter").getObjectId()))) {
                            fabUpdate = (FloatingActionButton) findViewById(R.id.fab_alt_update_catchup);
                            fabUpdate.show();
                            fabUpdate.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(CatchupDetailsAlternateActivity.this, NewCatchupActivity.class);
                                    intent.putExtra("operation", "update");
                                    intent.putExtra("objectId", getIntent().getStringExtra("objectId"));
                                    startActivity(intent);
                                }
                            });
                            btnDeleteCatchup = (Button) findViewById(R.id.btn_del_catchup);
                            btnDeleteCatchup.setVisibility(View.VISIBLE);
                            btnDeleteCatchup.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    AlertDialog myQuittingDialogBox = new AlertDialog.Builder(CatchupDetailsAlternateActivity.this)
                                            //set message, title, and icon
                                            .setTitle("Delete")
                                            .setMessage("Are you sure you want to Delete the Catchup?")
                                            .setIcon(R.drawable.ic_delete_black_36dp)

                                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                                                public void onClick(DialogInterface dialog, int whichButton) {
//                                                    notifyDataSetChanged();
                                                    ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Catchup");
                                                    parseQuery.whereEqualTo("objectId", getIntent().getStringExtra("objectId"));
                                                    Log.d("ObjectId", "onMenuItemClick: " + getIntent().getStringExtra("objectId"));
                                                    parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                                                        @Override
                                                        public void done(ParseObject object, ParseException e) {
                                                            if (e == null){
                                                                object.deleteInBackground(new DeleteCallback() {
                                                                    @Override
                                                                    public void done(ParseException e) {
                                                                        if(e == null){
                                                                            startActivity(new Intent(CatchupDetailsAlternateActivity.this, MainActivity.class));
                                                                        } else {
                                                                            Toast.makeText(CatchupDetailsAlternateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                            } else {
                                                                Toast.makeText(CatchupDetailsAlternateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                                    dialog.dismiss();
                                                }

                                            })



                                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {

                                                    dialog.dismiss();

                                                }
                                            })
                                            .create();

                                    myQuittingDialogBox.show();
                                }
                            });
                        }
                        currentCatchup = catchup;

                        JSONArray placesArray = catchup.getJSONArray("placesJSONArray");
                        JSONArray timesArray = catchup.getJSONArray("timesJSONArray");
                        invitedList.add(catchup.getParseUser("inviter"));
                        invitedList.addAll((ArrayList<ParseObject>)catchup.get("invited"));
                        goingList.addAll((ArrayList<ParseObject>) catchup.get("going"));
                        notGoingList.addAll((ArrayList<ParseObject>) catchup.get("notGoing"));

                        for (int i = 0; i < placesArray.length(); i++) {
                            try {
                                JSONObject placeJSON = placesArray.getJSONObject(i);
                                CatchupPlace place = new CatchupPlace(placesArray.getJSONObject(i).getString("id"));
                                place.setName(placeJSON.getString("name"));
                                place.setVotes((JSONArray) placeJSON.get("votes"));

                                placesInCatchup.add(0,place);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                        for (int i = 0; i < timesArray.length(); i++) {
                            try {
                                JSONObject timeJSON = timesArray.getJSONObject(i);
                                CatchupPlace time = new CatchupPlace(timesArray.getJSONObject(i).getString("id"));
                                time.setName(timeJSON.getString("name"));
                                time.setVotes((JSONArray) timeJSON.get("votes"));

                                timesInCatchup.add(0,time);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }

                        rvInvitees.getAdapter().notifyDataSetChanged();
                        rvPlaces.getAdapter().notifyDataSetChanged();
                        rvTimes.getAdapter().notifyDataSetChanged();
                        layout.addView(requestButton,3
                        );
                        Log.i(TAG, "done: Found Catchup : " + catchup.getString("title"));
                    }
                } else {
                    Log.e(TAG, "done: Error : " + e.getMessage(),e );
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CatchupDetailsAlternateActivity.this, MainActivity.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK) {
                final Place pickedPlace = PlacePicker.getPlace(this,data);
                String toastMsg = String.format("Adding %s to place suggestions", pickedPlace.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

            if (currentCatchup != null) {

                JSONArray jsonArray = null;

                jsonArray = currentCatchup.getJSONArray("placesJSONArray");

                if (jsonArray == null) {
                    jsonArray = new JSONArray();
                }
                JSONObject place = new JSONObject();
                try {
                    place.put("name",pickedPlace.getName());
                    place.put("latitude",pickedPlace.getLatLng().latitude);
                    place.put("longitude",pickedPlace.getLatLng().longitude);
                    place.put("address",pickedPlace.getAddress());
                    place.put("id",pickedPlace.getId());
                    place.put("votes", new JSONArray());

                    jsonArray.put(jsonArray.length(),place);
                    currentCatchup.put("placesJSONArray",jsonArray);

                    currentCatchup.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {

                                CatchupPlace place1 = new CatchupPlace(pickedPlace.getId());
                                place1.setName(pickedPlace.getName().toString());

                                placesInCatchup.add(0,place1);
                                rvPlaces.getAdapter().notifyDataSetChanged();
                                Toast.makeText(CatchupDetailsAlternateActivity.this, "Added CatchupPlace Suggestion of " + pickedPlace.getName(), Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e(TAG, "done: Error: " + e.getMessage(),e );
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public void changeVote(Boolean addVote, String placeId) {
        try {
            currentCatchup.changeVote(addVote,placeId);
            currentCatchup.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(CatchupDetailsAlternateActivity.this, "Changed your vote", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "done: Success: Saved vote");
                    } else {
                        Log.e(TAG, "done: Error: " + e.getMessage(),e );
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
