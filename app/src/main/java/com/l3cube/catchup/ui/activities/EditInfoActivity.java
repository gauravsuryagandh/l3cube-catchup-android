package com.l3cube.catchup.ui.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.l3cube.catchup.R;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class EditInfoActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);



        TextView fname = (TextView) findViewById(R.id.firstNameLabel);
        TextView lname = (TextView) findViewById(R.id.lastNameLabel);
        TextView email = (TextView) findViewById(R.id.emailAddressLabel);
        TextView birthDate = (TextView) findViewById(R.id.birthDateLabel);
        //ImageView mImageView = findViewById(R.id.profilePictureLabel);
        lname.setText(ParseUser.getCurrentUser().getString("lastName"));
        email.setText(ParseUser.getCurrentUser().getString("emailId"));
        birthDate.setText(ParseUser.getCurrentUser().getString("birthDate"));
        fname.setText(ParseUser.getCurrentUser().getString("firstName"));

        //ImageView mprofilePictureView;
        //mprofilePictureView = (ImageView) findViewById(R.id.profilePictureLabel);

        String mImageUrl = ParseUser.getCurrentUser().getString("profilePicture");


        new DownloadImageTask((ImageView) findViewById(R.id.profilePictureLabel))
                .execute(mImageUrl);

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

    class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;

    public DownloadImageTask(ImageView bmImage) {
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
