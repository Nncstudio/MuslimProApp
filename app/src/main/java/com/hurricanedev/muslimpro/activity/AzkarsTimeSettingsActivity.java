package com.hurricanedev.muslimpro.activity;

import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.TextView;

import com.hurricanedev.muslimpro.R;

import java.util.Locale;


public class AzkarsTimeSettingsActivity extends AppCompatActivity {

    public static final String MIN = "minutes";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_azkars_time_settings);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        Locale locale = new Locale("ar");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        setTitle(getResources().getString(R.string.title_activity_user_setting));

        final SharedPreferences sharedPreferences = getSharedPreferences("checkFail1", MODE_PRIVATE);
        int hours = sharedPreferences.getInt(MIN, 5);

        RadioButton radio5M = (RadioButton) findViewById(R.id.Radio_5_m);
        RadioButton radio15M = (RadioButton) findViewById(R.id.Radio_15_m);
        RadioButton radio60M = (RadioButton) findViewById(R.id.Radio_60_m);
        RadioButton radio120M = (RadioButton) findViewById(R.id.Radio_120_m);

        radio5M.setText(getResources().getString(R.string.Radio5txt));
        radio15M.setText(getResources().getString(R.string.Radio15txt));
        radio60M.setText(getResources().getString(R.string.Radio60txt));
        radio120M.setText(getResources().getString(R.string.Radio120txt));

        switch (hours) {
            case 5:
                radio5M.setChecked(true);
                break;
            case 15:
                radio15M.setChecked(true);
                break;
            case 60:
                radio60M.setChecked(true);
                break;
            case 120:
                radio120M.setChecked(true);
                break;
            default:
                radio60M.setChecked(true);
                break;
        }


        findViewById(R.id.Radio_5_m).setOnClickListener(view -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(MIN, 5);
            radio15M.setChecked(false);
            radio60M.setChecked(false);
            radio120M.setChecked(false);
            editor.apply();
        });

        findViewById(R.id.Radio_15_m).setOnClickListener(view -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(MIN, 15);
            radio5M.setChecked(false);
            radio60M.setChecked(false);
            radio120M.setChecked(false);
            editor.apply();
        });

        findViewById(R.id.Radio_60_m).setOnClickListener(view -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(MIN, 60);
            radio5M.setChecked(false);
            radio15M.setChecked(false);
            radio120M.setChecked(false);
            editor.apply();

        });
        findViewById(R.id.Radio_120_m).setOnClickListener(view -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(MIN, 120);
            radio5M.setChecked(false);
            radio60M.setChecked(false);
            radio15M.setChecked(false);
            editor.apply();


        });

        TextView textView = findViewById(R.id.donebut);
        TextView freq = findViewById(R.id.freq);
        freq.setText(getResources().getString(R.string.Chose_Frequency));
        textView.setText(getResources().getString(R.string.ok));

        findViewById(R.id.donebut).setOnClickListener(view -> finish());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
