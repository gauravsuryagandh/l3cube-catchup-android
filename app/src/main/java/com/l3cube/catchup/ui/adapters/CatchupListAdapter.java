package com.l3cube.catchup.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.l3cube.catchup.models.Catchup;
import com.l3cube.catchup.R;

import java.util.List;

/**
 * Created by push on 31/8/16.
 */
public class CatchupListAdapter extends RecyclerView.Adapter<CatchupListAdapter.MyViewHolder> {
    private List<Catchup> mCatchupList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView placeImage;
        public TextView title, inviter, place, time;

        public MyViewHolder(View view){
            super(view);
            placeImage = (ImageView) view.findViewById(R.id.iv_catchup_list_row);
            title = (TextView) view.findViewById(R.id.tv_catchup_list_row_title);
            inviter = (TextView) view.findViewById(R.id.tv_catchup_list_row_inviter);
            place = (TextView) view.findViewById(R.id.tv_catchup_list_row_place);
            time = (TextView) view.findViewById(R.id.tv_catchup_list_row_time);
        }
    }

    public CatchupListAdapter(List<Catchup> catchupList) {
        this.mCatchupList = catchupList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_row_catchup, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Catchup catchup = mCatchupList.get(position);
        holder.placeImage.setImageResource(catchup.getPlaceImage());
        holder.title.setText(catchup.getTitle());
        holder.inviter.setText(catchup.getInviter());
        holder.place.setText(catchup.getPlace());
        holder.time.setText(catchup.getTime());
    }

    @Override
    public int getItemCount() {
        return mCatchupList.size();
    }
}
