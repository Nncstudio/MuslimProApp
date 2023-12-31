package com.hurricanedev.muslimpro.activity;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.hurricanedev.muslimpro.model.Azkar;
import com.hurricanedev.muslimpro.adapter.AzkarsListAdapter;
import com.hurricanedev.muslimpro.utilities.DataBaseHandler;
import com.hurricanedev.muslimpro.R;

public class AzkarsActivity extends AppCompatActivity {

    private ArrayList<Azkar> imageArry = new ArrayList<>();
    private AzkarsListAdapter adapter;
    private DataBaseHandler db;
    private ListView dataList;
    private int count;
    private InterstitialAd mInterstitialAd;
    private String categoryValue;
    private String authorValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_azkars);

        Locale locale = new Locale("ar");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        setTitle(getResources().getString(R.string.title_activity_azkars));

        db = new DataBaseHandler(this);
        ImageView noQuotes = (ImageView) findViewById(R.id.NoQuotes);
        adapter = new AzkarsListAdapter(this, R.layout.azkar_items, imageArry);
        dataList = (ListView) findViewById(R.id.quotesList);
        Button btnLoadMore = new Button(this);

        btnLoadMore.setBackgroundResource(R.drawable.category_bg);
        btnLoadMore.setText(getResources().getText(R.string.btn_LoadMore));
        btnLoadMore.setTextColor(getResources().getColor(android.R.color.white));
        String activityType = getIntent().getStringExtra("mode");

        if (activityType.equals("ca")) {
            categoryValue = getIntent().getExtras()
                    .getString("category");

            Log.d("TAG", "onCreate: " + categoryValue);
            List<Azkar> contacts = db.getAzkarByCategory(categoryValue);
            for (Azkar cn : contacts) {

                imageArry.add(cn);

            }

        }
        if (activityType.equals("oc")) {
            authorValue = getIntent().getExtras().getString("sub");
            List<Azkar> contacts = db.getAzkarBySubcat(authorValue);
            for (Azkar cn : contacts) {

                imageArry.add(cn);

            }

        }

        if (activityType.equals("fav")) {
            setTitle(getResources().getText(R.string.title_activity_favorites));
            List<Azkar> contacts = db.getFavorites();
            Log.d("TAG", "onCreate: fav");
            for (Azkar cn : contacts) {

                imageArry.add(cn);

            }
            if (imageArry.isEmpty()) {

                noQuotes.setVisibility(View.VISIBLE);
            }

        }
        if (activityType.equals("allAzakers")) {

            List<Azkar> contacts = db.getAllAzkar(" LIMIT 50");
            for (Azkar cn : contacts) {

                imageArry.add(cn);

            }
            dataList.addFooterView(btnLoadMore);
        }


        dataList.setAdapter(adapter);
        dataList.setOnItemClickListener((parent, viewClicked, position, idInDB) -> {

            Intent i = new Intent(getApplicationContext(),
                    AzkarActivity.class);
            i.putExtra("id", position);
            i.putExtra("array", imageArry);
            i.putExtra("mode", activityType);
            i.putExtra("category", categoryValue);
            i.putExtra("sub", authorValue);

            startActivity(i);

        });

        btnLoadMore.setOnClickListener(arg0 ->
                // Starting a new async task
                new LoadMoreListView().execute()
        );

        AdView adView = new AdView(this);
        adView.setAdUnitId(getResources().getString(R.string.banner_ad_unit_id));
        adView.setAdSize(AdSize.BANNER);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layAdsAzkars);
        layout.addView(adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        AdRequest request = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(request);
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                finish();
            }
        });

    }


    class LoadMoreListView extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            // Before starting background task
            // Show Progress Dialog etc,.
        }

        protected Void doInBackground(Void... unused) {
            runOnUiThread(() -> {
                count += 50;
                List<Azkar> contacts = db.getAllAzkar(" LIMIT " + count + ",50");
                imageArry.addAll(contacts);
                int currentPosition = dataList.getFirstVisiblePosition();
                adapter = new AzkarsListAdapter(AzkarsActivity.this, R.layout.azkar_items, imageArry);
                dataList.setSelectionFromTop(currentPosition + 1, 0);
            });
            return (null);
        }

    }

    @Override
    public void onBackPressed() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                finish();
            }
        }
        return super.onOptionsItemSelected(item);
    }

}
