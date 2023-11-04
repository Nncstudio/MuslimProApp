package com.hurricanedev.muslimpro.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.hurricanedev.muslimpro.R;
import com.hurricanedev.muslimpro.activity.AzkarActivity;

import java.util.Random;

public class NotificationHelper extends ContextWrapper {
    public static final String CHANNELID = "ShopReminderID";
    public static final String CHANNELNAME = "Shop Reminder";

    private NotificationManager mManager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel channel = new NotificationChannel(CHANNELID, CHANNELNAME, NotificationManager.IMPORTANCE_HIGH);
        channel.setLightColor(Color.GREEN);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        getManager().createNotificationChannel(channel);
    }

    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    public NotificationCompat.Builder getChannelNotification(int p, String title, String description) {
        Intent intent = new Intent(getApplicationContext(), AzkarActivity.class);
        intent.putExtra("id", p);
        intent.putExtra("mode", "zikerday");
        PendingIntent pendingIntent = PendingIntent
                .getActivity(getApplicationContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Builder(getApplicationContext(), CHANNELID)
                .setContentTitle(title)
                .setContentText(description)
                .setContentIntent(pendingIntent)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_quran);
    }

    private int random() {
        return new Random().nextInt(266);
    }
}
