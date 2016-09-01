package com.l3cube.catchup.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.l3cube.catchup.R;

public class CatchupDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catchupdetails);

        String title = getIntent().getStringExtra("title");
        TextView tvTitle =(TextView) findViewById(R.id.TVtitle);
        tvTitle.setText(title);

        String date = getIntent().getStringExtra("date");
        TextView tvDate =(TextView) findViewById(R.id.TVdate);
        tvDate.setText(date);

        String time = getIntent().getStringExtra("time");
        TextView tvTime =(TextView) findViewById(R.id.TVtime);
        tvTime.setText(time);
    }
}
