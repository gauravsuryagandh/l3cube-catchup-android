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

public class EditUserDetailsActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView mFirstName, mLastName, mEmailAddress, mBirthDate, mContactNumber;
    private Button mSaveChanges;
    ParseUser user = ParseUser.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_details);

        mFirstName = (TextView) findViewById(R.id.editFirstNameLabel);
        mLastName = (TextView) findViewById(R.id.editLastNameLabel);
        mEmailAddress = (TextView) findViewById(R.id.editEmailLabel);
        mBirthDate = (TextView) findViewById(R.id.editbirthDateLabel);
        mContactNumber = (TextView) findViewById(R.id.editContactLabel);
        mSaveChanges = (Button) findViewById(R.id.saveChangesButton);

        //changed this

        mFirstName.setText(ParseUser.getCurrentUser().getString("firstName"));
        mLastName.setText(ParseUser.getCurrentUser().getString("lastName"));
        mEmailAddress.setText(ParseUser.getCurrentUser().getString("emailId"));
        mBirthDate.setText(ParseUser.getCurrentUser().getString("birthDate"));
        mContactNumber.setText(ParseUser.getCurrentUser().getString("mobileNumber"));

        String mImageUrl = ParseUser.getCurrentUser().getString("profilePicture");


        new DownloadImage((ImageView) findViewById(R.id.profilePictureLabel))
                .execute(mImageUrl);



        mSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.put("mobileNumber", mContactNumber.getText().toString());
                user.put("firstName", mFirstName.getText().toString());
                user.put("lastName", mLastName.getText().toString());
                user.put("emailId", mEmailAddress.getText().toString());
                user.put("birthDate", mBirthDate.getText().toString());

                user.saveInBackground();
                startActivity(new Intent(EditUserDetailsActivity.this, UserDetailsActivity.class));

            }
        });



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(EditUserDetailsActivity.this, UserDetailsActivity.class));
    }

}





class DownloadImage extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public DownloadImage(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}

