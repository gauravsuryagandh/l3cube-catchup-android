package com.l3cube.catchup.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.l3cube.catchup.R;

public class DiscountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(DiscountActivity.this, MainActivity.class));
    }

    public void goToMauli (View view) {
        goToUrl ( "https://www.zomato.com/pune/mauli-veg-restaurant-satara-road");
    }

    public void goToGoodluck (View view) {
        goToUrl ( "https://www.zomato.com/pune/cafe-goodluck-deccan-gymkhana");
    }


    public void goTobbq (View view) {
        goToUrl ( "https://www.zomato.com/pune/barbeque-nation-jm-road");
    }


    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }


}
