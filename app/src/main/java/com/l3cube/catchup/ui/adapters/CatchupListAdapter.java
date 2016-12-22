package com.l3cube.catchup.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.l3cube.catchup.models.Catchup;
import com.l3cube.catchup.R;
import com.l3cube.catchup.ui.activities.CatchupDetailsActivity;
import com.l3cube.catchup.ui.activities.MainActivity;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by push on 31/8/16.
 */
public class CatchupListAdapter extends RecyclerView.Adapter<CatchupListAdapter.MyViewHolder> {
    private List<Catchup> mCatchupList;
    private Context mContext;
    private int position;
    ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("CatchupParse");


    public class MyViewHolder extends RecyclerView.ViewHolder{
        public ImageView placeImage;
        public TextView title, inviter, place, time;
        public CardView root;
        public ImageButton menuButton;



        public MyViewHolder(View view){
            super(view);
            placeImage = (ImageView) view.findViewById(R.id.iv_catchup_list_row);
            title = (TextView) view.findViewById(R.id.tv_catchup_list_row_title);
            inviter = (TextView) view.findViewById(R.id.tv_catchup_list_row_inviter);
            place = (TextView) view.findViewById(R.id.tv_catchup_list_row_place);
            time = (TextView) view.findViewById(R.id.tv_catchup_list_row_time);
            root = (CardView) view.findViewById(R.id.root);
            menuButton= (ImageButton) view.findViewById(R.id.imageButton);
        }
    }

    public CatchupListAdapter(List<Catchup> catchupList, Context context) {
        this.mCatchupList = catchupList;
        mContext = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_catchup, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Catchup catchup = mCatchupList.get(position);
        String place = catchup.getPlace();
        switch (place) {
            case "Goodluck Cafe":
                holder.placeImage.setImageResource(R.drawable.goodluck_cafe);
                break;
            case "Rolls Delight":
                holder.placeImage.setImageResource(R.drawable.roll_delight);
                break;
            case "The Chaai":
                holder.placeImage.setImageResource(R.drawable.chaai);
                break;
            case "Pict, Pune":
                holder.placeImage.setImageResource(R.drawable.pict);
                break;
            default:
                holder.placeImage.setImageResource(R.drawable.image);
        }
        holder.title.setText(catchup.getTitle());
        holder.inviter.setText(catchup.getInviter());
        holder.place.setText(catchup.getPlace());
        holder.time.setText(catchup.getTime());

        //holder.root.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//
//                Log.i("CatchupAdapter", "onClick: card was clicked");
//                mContext.startActivity(new Intent(mContext, CatchupDetailsActivity.class).putExtra("objectId", mCatchupList.get(position).getObjectId()));
//            }
//        });

        holder.placeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(mContext, "Opening CatchUp " + mCatchupList.get(position).getObjectId(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, CatchupDetailsActivity.class);
                intent.putExtra("objectId", mCatchupList.get(position).getObjectId());
                mContext.startActivity(intent);
            }
        });

        holder.menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                PopupMenu popupMenu = new PopupMenu(mContext, holder.menuButton);
                popupMenu.inflate(R.menu.main_catchup_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.delete_catchup:
                                notifyDataSetChanged();
                                ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("CatchupParse");
                                parseQuery.whereEqualTo("objectId", mCatchupList.get(position).getObjectId());
                                Log.d("ObjectId", "onMenuItemClick: " + mCatchupList.get(position).getObjectId());
                                parseQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                                    @Override
                                    public void done(ParseObject object, ParseException e) {
                                        if (e == null){
                                            object.deleteInBackground(new DeleteCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    if(e == null){
                                                        Toast.makeText(mContext, "Deleted " + position, Toast.LENGTH_SHORT).show();
                                                        mCatchupList.remove(mCatchupList.get(position));
                                                        notifyDataSetChanged();
                                                    } else {
                                                        Toast.makeText(mContext, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(mContext, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                break;


                        }
                        return false;
                    }
                });

                 popupMenu.show();
            }
        });
    }



    @Override
    public int getItemCount() {
        return mCatchupList.size();
    }



    }

