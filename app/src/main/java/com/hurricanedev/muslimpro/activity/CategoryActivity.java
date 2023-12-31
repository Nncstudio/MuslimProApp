package com.hurricanedev.muslimpro.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;

import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.hurricanedev.muslimpro.adapter.CategoriesListAdapter;
import com.hurricanedev.muslimpro.model.Category;
import com.hurricanedev.muslimpro.utilities.DataBaseHandler;
import com.hurricanedev.muslimpro.R;


public class CategoryActivity extends AppCompatActivity {

    private ArrayList<Category> imageArry = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        Locale locale = new Locale("ar");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        setTitle(getResources().getString(R.string.title_activity_category));

        DataBaseHandler db = new DataBaseHandler(this);

        List<Category> categories = db.getAllCategories();
        imageArry.addAll(categories);

        CategoriesListAdapter adapter = new CategoriesListAdapter(this, R.layout.category_items,
                imageArry);

        ListView dataList = (ListView) findViewById(R.id.categoryList);
        dataList.setAdapter(adapter);
        dataList.setOnItemClickListener((parent, viewClicked, position, idInDB) -> {

            Category srr = imageArry.get(position);
            Intent i = new Intent(getApplicationContext(),
                    AzkarsActivity.class);
            i.putExtra("category", srr.getName());
            i.putExtra("mode", "ca");
            startActivity(i);

        });
        AdView adView = new AdView(this);
        adView.setAdUnitId(getResources().getString(R.string.banner_ad_unit_id));
        adView.setAdSize(AdSize.BANNER);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layAdsCategories);
        layout.addView(adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return true;
    }

}
