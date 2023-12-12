package com.example.rizzplayer.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int action_music = intent.getIntExtra("action_music", 0);

        Intent intentSer = new Intent(context, PlayerController.class);
        intentSer.putExtra("action_music", action_music);

        context.startService(intentSer);
    }
}
