package com.l3cube.catchup.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.l3cube.catchup.R;

public class NewcatchupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newcatchup);
    }
    public void onButtonClick(View v)
    {
        if(v.getId()== R.id.Bcreatedone){
            EditText titleObj =(EditText) findViewById(R.id.TFtitle);
            EditText dateObj = (EditText) findViewById(R.id.TFdate);
            EditText timeObj = (EditText) findViewById(R.id.TFtime);

            String title = titleObj.getText().toString();
            String date = dateObj.getText().toString();
            String time = timeObj.getText().toString();

            Intent i = new Intent(NewcatchupActivity.this,CatchupDetailsActivity.class);
            i.putExtra("title",title);
            i.putExtra("date",date);
            i.putExtra("time",time);
            startActivity(i);
        }
    }
}
