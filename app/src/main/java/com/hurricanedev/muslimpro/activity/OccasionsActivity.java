package com.hurricanedev.muslimpro.activity;

import android.content.Intent;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.core.view.MenuItemCompat;

import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;

import android.widget.ListView;
import androidx.appcompat.widget.SearchView;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.hurricanedev.muslimpro.model.Azkar;
import com.hurricanedev.muslimpro.utilities.DataBaseHandler;
import com.hurricanedev.muslimpro.R;
import com.hurricanedev.muslimpro.adapter.SubCategoriesListAdapter;

public class OccasionsActivity extends AppCompatActivity {

    private ArrayList<Azkar> imageArry = new ArrayList<>();
    private SubCategoriesListAdapter adapter;
    private DataBaseHandler db;
    private ListView dataList;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_occasions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        Locale locale = new Locale("ar");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        setTitle(getResources().getString(R.string.title_activity_occasions));

        db = new DataBaseHandler(this);

        List<Azkar> authors = db.getAllSubCat("");
        imageArry.addAll(authors);


        adapter = new SubCategoriesListAdapter(this, R.layout.occasino_items, imageArry);

        dataList = (ListView) findViewById(R.id.listView1);
        dataList.setAdapter(adapter);
        dataList.setOnItemClickListener((parent, viewClicked, position, idInDB) -> {

            Azkar srr = imageArry.get(position);
            Intent i = new Intent(getApplicationContext(),
                    AzkarsActivity.class);
            i.putExtra("sub", srr.getName());
            i.putExtra("mode", "oc");
            startActivity(i);

        });

        AdView adView = new AdView(this);
        adView.setAdUnitId(getResources().getString(R.string.banner_ad_unit_id));
        adView.setAdSize(AdSize.BANNER);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layAdsOccasions);
        layout.addView(adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_occasion, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getResources().getString(R.string.search));
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                imageArry.clear();

                List<Azkar> authors = db.getAllSubCat(searchView.getQuery());
                imageArry.addAll(authors);
                dataList.setAdapter(adapter);
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                // Here u can get the value "query" which is entered in the
                // search box.
                return false;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return true;
    }
}
