package com.l3cube.catchup.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.l3cube.catchup.models.Catchup;
import com.l3cube.catchup.R;
import com.l3cube.catchup.ui.activities.CatchupDetailsActivity;

import java.util.List;

/**
 * Created by push on 31/8/16.
 */
public class CatchupListAdapter extends RecyclerView.Adapter<CatchupListAdapter.MyViewHolder> {
    private List<Catchup> mCatchupList;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView placeImage;
        public TextView title, inviter, place, time;
        public CardView root;

        public MyViewHolder(View view){
            super(view);
            placeImage = (ImageView) view.findViewById(R.id.iv_catchup_list_row);
            title = (TextView) view.findViewById(R.id.tv_catchup_list_row_title);
            inviter = (TextView) view.findViewById(R.id.tv_catchup_list_row_inviter);
            place = (TextView) view.findViewById(R.id.tv_catchup_list_row_place);
            time = (TextView) view.findViewById(R.id.tv_catchup_list_row_time);
            root = (CardView) view.findViewById(R.id.root);
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
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        Catchup catchup = mCatchupList.get(position);
        String place = catchup.getPlace();
        switch (place){
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

        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("CatchupAdapter", "onClick: card was clicked");
                mContext.startActivity(new Intent(mContext, CatchupDetailsActivity.class).putExtra("objectId",mCatchupList.get(position).getObjectId()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCatchupList.size();
    }
}
