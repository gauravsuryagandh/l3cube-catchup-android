package com.l3cube.catchup.ui.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.l3cube.catchup.R;


public class VotingActivity extends AppCompatActivity {

   // private ImageView addChoice;
    //private RelativeLayout mLayout;
    //private EditText mEditText;
   // private Button mAddChoice;
    //private int count;

    private LinearLayout mLayout;
    private EditText mEditText;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*0.8),(int)(height*0.6));

        /*addChoice = (ImageView) findViewById(R.id.btn_add_choice);
        mLayout = (RelativeLayout) findViewById(R.id.activity_voting);
        mEditText = (EditText) findViewById(R.id.choice1);
        TextView textView = new TextView(this);
        textView.setText("Choice");
        setupListeners();

        addChoice.setOnClickListener(onClick());
        //addChoice.setOnClickListener();*/


        mLayout = (LinearLayout) findViewById(R.id.linearLayout);
        mEditText = (EditText) findViewById(R.id.editText);
        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(onClick());
        TextView textView = new TextView(this);
        textView.setText("New text");

    }

    private View.OnClickListener onClick() {
        return new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mLayout.addView(createNewTextView(mEditText.getText().toString()));
            }
        };
    }


    private TextView createNewTextView(String text) {
        final ViewGroup.LayoutParams lparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //final TextView textView = new TextView(this);
        final TextView textView = (TextView) new EditText(this);
        textView.setLayoutParams(lparams);
        //textView.setText("New text: " + text);
        textView.setHint("Add Choice " + text);
        return textView;
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_catchup_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_bar:
               //EditDetails();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
