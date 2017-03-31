package com.l3cube.catchup.ui;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.l3cube.catchup.R;
import com.l3cube.catchup.models.CatchupPlace;
import com.l3cube.catchup.models.ParseCatchup;
import com.l3cube.catchup.ui.adapters.InviteeAdapter;
import com.l3cube.catchup.ui.adapters.PlacesAdapter;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

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

    private String title;
    private ParseCatchup currentCatchup;
    private List<CatchupPlace> placesInCatchup;
    private ArrayList<ParseObject> invitedList;


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
        rvInvitees.setLayoutManager(new LinearLayoutManager(CatchupDetailsAlternateActivity.this,LinearLayoutManager.HORIZONTAL,false));
        rvInvitees.setAdapter(new InviteeAdapter(invitedList, CatchupDetailsAlternateActivity.this));

        rvPlaces = (RecyclerView) findViewById(R.id.rv_places);

        placesInCatchup = new ArrayList<>();
        rvPlaces.canScrollVertically(0);

        rvPlaces.setLayoutManager(new LinearLayoutManager(CatchupDetailsAlternateActivity.this,LinearLayoutManager.HORIZONTAL,false));
        rvPlaces.setAdapter(new PlacesAdapter(placesInCatchup,CatchupDetailsAlternateActivity.this,CatchupDetailsAlternateActivity.this));


        ParseQuery<ParseCatchup> query = ParseQuery.getQuery(ParseCatchup.class);

        query.getInBackground(getIntent().getStringExtra("objectId"), new GetCallback<ParseCatchup>() {
            @Override
            public void done(ParseCatchup catchup, ParseException e) {
                if (e == null) {
                    if (catchup != null) {
                        currentCatchup = catchup;

                        JSONArray placesArray = catchup.getJSONArray("placesJSONArray");
                        invitedList.addAll((ArrayList<ParseObject>) catchup.get("invited"));

                        for (int i = 0; i < placesArray.length(); i++) {
                            try {
                                JSONObject placeJSON = placesArray.getJSONObject(i);
                                CatchupPlace place = new CatchupPlace(placesArray.getJSONObject(i).getString("id"));
                                place.setName(placeJSON.getString("name"));

                                placesInCatchup.add(0,place);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }

                        rvInvitees.getAdapter().notifyDataSetChanged();
                        rvPlaces.getAdapter().notifyDataSetChanged();
                        Log.i(TAG, "done: Found Catchup : " + catchup.getString("title"));
                    }
                } else {
                    Log.e(TAG, "done: Error : " + e.getMessage(),e );
                }
            }
        });
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
                    place.put("votes",0);

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
