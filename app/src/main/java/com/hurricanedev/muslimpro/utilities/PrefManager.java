package com.hurricanedev.muslimpro.utilities;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {

    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    private static final String FAJR = "fajr";
    private static final String SUNRISE = "sunrise";
    private static final String DHUHR = "dhuhr";
    private static final String ASR = "asr";
    private static final String MAGHRIB = "maghrib";
    private static final String ISHA = "isha";
    private static final String MIDNIGHT = "midnight";
    private static final String VIEW_MODE = "view_mode";
    private final SharedPreferences pref;

    public PrefManager(Context context) {
        pref = context.getSharedPreferences("mainPref", Context.MODE_PRIVATE);
    }

    public void updateLastRead(String name, int num, int position) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("verseName", name);
        editor.putInt("verseNum", num);
        editor.putInt("position", position);
        editor.apply();
    }

    public void updateLastReadJuz(int num, int position) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("juzNum", num);
        editor.putInt("juzPosition", position);
        editor.apply();
    }

    public String getVerseName() {
        return pref.getString("verseName", "");
    }

    public int getVerse() {
        return pref.getInt("verseNum", -1);
    }

    public int getSurahPosition() {
        return pref.getInt("position", 1);
    }


    public int getJuzNum() {
        return pref.getInt("juzNum", 1);
    }

    public int getJuzPosition() {
        return pref.getInt("juzPosition", -1);
    }

    public void saveLocation(double lat, double lon) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putFloat(LATITUDE, (float) lat);
        editor.putFloat(LONGITUDE, (float) lon);
        editor.apply();
    }

    public void saveViewMode(boolean isOn) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(VIEW_MODE, isOn);
        editor.apply();
    }

    public boolean getViewMode() {
        return pref.getBoolean(VIEW_MODE, false);
    }

    public void saveHijriDate(String date) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("hijri_date", date);
        editor.apply();
    }

    public String getHijriDate() {
        return pref.getString("hijri_date", "???");
    }

    public void savePrayerTimes(String fajr, String sunrise, String dhuhr, String asr,
                                String maghrib, String isha, String midnight) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(FAJR, fajr);
        editor.putString(SUNRISE, sunrise);
        editor.putString(DHUHR, dhuhr);
        editor.putString(ASR, asr);
        editor.putString(MAGHRIB, maghrib);
        editor.putString(ISHA, isha);
        editor.putString(MIDNIGHT, midnight);
        editor.apply();
    }

    public String getFajr() {
        return pref.getString(FAJR, "00:00");
    }

    public String getSunrise() {
        return pref.getString(SUNRISE, "00:00");
    }

    public String getDhuhr() {
        return pref.getString(DHUHR, "00:00");
    }

    public String getAsr() {
        return pref.getString(ASR, "00:00");
    }

    public String getMaghrib() {
        return pref.getString(MAGHRIB, "00:00");
    }

    public String getIsha() {
        return pref.getString(ISHA, "00:00");
    }

    public String getMidnight() {
        return pref.getString(MIDNIGHT, "00:00");
    }

    public double getLocationLat() {
        return pref.getFloat(LATITUDE, 0);
    }

    public double getLocationLon() {
        return pref.getFloat(LONGITUDE, 0);
    }

}
