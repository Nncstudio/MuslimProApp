package com.hurricanedev.muslimpro.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.webkit.URLUtil;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hurricanedev.muslimpro.R;
import com.hurricanedev.muslimpro.utilities.Utilities;
import com.scwang.wave.MultiWaveHeader;

import java.io.IOException;
import java.util.Locale;

public class QuranPlayActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener{

    TextView surahname;
    Handler mHandler = new Handler();
    Utilities utils;
    ProgressDialog pDialog;
    public static final String USER_LANGUAGES = "user_langu";
    private SeekBar seekBar;
    private TextView currentTime;
    private TextView soundDuration;
    private FloatingActionButton playImage;
    private MediaPlayer mediaVoice;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quranplayer);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Locale locale = new Locale("ar");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        setTitle(getResources().getString(R.string.title_activity_quran));

        mediaVoice = new MediaPlayer();
        surahname = (TextView) findViewById(R.id.surahname);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        currentTime = (TextView) findViewById(R.id.quranCurrentDurationLabel1);
        soundDuration = (TextView) findViewById(R.id.quranTotalDurationLabel);
        playImage = findViewById(R.id.btnPlay);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimary));
        }

        utils = new Utilities();
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setMax(mediaVoice.getDuration());

        MultiWaveHeader multiWaveHeader = findViewById(R.id.wave);
        multiWaveHeader.setVelocity(2);
        multiWaveHeader.setGradientAngle(45);
        multiWaveHeader.setProgress(1f);
        multiWaveHeader.setColorAlpha(0.15f);
        multiWaveHeader.setWaveHeight(40);
        multiWaveHeader.setStartColor(getResources().getColor(R.color.colorPrimary));
        multiWaveHeader.setCloseColor(getResources().getColor(R.color.colorPrimary));

        Bundle bundle= getIntent().getExtras();
        if (bundle!= null) {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni == null) { // Check for Internet
                pDialog = ProgressDialog.show(QuranPlayActivity.this, getString(R.string.no_internet),getString (R.string.about_msg));
                pDialog.setCancelable(true);
                playImage.setEnabled(false);
                seekBar.setEnabled(false);
            } else {
                // Internet Check
                if (URLUtil.isValidUrl(getIntent().getExtras().getString("url"))) {
                    playQuran();
                } else {
                    Toast.makeText(this, getString(R.string.wrong_link), Toast.LENGTH_SHORT).show();
                    playImage.setEnabled(false);
                    seekBar.setEnabled(false);
                }
            }

        }


        // Play and Stop
        playImage.setOnClickListener(arg0 -> {
            if(mediaVoice.isPlaying()){
                mediaVoice.pause();
                playImage.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            }else{
                mediaVoice.start();
                playImage.setImageResource(R.drawable.ic_baseline_pause_24);

            }
        });

    }

    public void  playQuran() {
        try {
            mediaVoice.reset();
            mediaVoice.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaVoice.setDataSource(getIntent().getExtras().getString("url"));
            surahname.setText(getIntent().getExtras().getString("name"));

            mediaVoice.prepareAsync();
            mediaVoice.setOnPreparedListener(mp -> {
                mp.start();
                updateProgressBar();
                playImage.setImageResource(R.drawable.ic_baseline_pause_24);
            });

            mediaVoice.setOnCompletionListener(mp -> {
                currentTime.setText("");
                playImage.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            });

            playImage.setImageResource(R.drawable.ic_baseline_pause_24);

            seekBar.setProgress(0);
            seekBar.setMax(100);

        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }


    }

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        @SuppressLint("SetTextI18n")
        public void run() {

            if (mediaVoice != null) {
                long totalDuration = mediaVoice.getDuration();
                long currentDuration = mediaVoice.getCurrentPosition();

                soundDuration.setText("" + utils.milliSecondsToTimer(totalDuration));
                currentTime.setText("" + utils.milliSecondsToTimer(currentDuration));

                int progress = (utils.getProgressPercentage(currentDuration, totalDuration));
                seekBar.setProgress(progress);

                mHandler.postDelayed(this, 100);
            }
        }
    };







    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
        // empty
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
    }


    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mediaVoice.getDuration();
        int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

        mediaVoice.seekTo(currentPosition);

        updateProgressBar();
    }


    @Override
    public  void finish() {

        super.finish();
        if(mediaVoice !=null) {
            if (mediaVoice.isPlaying()) {
                mediaVoice.stop();
            }
            mediaVoice.release();
            mediaVoice = null;
            finish();

        }

    }

    @Override
    protected  void onDestroy(){
        super.onDestroy();
         if(mediaVoice != null) {
             mediaVoice.stop();
             mediaVoice = null;
         }
        finish();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home && mediaVoice != null) {
            mediaVoice.stop();
            mHandler.removeCallbacks(mUpdateTimeTask);
            seekBar.setProgress(0);
        }
        return super.onOptionsItemSelected(item);

    }





    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mHandler.removeCallbacks(mUpdateTimeTask);
        seekBar.setProgress(0);
        finish();
        super.onBackPressed();
    }

}