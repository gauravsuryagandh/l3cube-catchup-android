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
public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHolder>{

    private List<Person> mContactsList;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView number;

        public MyViewHolder(View itemView) {

            super(itemView);
            number = (TextView) itemView.findViewById(R.id.tv_contact_list_row_number);
        }
    }

    public ContactListAdapter(List<Person> contactsList) {
        this.mContactsList = contactsList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_row_contact, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Person contact = mContactsList.get(position);
        holder.number.setText(contact.getPhone());
    }

    @Override
    public int getItemCount() {
        return mContactsList.size();
    }
}
