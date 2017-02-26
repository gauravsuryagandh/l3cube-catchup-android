package com.l3cube.catchup.ui.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.l3cube.catchup.R;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter{

    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, ArrayList<ParseObject>> expandableListDetail;

    public ExpandableListAdapter(Context context, List<String> expandableListTitle,
                                 HashMap<String, ArrayList<ParseObject>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition)).size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition)).get(expandedListPosition);
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.elv_catchup_details, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.tv_catchup_details_elv_title);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ParseObject expandedListObject = (ParseObject) getChild(listPosition, expandedListPosition);
        try {
            expandedListObject.fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final String expandedListName = (String) expandedListObject.get("firstName") + " "+ expandedListObject.get("lastName");
        final String expandedListNumber = (String) expandedListObject.get("mobileNumber");
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_row_invited_contact, null);
        }
        TextView expandedListTextView1 = (TextView) convertView
                .findViewById(R.id.tv_invited_contact_list_row_name);
        expandedListTextView1.setText(expandedListName);
        TextView expandedListTextView2 = (TextView) convertView
                .findViewById(R.id.tv_invited_contact_list_row_number);
        expandedListTextView2.setText(expandedListNumber);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
