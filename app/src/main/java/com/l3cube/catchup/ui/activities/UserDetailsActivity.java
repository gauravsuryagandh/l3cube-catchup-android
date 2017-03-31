package com.l3cube.catchup.ui.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.l3cube.catchup.R;
import com.parse.ParseUser;

import java.io.InputStream;

public class UserDetailsActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);



        TextView fname = (TextView) findViewById(R.id.firstNameLabel);
        TextView lname = (TextView) findViewById(R.id.lastNameLabel);
        TextView email = (TextView) findViewById(R.id.emailAddressLabel);
        TextView birthDate = (TextView) findViewById(R.id.birthDateLabel);
        TextView mPhoneNumber = (TextView) findViewById(R.id.contactNumberLabel);
        Button mEditDetails = (Button) findViewById(R.id.editDetailsButton);
        //ImageView mImageView = findViewById(R.id.profilePictureLabel);
        fname.setText(ParseUser.getCurrentUser().getString("firstName"));
        lname.setText(ParseUser.getCurrentUser().getString("lastName"));
        email.setText(ParseUser.getCurrentUser().getString("emailId"));
        birthDate.setText(ParseUser.getCurrentUser().getString("birthDate"));
        mPhoneNumber.setText(ParseUser.getCurrentUser().getString("mobileNumber"));


        //ImageView mprofilePictureView;
        //mprofilePictureView = (ImageView) findViewById(R.id.profilePictureLabel);

        String mImageUrl = ParseUser.getCurrentUser().getString("profilePicture");


        new DownloadImageTask((ImageView) findViewById(R.id.profilePictureLabel))
                .execute(mImageUrl);


        mEditDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserDetailsActivity.this, EditUserDetailsActivity.class));
            }
        });

        //Log.d(TAG, "This is image Url" + mImageUrl);


        //Picasso.with(context).load(ParseUser.getCurrentUser().getString("profilePicture")).into(mprofilePictureView);
       /* URL newurl = null;
        try {
            newurl = new URL(ParseUser.getCurrentUser().getString("profilePicture"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Bitmap mIcon = null;
        try {
            mIcon = BitmapFactory.decodeStream(newurl.openConnection() .getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        mprofilePictureView.setImageBitmap(mIcon);

        //profilePictureView.setProfileId(ParseUser.getCurrentUser().getString("id"));
        */
       //type 2

        //String Url = ParseUser.getCurrentUser().getString("profilePicture");
        //profilePictureView.setImageBitmap(getBitmapFromURL(Url));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(UserDetailsActivity.this, MainActivity.class));
    }


    /*public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }*/

}

