package com.hurricanedev.muslimpro.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import com.hurricanedev.muslimpro.R;
import com.hurricanedev.muslimpro.model.Reponse;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class QiblaActivity extends AppCompatActivity implements LocationListener, SensorEventListener {

    private FusedLocationProviderClient client;
    private GitHubService service;

    private Sensor sensor;
    private SensorManager manager;
    private float currentDegree;
    private double qiblaDegree;
    private TextView degreeText;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qibla);

        Locale locale = new Locale("ar");
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        setTitle(getResources().getString(R.string.qibla_compass_title));

        TextView toUse = findViewById(R.id.to_use);
        TextView makeSure = findViewById(R.id.make_sure);
        TextView gpsText = findViewById(R.id.gps_txt);

        toUse.setText(getResources().getString(R.string.to_use_the_compass_properly));
        makeSure.setText(getResources().getString(R.string.make_sure_your_connection_is_enabled));
        gpsText.setText(getResources().getString(R.string.make_sure_your_location_is_enabled));

        degreeText = findViewById(R.id.degree_text);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.aladhan.com/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(GitHubService.class);

        Dexter.withContext(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                        if (ActivityCompat.checkSelfPermission(QiblaActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                ActivityCompat.checkSelfPermission(QiblaActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }


                        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

                        sensor = manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

                        //manager.registerListener(getListener(), sensor, SensorManager.SENSOR_DELAY_UI);

                        if (locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                            manager.registerListener(QiblaActivity.this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
                        } else {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivityForResult(intent, 4004);
                            Toast.makeText(QiblaActivity.this, getString(R.string.make_sure_your_location_is_enabled), Toast.LENGTH_LONG).show();
                        }

                        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            Log.d("TAG", "onCreate: Not null");
                            location.setBearing(0f);
                            onLocationChanged(location);
                        } else {
                            Log.d("TAG", "onCreate: Location is null");
                        }

                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {

                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (manager != null)
            manager.unregisterListener(this, sensor);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        ImageView compass = findViewById(R.id.compass);
        int degree = Math.round(sensorEvent.values[0]);

        degreeText.setText(sensorEvent.values[0] + "°");

        Log.d("TAG", "onSensorChanged: " + sensorEvent.values[0] + "  " + qiblaDegree);
        RotateAnimation rotateAnimation = new RotateAnimation(currentDegree, (float) (-degree + qiblaDegree), Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(300);
        rotateAnimation.setFillAfter(true);
        findViewById(R.id.compass_layout).setAnimation(rotateAnimation);
        currentDegree = (float) (-degree + qiblaDegree);

        Log.d("TAG", "onSensorChanged: cd  "+ currentDegree);
        if ((float)(qiblaDegree - 3) < degree && degree < (float)(qiblaDegree + 3)) {
            compass.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
        } else {
            compass.setColorFilter(getResources().getColor(android.R.color.black));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public interface GitHubService {
        @GET("qibla/{latitude}/{longitude}")
        Call<Reponse> getDegree(@Path("latitude") String latitude,
                                      @Path("longitude") String longitude);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Log.d("TAG", "onLocationChanged: " + location.getLatitude() + "  " + location.getLongitude());

        service.getDegree(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()))
                .enqueue(new Callback<Reponse>() {
                    @Override
                    public void onResponse(Call<Reponse> call, Response<Reponse> response) {
                        Log.d("TAG", "onResponse: " + response.body().getData().getDirection());
                        qiblaDegree = response.body().getData().getDirection();
                    }

                    @Override
                    public void onFailure(Call<Reponse> call, Throwable t) {
                        Log.d("TAG", "onFailure: " + t.getMessage());
                    }
                });
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}