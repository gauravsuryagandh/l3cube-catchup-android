package com.l3cube.catchup;

import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.l3cube.catchup.ui.activities.SignupActivity;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private List<Catchup> mCatchupList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private CatchupListAdapter mCatchupListAdapter;
    private FloatingActionButton mFloatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ParseUser.getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
        } else {
            mRecyclerView = (RecyclerView) findViewById(R.id.rv_catchup_list);
            mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fab_catchup_list);

            mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, NewCatchupActivity.class);//verify
                    startActivity(intent);
                }
            });

            mCatchupListAdapter = new CatchupListAdapter(mCatchupList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
            mRecyclerView.setAdapter(mCatchupListAdapter);

            mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), mRecyclerView, new ClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Intent intent = new Intent(MainActivity.this, CatchupDetailsActivity.class);
                    intent.putExtra("position",position);
                    startActivity(intent);
                }

                @Override
                public void onLongClick(View view, int position) {

                }
            }));

            getCatchupsList();
        }
    }

    private void getCatchupsList() {
        Catchup catchup = new Catchup(R.drawable.image,"L3Cube Meet","Aditya Shirole","The Chaai, F.C. Road","Today @ 4:30pm");
        mCatchupList.add(catchup);

        catchup = new Catchup(R.drawable.image,"Dinner Party","Shrunoti","Goodluck Cafe, F.C. Road","Tomorrow @ 8:30pm");
        mCatchupList.add(catchup);

        catchup = new Catchup(R.drawable.image,"Pokemon Hunt","Sejal Abhangrao","BVP ground, Dhankawadi","12th September @ 4:30pm");
        mCatchupList.add(catchup);

        mCatchupListAdapter.notifyDataSetChanged();
    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private MainActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final MainActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

}
