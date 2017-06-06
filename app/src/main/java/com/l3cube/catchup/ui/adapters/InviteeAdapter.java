package com.l3cube.catchup.ui.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.l3cube.catchup.R;
import com.l3cube.catchup.models.ParseCatchup;
import com.l3cube.catchup.ui.activities.DownloadImageTask;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;

/**
 * Created by adityashirole on 30-03-2017.
 */

public class InviteeAdapter extends RecyclerView.Adapter<InviteeAdapter.ViewHolder> {

    Context mContext;
    ParseObject currentCatchup;
    ArrayList<ParseObject> invitedList;
    ArrayList<ParseObject> goingList;
    ArrayList<ParseObject> notGoingList;

    public InviteeAdapter(ArrayList<ParseObject> invitedList, ArrayList<ParseObject> goingList, ArrayList<ParseObject> notGoingList, ParseCatchup currentCatchup, Context mContext) {
        this.invitedList = invitedList;
        this.goingList = goingList;
        this.notGoingList = notGoingList;
        this.mContext = mContext;
        this.currentCatchup = currentCatchup;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_invitee,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ParseObject person = invitedList.get(position);
        String name;

        try {
            person.fetchIfNeeded();
            try {
                name = person.getString("firstName")
                        .concat(" ")
                        .concat(person.getString("lastName"));
            } catch (NullPointerException e) {
                name = person.getString("firstName");
            }
            if (person.getClassName()=="_User") {
                holder.name.setText(name);
//                if (!person.getString("profilePicture").equals(null)) {
                try {
                    String mImageUrl = person.getString("profilePicture");
                    new DownloadImageTask(holder.avatar).execute(mImageUrl);
                } catch (NullPointerException e){
                    Log.d("", "no profile pic: "+e.getMessage());
                }
                if (position==0) {
                    holder.rsvp.setText("Inviter");
                } else if (goingList.contains(person.getObjectId())) {
                    holder.rsvp.setText("Going");
                } else if (notGoingList.contains(person.getObjectId())){
                    holder.rsvp.setText("Not Going");
                } else{
                    if (ParseUser.getCurrentUser().getObjectId().equals(person.getObjectId()) && position!=0){
                        holder.rsvp.setVisibility(View.GONE);
                        holder.rsvpButtons.setVisibility(View.VISIBLE);
                        holder.going.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                try {
//                                    ArrayList<ParseObject> going = (ArrayList<ParseObject>) currentCatchup.fetchIfNeeded().get("going");
//                                    going.add(ParseUser.getCurrentUser());
//                                    currentCatchup.remove("going");
//                                    currentCatchup.put("going", going);
//                                    currentCatchup.saveInBackground(new SaveCal
// lback() {
//                                        @Override
//                                        public void done(ParseException e) {
                                            Toast.makeText(mContext, "RSVPed: Going", Toast.LENGTH_SHORT).show();
                                            holder.rsvpButtons.setVisibility(View.GONE);
                                            holder.rsvp.setVisibility(View.VISIBLE);
                                            holder.rsvp.setText("Going");
//                                        }
//                                    });
//                                } catch (ParseException e) {
//                                    e.printStackTrace();
//                                }
                            }
                        });
                        holder.notGoing.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
//                                try {
//                                    ArrayList<ParseObject> going = (ArrayList<ParseObject>) currentCatchup.fetchIfNeeded().get("notGoing");
//                                    going.add(ParseUser.getCurrentUser());
////                                    currentCatchup.remove("notGoing");
//                                    currentCatchup.put("notGoing", going);
//                                    currentCatchup.saveInBackground(new SaveCallback() {
//                                        @Override
//                                        public void done(ParseException e) {
                                            Toast.makeText(mContext, "RSVPed: Not Going", Toast.LENGTH_SHORT).show();
                                            holder.rsvpButtons.setVisibility(View.GONE);
                                            holder.rsvp.setVisibility(View.VISIBLE);
                                            holder.rsvp.setText("Not Going");
//                                        }
//                                    });
//                                } catch (ParseException e) {
//                                    e.printStackTrace();
//                                }
                            }
                        });
                    } else
                        holder.rsvp.setText("Invited");
                }
            } else {
                holder.name.setText(name);
                holder.rsvp.setText("Invited");
            }
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return invitedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView root;
        ImageView avatar;
        TextView name, rsvp;
        LinearLayout rsvpButtons;
        Button going, notGoing;

        public ViewHolder(View v) {
            super(v);
            root = (CardView) v.findViewById(R.id.root);
            avatar = (ImageView) v.findViewById(R.id.avatar);
            name = (TextView) v.findViewById(R.id.name);
            rsvp = (TextView) v.findViewById(R.id.rsvp);
            rsvpButtons = (LinearLayout) v.findViewById(R.id.rsvp_buttons);
            going = (Button) v.findViewById(R.id.rsvp_going);
            notGoing = (Button) v.findViewById(R.id.rsvp_not_going);
        }
    }
}
