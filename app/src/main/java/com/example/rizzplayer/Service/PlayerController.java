package com.example.rizzplayer.Service;

import static com.example.rizzplayer.Service.MyApplication.CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.rizzplayer.Business.Playlist;
import com.example.rizzplayer.Business.Song;
import com.example.rizzplayer.MainActivity;
import com.example.rizzplayer.R;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

public class PlayerController extends Service {
    public static final int ACTION_PAUSE = 1, ACTION_RESUME = 2, ACTION_CLEAR = 3, ACTION_START = 4, ACTION_NEXT = 5, ACTION_PREVIOUS = 6;

    private Song msong;
    byte[] art;
    Bitmap songImg;

    public static Playlist playlist = new Playlist("Current Playlist");

    public PlayerController() {
    }

    public static MediaPlayer mediaPlayer;
    public static MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
    private boolean isPlaying = false;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("Service", "Service Started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Song song = (Song) bundle.get("song_Object");
            if (song!= null && song!=msong){
                msong = song;
            try {
                startMusic(msong);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }}
        }

        int action_music = intent.getIntExtra("action_music", 0);
        try {
            HandleAction_Music(action_music);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return START_NOT_STICKY;

    }

    private void startMusic(Song song) throws IOException {
        if (mediaPlayer == null) {
        } else {
            metaRetriever.release();
            mediaPlayer.release();
        }
        mediaPlayer = new MediaPlayer();
        metaRetriever = new MediaMetadataRetriever();
        try {

            mediaPlayer.setDataSource(song.getResource());
            mediaPlayer.prepare();

            metaRetriever.setDataSource(song.getResource(), new HashMap<String, String>());
            try {
                art = metaRetriever.getEmbeddedPicture();
                Bitmap songImage = BitmapFactory.decodeByteArray(art, 0, art.length);
                this.songImg = songImage;
            } catch (Exception e) {
                songImg = BitmapFactory.decodeResource(getResources(), (R.drawable.anya_shocked));
            }
            //Toast.makeText(this, metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE), Toast.LENGTH_SHORT).show();
            mediaPlayer.start();
            isPlaying = true;
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.pause();
                    if (mediaPlayer.isLooping())
                        return;
                    NextSong(ACTION_NEXT);
                }
            });

            sendNotification(song);

            SendHandler(ACTION_START);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void HandleAction_Music(int Action) throws IOException {
        switch (Action) {
            case ACTION_PAUSE:
                PauseMusic();
                break;

            case ACTION_RESUME:
                ResumeMusic();
                break;

            case ACTION_CLEAR:
                ClearMusic();
                break;

            case ACTION_PREVIOUS:
                NextSong(ACTION_PREVIOUS);
                break;

            case ACTION_NEXT:
                NextSong(ACTION_NEXT);
                break;
        }
    }

    private void NextSong(int action) {
        SendHandler(action);
    }

    private void ClearMusic() {
        stopSelf();
        SendHandler(ACTION_CLEAR);
    }

    private void ResumeMusic() {
        if (mediaPlayer != null && !isPlaying) {
            Log.e("GG", "RESUMED!");
            mediaPlayer.start();
            isPlaying = true;
            sendNotification(msong);
            SendHandler(ACTION_RESUME);
        }
    }

    private void PauseMusic() {
        if (mediaPlayer != null && isPlaying) {
            Log.e("GG", "PAUSED!");
            mediaPlayer.pause();
            isPlaying = false;
            sendNotification(msong);
            SendHandler(ACTION_PAUSE);
        }
    }

    private void SendHandler(int action) {
        Intent intent = new Intent("send_data_toActivity");
        Bundle bundle = new Bundle();
        bundle.putSerializable("song_Object", msong);
        bundle.putBoolean("player_status", isPlaying);
        bundle.putInt("action_music", action);
        intent.putExtras(bundle);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void sendNotification(@NotNull Song song) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.custom_layout);
        remoteViews.setTextViewText(R.id.custom_textViewTittle, song.getTitle());
        remoteViews.setTextViewText(R.id.custom_textViewSinger, song.getSinger());

        remoteViews.setImageViewBitmap(R.id.custom_img_song, this.songImg);

        remoteViews.setImageViewResource(R.id.custom_play, R.drawable.play_arrow_48px);

        if (isPlaying) {
            remoteViews.setOnClickPendingIntent(R.id.custom_play, getPendingIntent(this, ACTION_PAUSE));
            remoteViews.setImageViewResource(R.id.custom_play, R.drawable.pause_48px);
        } else {
            remoteViews.setOnClickPendingIntent(R.id.custom_play, getPendingIntent(this, ACTION_RESUME));
            remoteViews.setImageViewResource(R.id.custom_play, R.drawable.play_arrow_48px);
        }

        remoteViews.setOnClickPendingIntent(R.id.custom_close, getPendingIntent(this, ACTION_CLEAR));
        remoteViews.setOnClickPendingIntent(R.id.custom_previous, getPendingIntent(this, ACTION_PREVIOUS));
        remoteViews.setOnClickPendingIntent(R.id.custom_next, getPendingIntent(this, ACTION_NEXT));

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setCustomContentView(remoteViews)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_android_notification)
                .build();

        startForeground(15, notification);

    }


    private PendingIntent getPendingIntent(Context context, int action) {
        Intent intent = new Intent(this, MyReceiver.class);
        intent.putExtra("action_music", action);
        return PendingIntent.getBroadcast(context.getApplicationContext(), action, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        Log.e("Service", "Service Destroyed");
    }
}