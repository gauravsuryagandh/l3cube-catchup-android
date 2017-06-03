package com.l3cube.catchup.ui.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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

import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Created by pshkrjshnde on 2/6/17.
 */

public class TimesAdapter extends RecyclerView.Adapter<TimesAdapter.BaseHolder> {
    private static final int TIME_PICKER_REQUEST = 3;
    private int hr;
    private int min;
    Context mContext;
    Activity mActivity;
    String objectId;
    private static final int VIEW_TYPE_ADD_TIME = 170;
    private static final int VIEW_TYPE_TIME_ITEM = 467;
    List<CatchupPlace> times;

    public TimesAdapter(String objectId, List<CatchupPlace> timesInCatchup, Context mContext, Activity activity) {
        this.objectId = objectId;
        this.mContext = mContext;
        this.mActivity = activity;
        this.times = timesInCatchup;
    }

    @Override
    public TimesAdapter.BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;

        if (viewType == VIEW_TYPE_TIME_ITEM) {
            v = LayoutInflater.from(mContext).inflate(R.layout.item_times, parent, false);
            return new TimesAdapter.ViewHolder(v);
        } else {
            v = LayoutInflater.from(mContext).inflate(R.layout.item_times_add_time, parent, false);
            return new TimesAdapter.AddPlaceHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(final TimesAdapter.BaseHolder holder, final int position) {

        if (getItemViewType(position) == VIEW_TYPE_TIME_ITEM) {
            final TimesAdapter.ViewHolder viewHolder = (TimesAdapter.ViewHolder) holder;

            if (position != 0) {
                viewHolder.getName().setText(times.get(position - 1).getName());
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
                                        JSONArray timesArray = catchup.getJSONArray("timesJSONArray");
                                        Integer voteCount = 0;
                                        for (int i = 0; i < timesArray.length(); i++) {
                                            try {
                                                JSONObject timeJSON = timesArray.getJSONObject(i);
                                                if (timeJSON.getString("name").equals(times.get(position - 1).getName())) {
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
                                        catchup.put("timesJSONArray", timesArray);
                                        final Integer finalVoteCount = voteCount;
                                        catchup.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                ((CatchupDetailsAlternateActivity) mActivity).changeVote(true, times.get(position - 1).getId());
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
                                        JSONArray timesArray = catchup.getJSONArray("timesJSONArray");
                                        Integer voteCount = 0;
                                        for (int i = 0; i < timesArray.length(); i++) {
                                            try {
                                                JSONObject timeJSON = timesArray.getJSONObject(i);
                                                if (timeJSON.getString("name").equals(times.get(position - 1).getName())) {
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
                                        catchup.put("timesJSONArray", timesArray);
                                        final Integer finalVoteCount = voteCount;
                                        catchup.saveInBackground(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                ((CatchupDetailsAlternateActivity) mActivity).changeVote(false, times.get(position - 1).getId());
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

            viewHolder.getVotes().setText(new String("Votes: ").concat(String.valueOf(times.get(position - 1).getVotes())));

        } else {
            TimesAdapter.AddPlaceHolder addPlaceHolder = (TimesAdapter.AddPlaceHolder) holder;

            addPlaceHolder.getAddButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar c = Calendar.getInstance();
                    hr = c.get(Calendar.HOUR_OF_DAY);
                    min = c.get(Calendar.MINUTE);
                    createdDialog(3).show();
                }
            });

            addPlaceHolder.getVotes().setText("");
        }
    }

    protected Dialog createdDialog(int id) {
        switch (id) {
            case TIME_PICKER_REQUEST:
                return new TimePickerDialog(mContext, timePickerListener, hr, min, false);
        }
        return null;
    }
    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minutes) {
            hr = hourOfDay;
            min = minutes;
            updateTime(hr, min);
        }
    };

    private void updateTime(int hours, int mins) {
        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)

            timeSet = "PM";
        else
            timeSet = "AM";
        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);
        final String aTime = new StringBuilder().append(hours).append(':').append(minutes).append(" ").append(timeSet).toString();
        Toast.makeText(mContext, aTime, Toast.LENGTH_SHORT).show();
        ParseQuery<ParseCatchup> query = ParseQuery.getQuery(ParseCatchup.class);

        query.getInBackground(this.objectId, new GetCallback<ParseCatchup>() {
            @Override
            public void done(ParseCatchup catchup, ParseException e) {
                if (e == null) {
                    if (catchup != null) {
                        JSONArray timesArray = catchup.getJSONArray("timesJSONArray");
                        JSONObject place = new JSONObject();
                        try {
                            Random random = new Random();
                            String randomId = String.valueOf(100000 + random.nextInt(900000));
                            CatchupPlace newTime = new CatchupPlace(randomId);
                            newTime.setName(aTime);
                            times.add(newTime);
                            place.put("name",aTime);
                            place.put("id", randomId);
                            place.put("votes", new JSONArray());
                            timesArray.put(place);
                            catchup.put("timesJSONArray", timesArray);
                            catchup.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if(e==null)
                                        notifyDataSetChanged();
                                    else
                                        Log.d("", "error: " + e.getMessage());
                                }
                            });
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                } else {
                    Log.e("", "done: Error : " + e.getMessage(),e );
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return times.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return VIEW_TYPE_ADD_TIME;
        } else {
            return VIEW_TYPE_TIME_ITEM;
        }
    }

    public class ViewHolder extends TimesAdapter.BaseHolder {
        CardView root;
        TextView name;
        TextView rsvpButton;
        TextView votes;

        public CardView getRoot() {
            return root;
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
            votes = (TextView) v.findViewById(R.id.votes);
        }

        public void setVotes(TextView votes) {
            this.votes = votes;
        }

        public TextView getVotes() {
            return votes;
        }
    }

    public class AddPlaceHolder extends TimesAdapter.BaseHolder {
        CardView root;
        TextView name;
        TextView addButton;
        TextView votes;

        public CardView getRoot() {
            return root;
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

        public TextView getName() {
            return name;
        }

        public void setText(TextView votes) {
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
