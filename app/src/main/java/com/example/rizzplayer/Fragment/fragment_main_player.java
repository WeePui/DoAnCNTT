package com.example.rizzplayer.Fragment;

import static com.example.rizzplayer.Service.PlayerController.playlist;
import static com.example.rizzplayer.Service.PlayerController.ACTION_CLEAR;
import static com.example.rizzplayer.Service.PlayerController.ACTION_NEXT;
import static com.example.rizzplayer.Service.PlayerController.ACTION_PAUSE;
import static com.example.rizzplayer.Service.PlayerController.ACTION_PREVIOUS;
import static com.example.rizzplayer.Service.PlayerController.ACTION_RESUME;
import static com.example.rizzplayer.Service.PlayerController.ACTION_START;
import static com.example.rizzplayer.Service.PlayerController.mediaPlayer;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.rizzplayer.Business.Playlist;
import com.example.rizzplayer.DAO.SongDAO;
import com.example.rizzplayer.MainActivity;
import com.example.rizzplayer.Service.PlayerController;
import com.example.rizzplayer.R;
import com.example.rizzplayer.Business.Song;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_main_player#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_main_player extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Drawable player_drawable;
    boolean isHearted, isPLaying, MainPlayerIsOpen = true;
    public static SeekBar seekBar;

    public static TextView remain, passes;

    private Song song;

    private String offline;
    TextView titleSong, Singer;
    public ImageView heart_btn;
    public static ImageView cover;
    public ImageView close_player;
    public ImageView exit_btn;
    public ImageView Play_Pause_BTN;
    public ImageView forward_btn;
    public ImageView previous_btn;
    public ImageView PlayerSetting;


    public fragment_main_player() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainPlayer.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_main_player newInstance(String param1, String param2) {
        fragment_main_player fragment = new fragment_main_player();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_player, container, false);
    }
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null)
                return;
            offline = (String) bundle.get("offline_song");
            song = (Song) bundle.get("song_Object");
            isPLaying = bundle.getBoolean("player_status");
            int actionMusic = bundle.getInt("action_music");
            handlerMusic(actionMusic);

        }

        private void handlerMusic(int action) {
            switch (action) {
                case ACTION_START:
                    setSong(song);
                    break;

                case ACTION_PAUSE:

                case ACTION_RESUME:
                    setPlayBtn();
                    break;

                case ACTION_CLEAR:
                    isPLaying = false;
                    setPlayBtn();
                    break;

                case ACTION_PREVIOUS:
                    PreviousSong();
                    break;

                case ACTION_NEXT:
                    NextSong();
                    break;
            }
        }
    };

    public void PreviousSong() {

        try {
            if (playlist.getCurrent() == 0) {
                PlaySong(playlist.getSong(playlist.getIndex()));
            } else {
                PlaySong(playlist.getSong(playlist.getCurrent() - 1));
            }

        } catch (Exception e) {
            Log.e("GG", e.getMessage());
            PreviousSong();
            return;
        }
    }

    public void NextSong() {

        try {
            if (playlist.getCurrent() + 1 < playlist.getIndex()) {
                PlaySong(playlist.getSong(playlist.getCurrent() + 1));
            } else {
                PlaySong(playlist.getSong(0));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, new IntentFilter("send_data_toActivity"));

        seekBar = view.findViewById(R.id.seekbar);
        remain = view.findViewById(R.id.remain);
        passes = view.findViewById(R.id.passes);

        PlayerSetting = view.findViewById(R.id.player_setting);
        PlayerSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getContext(), PlayerSetting);
                popupMenu.inflate(R.menu.sub_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Song song = playlist.getSong(playlist.getCurrent());
                        int id = menuItem.getItemId();
                        if (id == R.id.download){
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                downloadSong(song);
                            }
                            else
                                Toast.makeText(getContext(), "Hãy đăng nhập để tải về bài hát", Toast.LENGTH_LONG).show();
                        }
                        if (id == R.id.addToFav) {
                            //To do thêm vào danh sách phát là List String trong profile business và update profile trong realtime DB
                            Toast.makeText(getContext(), "Tính năng chưa được hoàn thiện", Toast.LENGTH_LONG).show();
                        }
                        else if( id == R.id.listen){
                            Toast.makeText(getContext(), "Bạn đang nghe bài này mà :D", Toast.LENGTH_LONG).show();
                        }
                    return  true;
                    }
                });
                popupMenu.show();
            }
        });


        titleSong = view.findViewById(R.id.Player_Tittle);
        Singer = view.findViewById(R.id.Player_Singer);
        cover = view.findViewById(R.id.img_thumb);
        Play_Pause_BTN = view.findViewById(R.id.mainPLayerPlay);
        Play_Pause_BTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartPlaying();
            }
        });

        forward_btn = view.findViewById(R.id.forward);
        forward_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NextSong();
            }
        });

        previous_btn = view.findViewById(R.id.previous);
        previous_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PreviousSong();
            }
        });

        heart_btn = view.findViewById(R.id.heart);
        heart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!mediaPlayer.isLooping()) {
                    mediaPlayer.setLooping(true);
                    heart_btn.setImageResource(R.drawable.favorite_filled);
                } else {
                    mediaPlayer.setLooping(false);
                    heart_btn.setImageResource(R.drawable.favorite_48px);
                }
                Log.e("GG", "Heart " + mediaPlayer.isLooping());
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(mediaPlayer!=null && b)
                    mediaPlayer.seekTo(i*1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        close_player = view.findViewById(R.id.player_close);
        close_player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Send2Player(MainActivity.Player_Minimize);
            }
        });

        exit_btn = view.findViewById(R.id.exit);
        exit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Send2Service(ACTION_CLEAR);
                heart_btn.setImageResource(R.drawable.favorite_48px);
            }
        });
    }

    private void downloadSong(Song song) {
        SongDAO songDAO = new SongDAO();
        songDAO.downloadFiles(getContext(), song);
        Toast.makeText(getContext(), song.getTitle() + " is downloading", Toast.LENGTH_SHORT).show();
    }

    private void PausePlaying() {
        isPLaying = false;
        setPlayBtn();
        Send2Service(ACTION_PAUSE);
    }

    private void Send2Service(int action) {
        Intent intent = new Intent(getContext(), PlayerController.class);
        intent.putExtra("action_music", action);
        getContext().startService(intent);
    }

    private void Send2Player(int action) {
        Intent intent = new Intent("send_data_toPlayer");
        intent.putExtra("action_player", action);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }

    public void PlaySong(Song song) {
        Intent intent = new Intent(getContext(), PlayerController.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("song_Object", song);
        intent.putExtras(bundle);

        getContext().startService(intent);
        setSong(song);
        isPLaying = true;
        setPlayBtn();
    }

    private void StartPlaying() {
        if (!isPLaying) {
            isPLaying = true;
            setPlayBtn();
            Send2Service(ACTION_RESUME);
        } else
            PausePlaying();
    }

    public void setPlayBtn() {
        if (isPLaying) {
            Play_Pause_BTN.setImageResource(R.drawable.pause_circle_48px);
        } else
            Play_Pause_BTN.setImageResource(R.drawable.play_circle_48px);
    }

    public void setSong(Song song) {
        Singer.setText(song.getSinger());
        titleSong.setText(song.getTitle());
        Picasso.get().load(song.getCover()).into(cover);
        int duration = mediaPlayer.getDuration();
        int minutes = (duration / 1000) / 60;
        int seconds = (duration / 1000) % 60;
        @SuppressLint("DefaultLocale") String time = String.format("%02d:%02d", minutes, seconds);
        remain.setText(time);


        /*Picasso.get()
                .load(song.getCover())
                .transform(new Blurry(getContext(), 25, 1))
                .into((Target) player_drawable);*/
        setPlayBtn();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }
}