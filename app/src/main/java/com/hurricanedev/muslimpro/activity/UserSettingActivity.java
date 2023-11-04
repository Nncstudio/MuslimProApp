package com.hurricanedev.muslimpro.activity;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatCheckedTextView;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.AppCompatSpinner;

import com.hurricanedev.muslimpro.R;
import com.hurricanedev.muslimpro.service.AlarmTask;

import java.util.Calendar;
import java.util.Date;


public class UserSettingActivity extends PreferenceActivity {

    private static final String USERTIME = "userTime";
    private SharedPreferences sharedPrefs;
    private Preference mytime;
    private long summary;
    static final int DIALOG_ID = 10;

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        // Allow super to try and create a view first
        final View result = super.onCreateView(name, context, attrs);
        if (result != null) {
            return result;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // If we're running pre-L, we need to 'inject' our tint aware Views in place of the
            // standard framework versions
            switch (name) {
                case "EditText":
                    return new AppCompatEditText(this, attrs);
                case "Spinner":
                    return new AppCompatSpinner(this, attrs);
                case "CheckBox":
                    return new AppCompatCheckBox(this, attrs);
                case "RadioButton":
                    return new AppCompatRadioButton(this, attrs);
                case "CheckedTextView":
                    return new AppCompatCheckedTextView(this, attrs);
                default:
                    break;
            }
        }

        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(UserSettingActivity.this);
        Preference myPref = findPreference("about");
        mytime = findPreference("mytime");
        Preference time = findPreference("time");
        summary = sharedPrefs.getLong(USERTIME, 0);
        if (summary != 0) {
            mytime.setSummary(DateFormat.getTimeFormat(getBaseContext())
                    .format(summary));
        } else {
            mytime.setSummary("");
        }

        mytime.setOnPreferenceClickListener(preference -> {

            showDialog(DIALOG_ID);

            return false;
        });

        time.setOnPreferenceClickListener(preference -> {

            startActivity(new Intent(UserSettingActivity.this, AzkarsTimeSettingsActivity.class));

            return false;
        });

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_ID) {
            int hour;
            int minute;
            final Calendar c = Calendar.getInstance();
            if (summary != 0) {
                c.setTimeInMillis(sharedPrefs.getLong(USERTIME,
                        System.currentTimeMillis()));
            }
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);
            return new TimePickerDialog(this, (view, pickerHour, pickerMinute) -> {
                Calendar calendar = Calendar.getInstance();
                int hourOne = pickerHour;
                int minuteTwo = pickerMinute;

                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, hourOne);
                calendar.set(Calendar.MINUTE, minuteTwo);
                calendar.set(Calendar.SECOND, 0);

                Editor e = sharedPrefs.edit();
                e.putLong(USERTIME, calendar.getTimeInMillis());
                e.apply();
                mytime.setSummary(DateFormat.getTimeFormat(getBaseContext())
                        .format(new Date(calendar.getTimeInMillis())));

                new AlarmTask(UserSettingActivity.this, calendar).run();
            }, hour, minute, false);
        }
        return null;
    }

}