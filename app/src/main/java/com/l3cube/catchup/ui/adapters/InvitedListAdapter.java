package com.l3cube.catchup.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.l3cube.catchup.R;
import com.l3cube.catchup.models.Person;

import java.util.List;

/**
 * Created by push on 27/9/16.
 */
public class InvitedListAdapter extends RecyclerView.Adapter<InvitedListAdapter.MyViewHolder> {
    private List<Person> mInvitedList;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView name;
        public TextView number;

        public MyViewHolder(View itemView) {

            super(itemView);
            name = (TextView) itemView.findViewById(R.id.tv_invited_contact_list_row_name);
            number = (TextView) itemView.findViewById(R.id.tv_invited_contact_list_row_number);
        }
    }

    public InvitedListAdapter(List<Person> contactsList) {
        this.mInvitedList = contactsList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row_invited_contact, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Person contact = mInvitedList.get(position);
        holder.name.setText(contact.getName());
        holder.number.setText(contact.getPhone());
    }

    @Override
    public int getItemCount() {
        return mInvitedList.size();
    }
}
