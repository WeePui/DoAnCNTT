package com.example.rizzplayer.Service;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;

public class MyApplication extends Application {

    public static final String CHANNEL_ID = "channel_service_example";

    @Override
    public void onCreate() {
        super.onCreate();

        createChannelNotification();
    }

    private void createChannelNotification() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "Channel Service Example", NotificationManager.IMPORTANCE_HIGH);

        notificationChannel.setSound(null, null);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(notificationChannel);
    }
}
