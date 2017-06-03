package com.l3cube.catchup.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.l3cube.catchup.R;
import com.l3cube.catchup.models.CatchupPlace;
import com.l3cube.catchup.models.ParseCatchup;
import com.l3cube.catchup.ui.CatchupDetailsAlternateActivity;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by adityashirole on 30-03-2017.
 */

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.BaseHolder> {
    private static final int PLACE_PICKER_REQUEST = 2;
    Context mContext;
    Activity mActivity;
    String objectId;
    private static final int VIEW_TYPE_ADD_PLACE = 169;
    private static final int VIEW_TYPE_PLACE_ITEM = 466;
    List<CatchupPlace> places;

    public PlacesAdapter(String objectId, List<CatchupPlace> placesInCatchup, Context mContext, Activity activity) {
        this.objectId = objectId;
        this.mContext = mContext;
        this.mActivity = activity;
        this.places = placesInCatchup;
    }

    @Override
    public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        if (viewType == VIEW_TYPE_PLACE_ITEM) {
            v = LayoutInflater.from(mContext).inflate(R.layout.item_places, parent, false);
            return new PlacesAdapter.ViewHolder(v);
        } else {
            v = LayoutInflater.from(mContext).inflate(R.layout.item_places_add_place, parent, false);
            return new PlacesAdapter.AddPlaceHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(final BaseHolder holder, final int position) {

       if (getItemViewType(position) == VIEW_TYPE_PLACE_ITEM) {
           final ViewHolder viewHolder = (ViewHolder) holder;

           if (position != 0) {
               viewHolder.getName().setText(places.get(position - 1).getName());
           } else {

           }

           viewHolder.getRsvpButton().setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   if (viewHolder.getRsvpButton().getText().toString().equalsIgnoreCase("vote")) {
                       ParseQuery<ParseCatchup> query = ParseQuery.getQuery(ParseCatchup.class);
                       query.getInBackground(objectId, new GetCallback<ParseCatchup>() {
                           @Override
                           public void done(ParseCatchup catchup, ParseException e) {
                               if (e == null) {
                                   if (catchup != null) {
                                       JSONArray placesArray = catchup.getJSONArray("placesJSONArray");
                                       Integer voteCount = 0;
                                       for (int i = 0; i < placesArray.length(); i++) {
                                           try {
                                               JSONObject timeJSON = placesArray.getJSONObject(i);
                                               if (timeJSON.getString("name").equals(places.get(position - 1).getName())) {
                                                   JSONArray votes = timeJSON.getJSONArray("votes");
                                                   Boolean flag = true;
                                                   for (int j = 0; j < votes.length(); j++){
                                                       if (votes.get(j).equals(ParseUser.getCurrentUser().getObjectId())){
                                                           flag = false;
                                                           break;
                                                       }
                                                   }
                                                   if (flag){
                                                       votes.put(ParseUser.getCurrentUser().getObjectId());
                                                   }
                                                   voteCount = votes.length();
                                                   break;
                                               }
                                           } catch (JSONException e1) {
                                               e1.printStackTrace();
                                           }
                                       }
                                       catchup.put("placesJSONArray", placesArray);
                                       final Integer finalVoteCount = voteCount;
                                       catchup.saveInBackground(new SaveCallback() {
                                           @Override
                                           public void done(ParseException e) {
                                               ((CatchupDetailsAlternateActivity) mActivity).changeVote(true, places.get(position - 1).getId());
                                               viewHolder.getRsvpButton().setText("UnVote");
                                               viewHolder.getVotes().setText(new String("Votes: ").concat(String.valueOf(finalVoteCount)));
                                           }
                                       });
                                   }
                               } else {
                                   Log.e("", "done: Error : " + e.getMessage(),e );
                               }
                           }
                       });
                   } else {
                       ParseQuery<ParseCatchup> query = ParseQuery.getQuery(ParseCatchup.class);
                       query.getInBackground(objectId, new GetCallback<ParseCatchup>() {
                           @Override
                           public void done(ParseCatchup catchup, ParseException e) {
                               if (e == null) {
                                   if (catchup != null) {
                                       JSONArray placesArray = catchup.getJSONArray("placesJSONArray");
                                       Integer voteCount = 0;
                                       for (int i = 0; i < placesArray.length(); i++) {
                                           try {
                                               JSONObject timeJSON = placesArray.getJSONObject(i);
                                               if (timeJSON.getString("name").equals(places.get(position - 1).getName())) {
                                                   JSONArray votes = timeJSON.getJSONArray("votes");
                                                   int flag = -1;
                                                   for (int j = 0; j < votes.length(); j++){
                                                       if (votes.get(j).equals(ParseUser.getCurrentUser().getObjectId())){
                                                           flag = j;
                                                           break;
                                                       }
                                                   }
                                                   if (flag!=-1){
                                                       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                           votes.remove(flag);
                                                       }
                                                   }
                                                   voteCount = votes.length();
                                                   break;
                                               }
                                           } catch (JSONException e1) {
                                               e1.printStackTrace();
                                           }
                                       }
                                       catchup.put("placesJSONArray", placesArray);
                                       final Integer finalVoteCount = voteCount;
                                       catchup.saveInBackground(new SaveCallback() {
                                           @Override
                                           public void done(ParseException e) {
                                               ((CatchupDetailsAlternateActivity) mActivity).changeVote(false, places.get(position - 1).getId());
                                               viewHolder.getRsvpButton().setText("Vote");
                                               viewHolder.getVotes().setText(new String("Votes: ").concat(String.valueOf(finalVoteCount)));
                                           }
                                       });
                                   }
                               } else {
                                   Log.e("", "done: Error : " + e.getMessage(),e );
                               }
                           }
                       });
                   }
               }
           });

           viewHolder.getVotes().setText(new String("Votes: ").concat(String.valueOf(places.get(position - 1).getVotes())));

       } else {
           AddPlaceHolder addPlaceHolder = (AddPlaceHolder) holder;

           addPlaceHolder.getAddButton().setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   try {
                       Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                               .build(mActivity);
                       mActivity.startActivityForResult(intent, PLACE_PICKER_REQUEST);
                   } catch (GooglePlayServicesRepairableException e){

                   } catch (GooglePlayServicesNotAvailableException e){

                   }
               }
           });

           addPlaceHolder.getVotes().setText("");
       }
    }



    @Override
    public int getItemCount() {
        return places.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return VIEW_TYPE_ADD_PLACE;
        } else {
            return VIEW_TYPE_PLACE_ITEM;
        }
    }

    public class ViewHolder extends BaseHolder {
        CardView root;
        ImageView avatar;
        TextView name;
        TextView rsvpButton;
        TextView votes;

        public CardView getRoot() {
            return root;
        }

        public void setAvatar(ImageView avatar) {
            this.avatar = avatar;
        }

        public void setName(TextView name) {
            this.name = name;
        }

        public void toggleRSVPButton() {
            if (rsvpButton.getText().toString().equalsIgnoreCase("vote")) {
                rsvpButton.setText("UnVote");
            } else {
                rsvpButton.setText("Vote");
            }
        }

        public ImageView getAvatar() {
            return avatar;
        }

        public TextView getName() {
            return name;
        }

        public TextView getRsvpButton() {
            return rsvpButton;
        }

        public void setVotes(TextView votes) {
            this.votes = votes;
        }

        public TextView getVotes() {
            return votes;
        }

        public ViewHolder(View v) {
            super(v);
            root = (CardView) v.findViewById(R.id.root);
            rsvpButton = (TextView) v.findViewById(R.id.rsvp_button);
            name = (TextView) v.findViewById(R.id.name);
            votes = (TextView) v.findViewById(R.id.votes);
        }
    }

    public class AddPlaceHolder extends BaseHolder {
        CardView root;
        ImageView avatar;
        TextView name;
        TextView votes;
        TextView addButton;

        public CardView getRoot() {
            return root;
        }

        public void setAvatar(ImageView avatar) {
            this.avatar = avatar;
        }

        public void setName(TextView name) {
            this.name = name;
        }

        public TextView getAddButton() {
            return addButton;
        }

        public void setAddButton(TextView addButton) {
            this.addButton = addButton;
        }

        public ImageView getAvatar() {
            return avatar;
        }

        public TextView getName() {
            return name;
        }

        public void setVotes(TextView votes) {
            this.votes = votes;
        }

        public TextView getVotes() {
            return votes;
        }



        public AddPlaceHolder(View v) {
            super(v);
            root = (CardView) v.findViewById(R.id.root);
            addButton = (TextView) v.findViewById(R.id.rsvp_button);
            votes = (TextView) v.findViewById(R.id.votes);
        }
    }

    public class BaseHolder extends RecyclerView.ViewHolder {

        public BaseHolder(View itemView) {
            super(itemView);
        }
    }
}
