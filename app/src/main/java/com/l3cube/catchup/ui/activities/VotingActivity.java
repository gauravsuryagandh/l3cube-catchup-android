package com.l3cube.catchup.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.l3cube.catchup.R;


public class VotingActivity extends AppCompatActivity {

    private ImageView addChoice;
    private RelativeLayout mLayout;
    private EditText mEditText;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*0.8),(int)(height*0.6));

        addChoice = (ImageView) findViewById(R.id.btn_add_choice);
        mLayout = (RelativeLayout) findViewById(R.id.activity_voting);
        mEditText = (EditText) findViewById(R.id.choice1);
        TextView textView = new TextView(this);
        textView.setText("Choice");
        setupListeners();


    }
    private void setupListeners(){

        addChoice.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {


               // mEditText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                mLayout.addView(mEditText);
                //Toast.makeText(VotingActivity.this,count,Toast.LENGTH_SHORT).show();



            }
        });
    }



private TextView createNewTextView(String text){
    final RelativeLayout.LayoutParams lparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    final TextView textView = new TextView(this);
    textView.setLayoutParams(lparams);
    textView.setText(text);
    return textView;
}

}
