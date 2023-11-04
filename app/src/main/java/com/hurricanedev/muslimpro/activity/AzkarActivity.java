package com.hurricanedev.muslimpro.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import com.hurricanedev.muslimpro.model.Azkar;
import com.hurricanedev.muslimpro.utilities.DataBaseHandler;
import com.hurricanedev.muslimpro.R;
import com.hurricanedev.muslimpro.utilities.RoundImage;

public class AzkarActivity extends AppCompatActivity  {


    private String fav;
    private Azkar ziker;
    private DataBaseHandler db;
    private ArrayList<Azkar> myList = new ArrayList<>();
    private ImageView imgIcon;
    private InterstitialAd interstitial;
    public static final String USER_LANGUAGES = "user_langu";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_azkar);

        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        String userlango = preferences.getString(USER_LANGUAGES , "en");


        Locale locale = new Locale("ar");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        setTitle(getResources().getString(R.string.title_activity_azkar));

        db = new DataBaseHandler(this);
        TextView textAuth = findViewById(R.id.textAuth);
        TextView textQuote = findViewById(R.id.textazkar);
        imgIcon = (ImageView) findViewById(R.id.imgcon);
        ImageButton btnNext = (ImageButton) findViewById(R.id.btn_next);
        ImageButton btnPrevious = (ImageButton) findViewById(R.id.btn_prev);
        Typeface fontQuote = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Light.ttf");
        Typeface fontAuth = Typeface.createFromAsset(getAssets(),
                "fonts/Roboto-Italic.ttf");
        textQuote.setTypeface(fontQuote);
        textQuote.setTextSize(21);
        textAuth.setTypeface(fontAuth);
        AtomicInteger currentId = new AtomicInteger(getIntent().getExtras().getInt("id"));
        String mode = getIntent().getExtras().getString("mode");


        Log.d("ziker id", "onCreate: " + mode);
        if (mode == null || mode.equals("") || mode.equals("allAzakers")) {

            myList = (ArrayList<Azkar>) db.getAllAzkar("");
            ziker = myList.get(currentId.get());
        } else {

            switch (mode) {
                case "zikerday":
                    ziker = db.getAzkar(getIntent().getIntExtra("id", 0));
                    Log.d("ziker id", "onCreate: " + getIntent().getIntExtra("id", 0));
                    btnNext.setVisibility(View.GONE);
                    btnPrevious.setVisibility(View.GONE);
                    break;
                case "ca": {

                    String ca = getIntent().getStringExtra("category");

                    Log.d("TAG", "onCreate: " + ca);

                    myList = (ArrayList<Azkar>) db.getAzkarByCategory(ca);

                    ziker = myList.get(currentId.get());


                    break;
                }
                case "oc": {

                    String ca = getIntent().getStringExtra("sub");
                    ziker = db.getAzkarBySubcat(ca).get(currentId.get());

                    myList = (ArrayList<Azkar>) db.getAzkarBySubcat(ca);

                    break;
                }
                case "fav":

                    ziker = db.getFavorites().get(currentId.get());

                    myList = (ArrayList<Azkar>) db.getFavorites();
                    break;
            }
        }

        /*if(mode != null && mode.equals("zikerday")){
            ziker = db.getAllAzkar(" LIMIT 256").get(getIntent().getIntExtra("id", 0));
            btnNext.setVisibility(View.GONE);
            btnPrevious.setVisibility(View.GONE);
        }
        else {
            myList = (ArrayList<Azkar>) db.getAllAzkar(" LIMIT 256");
            ziker = myList.get(currentId.get());
        }*/


        textAuth.setText(ziker.getName());
        textQuote.setText(ziker.getAzkar());
        checkPicure();
        btnNext.setOnClickListener(view -> {

            if (currentId.get() < (myList.size() - 1)) {

                currentId.getAndIncrement();
                ziker = myList.get(currentId.get());
                textAuth.setText(ziker.getName());
                textQuote.setText(ziker.getAzkar());
                checkPicure();
            }
        });

        btnPrevious.setOnClickListener(view -> {

            if (currentId.get() > 0) {

                currentId.getAndDecrement();
                ziker = myList.get(currentId.get());
                textAuth.setText(ziker.getName());
                textQuote.setText(ziker.getAzkar());
                checkPicure();
            }
        });

        fav = ziker.getFav();


        AdView adView = new AdView(this);
        adView.setAdUnitId(getResources().getString(R.string.banner_ad_unit_id));
        adView.setAdSize(AdSize.BANNER);
        LinearLayout layout = (LinearLayout) findViewById(R.id.layAdsazkar);
        layout.addView(adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        if(mode.equals("allAzakers")){


            interstitial = new InterstitialAd(this);
            interstitial.setAdUnitId(getResources().getString(R.string.interstitial_ad_unit_id));
            AdRequest adRequest2 = new AdRequest.Builder().build();

            interstitial.loadAd(adRequest2);
            interstitial.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {

                    displayInterstitial();

                }
            });
        }


    }

    public void checkPicure(){
        boolean isExist = false;
        InputStream imageStream = null;
        try {
            imageStream = getAssets().open("subcategories/"+ziker.getFileName()+".png");

            isExist =true;
        } catch (IOException e) {
            e.printStackTrace();
        }


        RoundImage roundedImage;
        if (isExist){
            Bitmap theImage = BitmapFactory.decodeStream(imageStream);
            roundedImage = new RoundImage(theImage);
            imgIcon.setImageDrawable(roundedImage );
        }
        else {
            Bitmap bm = BitmapFactory.decodeResource(getResources(),R.drawable.ic_quran);
            roundedImage = new RoundImage(bm);
            imgIcon.setImageDrawable(roundedImage);
        }

    }

    public void doShare() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getApplicationInfo().name);
        intent.putExtra(Intent.EXTRA_TEXT,
                ziker.getAzkar() + "  - " + ziker.getName());
        AzkarActivity.this.startActivity(Intent.createChooser(intent,
                getResources().getString(R.string.share)));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_azkar, menu);
        if (fav.equals("0")) {
            menu.findItem(R.id.action_favorite).setIcon(R.drawable.ic_baseline_favorite_24);
        }
        if (fav.equals("1")) {
            menu.findItem(R.id.action_favorite).setIcon(R.drawable.ic_baseline_delete_forever_24);
        }

        return true;
    }

    @TargetApi(11)
    private void copyToClipBoard(String ziker) {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            clipboard.setText(ziker);
        } else {

            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("text", ziker);
            clipboard.setPrimaryClip(clip);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_share:
                doShare();
                break;
            case R.id.copy:
                String duaa = ziker.getAzkar() + "- " + ziker.getName();
                copyToClipBoard(duaa);
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.copy_msg),
                        Toast.LENGTH_LONG).show();
                break;
            case R.id.action_favorite:
                if (ziker.getFav().equals("0")) {
                    ziker.setFav("1");
                    db.updateAzkar(ziker);
                    item.setIcon(R.drawable.ic_baseline_delete_forever_24);
                } else if (ziker.getFav().equals("1")) {
                    ziker.setFav("0");
                    db.updateAzkar(ziker);
                    item.setIcon(R.drawable.ic_baseline_favorite_24);
                }
                break;
            default:
                break;
        }
        return true;
    }



    public void displayInterstitial() {
        if (interstitial.isLoaded()) {
            interstitial.show();
        }
    }
}
