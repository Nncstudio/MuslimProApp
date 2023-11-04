package com.hurricanedev.muslimpro.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.hurricanedev.muslimpro.utilities.DataBaseHandler;

import java.util.Random;

public class ReminderReciver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        //String title = intent.getStringExtra("title");
        //String description = intent.getStringExtra("description");
        //int notificationID = intent.getIntExtra("notificationID", 0);
        //int position = intent.getIntExtra("position", 0);

        DataBaseHandler db = new DataBaseHandler(context);
        db.openDataBase();

        int position = random();

        String title = db.getAzkar(position).getName();
        String description = db.getAzkar(position).getAzkar();

        NotificationHelper notificationHelper = new NotificationHelper(context);
        NotificationCompat.Builder builder = notificationHelper
                .getChannelNotification(position, title, description);
        notificationHelper.getManager().notify(position, builder.build());
    }


    private int random() {
        return new Random().nextInt(266);
    }

}
