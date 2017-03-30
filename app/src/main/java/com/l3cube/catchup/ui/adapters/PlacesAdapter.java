package com.l3cube.catchup.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
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
import com.l3cube.catchup.ui.CatchupDetailsAlternateActivity;
import com.l3cube.catchup.ui.activities.NewCatchupActivity;

import java.util.List;

/**
 * Created by adityashirole on 30-03-2017.
 */

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.BaseHolder> {
    private static final int PLACE_PICKER_REQUEST = 2;
    Context mContext;
    Activity mActivity;
    private static final int VIEW_TYPE_ADD_PLACE = 169;
    private static final int VIEW_TYPE_PLACE_ITEM = 466;
    List<CatchupPlace> places;

    public PlacesAdapter(List<CatchupPlace> placesInCatchup, Context mContext, Activity activity) {
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
                       ((CatchupDetailsAlternateActivity) mActivity).changeVote(true, places.get(position - 1).getId());
                       viewHolder.getRsvpButton().setText("UnVote");
                   } else {
                       ((CatchupDetailsAlternateActivity) mActivity).changeVote(false, places.get(position - 1).getId());
                       viewHolder.getRsvpButton().setText("Vote");
                   }
               }
           });

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

        public ViewHolder(View v) {
            super(v);
            root = (CardView) v.findViewById(R.id.root);
            rsvpButton = (TextView) v.findViewById(R.id.rsvp_button);
            name = (TextView) v.findViewById(R.id.name);


        }
    }

    public class AddPlaceHolder extends BaseHolder {
        CardView root;
        ImageView avatar;
        TextView name;
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



        public AddPlaceHolder(View v) {
            super(v);
            root = (CardView) v.findViewById(R.id.root);
            addButton = (TextView) v.findViewById(R.id.rsvp_button);


        }
    }

    public class BaseHolder extends RecyclerView.ViewHolder {

        public BaseHolder(View itemView) {
            super(itemView);
        }
    }
}
