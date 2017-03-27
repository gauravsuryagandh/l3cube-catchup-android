package com.l3cube.catchup.ui.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.l3cube.catchup.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter{

    // 4 Child types
    private static final int CHILD_TYPE_1 = 0;
    private static final int CHILD_TYPE_2 = 1;
    private static final int CHILD_TYPE_3 = 2;
    private static final int CHILD_TYPE_4 = 3;

    // 3 Group types
    private static final int GROUP_TYPE_1 = 0;
    private static final int GROUP_TYPE_2 = 1;
    private static final int  GROUP_TYPE_3 = 2;

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
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition)).get(expandedListPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition)).size();
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ParseObject expandedListObject = (ParseObject) getChild(listPosition, expandedListPosition);
        LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        int childType = getChildType(listPosition, 0);

        try {
            expandedListObject.fetchIfNeeded();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (convertView == null) {
            switch (childType) {
                case CHILD_TYPE_1:
                    convertView = layoutInflater.inflate(R.layout.item_row_contact, null);
//                    convertView.setTag(childType);
                    break;
                case CHILD_TYPE_2:
                    convertView = layoutInflater.inflate(R.layout.item_row_contact, null);
//                    convertView.setTag(childType);
                    break;
                case CHILD_TYPE_3:
//                    convertView = inflater.inflate(R.layout.comments_expandable_list_child, null);
//                    convertView.setTag(childType);
                    break;
                case CHILD_TYPE_4:
//                    convertView = inflater.inflate(R.layout.comments_undefined, null);
//                    convertView.setTag(childType);
                    break;
            }
        }

        switch (childType) {
            case CHILD_TYPE_1:
                int userGoing = 0;
                final ArrayList<ParseObject> going = this.expandableListDetail.get("RSVPed Yes");
                final ArrayList<ParseObject> notGoing = this.expandableListDetail.get("RSVPed No");
                final String expandedListName = expandedListObject.get("firstName") + " "+ expandedListObject.get("lastName");
                final String expandedListNumber = (String) expandedListObject.get("mobileNumber");
                TextView expandedListTextView = (TextView) convertView.findViewById(R.id.tv_contact_list_row_name);
                expandedListTextView.setText(expandedListName);
                expandedListTextView = (TextView) convertView.findViewById(R.id.tv_contact_list_row_number);
                expandedListTextView.setText(expandedListNumber);
                expandedListTextView = (TextView) convertView.findViewById(R.id.tv_contact_list_row_RSVP);
                for (ParseObject goingPerson: going) {
                    userGoing = 0;
                    try {
                        if (goingPerson.fetchIfNeeded().getObjectId().equals(expandedListObject.getObjectId())){
                            userGoing = 2;
                            break;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if (userGoing == 2){
                    RelativeLayout expandedListLayout = (RelativeLayout) convertView.findViewById(R.id.rl_contact_row_RSVP_btn);
                    expandedListLayout.setVisibility(View.GONE);
                    expandedListTextView.setText("Going");
                } else {
                    try {
                        if(expandedListObject.fetchIfNeeded().getObjectId().equals(ParseUser.getCurrentUser().getObjectId())){
                            expandedListTextView.setVisibility(View.GONE);
                            final ParseObject catchupObject = this.expandableListDetail.get("Object").get(0);
                            Button expandedListButton = (Button) convertView.findViewById(R.id.btn_contact_list_row_RSVP_ng);
                            expandedListButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    notGoing.add(ParseUser.getCurrentUser());
                                    catchupObject.put("notGoing", notGoing);
                                    catchupObject.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e==null){
                                                Toast.makeText(context, "updated: not going", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                            expandedListButton = (Button) convertView.findViewById(R.id.btn_contact_list_row_RSVP_g);
                            final TextView finalExpandedListTextView = expandedListTextView;
                            final RelativeLayout expandedListRelativeLayout = (RelativeLayout) convertView.findViewById(R.id.rl_contact_row_RSVP_btn);
                            expandedListButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    going.add(ParseUser.getCurrentUser());
                                    catchupObject.put("going", going);
                                    catchupObject.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e==null){
                                                Toast.makeText(context, "updated: going", Toast.LENGTH_SHORT).show();
                                                finalExpandedListTextView.setVisibility(View.VISIBLE);
                                                expandedListRelativeLayout.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                                }
                            });
                        } else {
                            RelativeLayout expandedListLayout = (RelativeLayout) convertView.findViewById(R.id.rl_contact_row_RSVP_btn);
                            expandedListLayout.setVisibility(View.GONE);
                            expandedListTextView.setText("Not Going");
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHILD_TYPE_2:
                //Define how to render the data on the CHILD_TYPE_2 layout
                break;
            case CHILD_TYPE_3:
                //Define how to render the data on the CHILD_TYPE_3 layout
                break;
            case CHILD_TYPE_4:
                //Define how to render the data on the CHILD_TYPE_4 layout
                break;
        }

        return convertView;
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
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
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

    @Override
    public int getChildTypeCount() {
        return 4; // I defined 4 child types (CHILD_TYPE_1, CHILD_TYPE_2, CHILD_TYPE_3, CHILD_TYPE_UNDEFINED)
    }

    @Override
    public int getGroupTypeCount() {
        return 3; // I defined 3 groups types (GROUP_TYPE_1, GROUP_TYPE_2, GROUP_TYPE_3)
    }

    @Override
    public int getChildType(int groupPosition, int childPosition) {
        switch (groupPosition) {
            case 0:
                switch (childPosition) {
                    case 0:
                        return CHILD_TYPE_1;
                    case 1:
                        return CHILD_TYPE_2;
                }
                break;
            case 1:
                return CHILD_TYPE_3;
            case 2:
                return CHILD_TYPE_4;
        }

        return CHILD_TYPE_4;
    }

    public void sendRsvpNotif(){

        String curr_user_id = ParseUser.getCurrentUser().getObjectId();



    }
}
