package com.l3cube.catchup.ui.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.l3cube.catchup.R;
import com.l3cube.catchup.ui.activities.DownloadImageTask;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Created by adityashirole on 30-03-2017.
 */

public class InviteeAdapter extends RecyclerView.Adapter<InviteeAdapter.ViewHolder> {

    Context mContext;
    ArrayList<ParseObject> invitedList;

    public InviteeAdapter(ArrayList<ParseObject> invitedList, Context mContext) {
        this.invitedList = invitedList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_invitee,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ParseObject person = invitedList.get(position);
        String name;

        try {
            person.fetchIfNeeded();
            if (!person.getString("lastName").equals(null))
                name = person.getString("firstName")
                        .concat(" ")
                        .concat(person.getString("lastName"));
            else
                name = person.getString("firstName");
            if (person.getClassName()=="_User") {
                holder.name.setText(name);
                if (!person.getString("profilePicture").equals(null)){
                    String mImageUrl = person.getString("profilePicture");
                    new DownloadImageTask(holder.avatar).execute(mImageUrl);
                }
            } else {
                holder.name.setText(name);
            }
        } catch (ParseException e) {
            e.printStackTrace();
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

        public ViewHolder(View v) {
            super(v);
            root = (CardView) v.findViewById(R.id.root);
            avatar = (ImageView) v.findViewById(R.id.avatar);
            name = (TextView) v.findViewById(R.id.name);
            rsvp = (TextView) v.findViewById(R.id.rsvp);
        }
    }
}
