package com.example.rizzplayer;



import static com.example.rizzplayer.Fragment.fragment_main_player.passes;
import static com.example.rizzplayer.Fragment.fragment_main_player.seekBar;
import static com.example.rizzplayer.Service.PlayerController.ACTION_CLEAR;
import static com.example.rizzplayer.Service.PlayerController.ACTION_PAUSE;
import static com.example.rizzplayer.Service.PlayerController.ACTION_RESUME;
import static com.example.rizzplayer.Service.PlayerController.ACTION_START;
import static com.example.rizzplayer.Service.PlayerController.mediaPlayer;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.rizzplayer.Business.Profile;
import com.example.rizzplayer.Business.Song;
import com.example.rizzplayer.DAO.ProfileDAO;
import com.example.rizzplayer.Fragment.Fragment_HomePage_Admin;
import com.example.rizzplayer.Fragment.fragment_home_page;
import com.example.rizzplayer.Fragment.fragment_library;
import com.example.rizzplayer.Fragment.fragment_login;
import com.example.rizzplayer.Fragment.fragment_main_player;
import com.example.rizzplayer.Fragment.fragment_search;
import com.example.rizzplayer.Fragment.fragment_signup;
import com.example.rizzplayer.Service.PlayerController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.io.File;


public class MainActivity extends AppCompatActivity {

    RelativeLayout miniPlayer;
    private Song song;
    com.example.rizzplayer.Fragment.fragment_main_player fragment_main_player = new fragment_main_player();
    com.example.rizzplayer.Fragment.fragment_search fragment_search = new fragment_search();
    Fragment_HomePage_Admin fragment_homePage_admin = new Fragment_HomePage_Admin();
    com.example.rizzplayer.Fragment.fragment_login fragment_login = new fragment_login();
    com.example.rizzplayer.Fragment.fragment_library fragment_library = new fragment_library();
    com.example.rizzplayer.Fragment.fragment_signup fragment_signup = new fragment_signup();
    com.example.rizzplayer.Fragment.fragment_home_page fragment_home_page = new fragment_home_page();

    public static final int Player_Minimize = 0, Player_Maximize = 1;
    public static final int Lib_pop = 0, Lib_rock = 1, Lib_hiphop = 2;
    public static final int LOGIN = 0, SIGNUP = 1, BACK = 2;
    private boolean isPlaying, PlayerIsOpen = false;
    ImageView home_btn, songList_btn, search_btn, setting_btn;
    ImageView img_song, play_pause_btn, close_btn;

    View Main_Player, Menu;
    TextView titleSong, Singer;
    private FragmentTransaction fragmentTransaction;
    private final FragmentManager fragmentManager = getSupportFragmentManager();

    private String offline;

    private final BroadcastReceiver broadcastReceiverLogin = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null)
                return;
            int actionPlayer = intent.getIntExtra("action_login", -1);

            handlerLogin(actionPlayer);
        }

        public void handlerLogin(int action) {
            switch (action) {
                case LOGIN:
                    replaceMenu(fragment_login);
                    break;
                case SIGNUP:
                    replaceMenu(fragment_signup);
                    break;
                case BACK:
                    replaceMenu(new fragment_home_page());
                    break;
            }
        }
    };
    String genre;
    private final BroadcastReceiver broadcastReceiverLib = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null)
                return;
            int action_lib = intent.getIntExtra("action_Lib", 1);
            genre = intent.getStringExtra("type");
            handlerLib(action_lib);
        }

        public void handlerLib(int action) {
            switch (action) {
                case Lib_pop:
                case Lib_rock:
                case Lib_hiphop:
                    replaceLib(fragment_library, genre);

                    break;
            }
        }

    };


    private final BroadcastReceiver broadcastReceiverMusic = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle == null)
                return;

            song = (Song) bundle.get("song_Object");
            offline = (String) bundle.get("offline_song");
            isPlaying = bundle.getBoolean("player_status");
            int actionMusic = bundle.getInt("action_music");

            handlerMusic(actionMusic);
        }


    };

    private void handlerMusic(int action) {
        switch (action) {
            case ACTION_START:
                StartPlaying();
                ShowSong(song);
                fragment_main_player.setSong(song);
                break;

            case ACTION_PAUSE:

            case ACTION_RESUME:
                SetButton();
                break;

            case ACTION_CLEAR:
                PlayerIsOpen = false;
                miniPlayer.setVisibility(View.GONE);
                Menu.setVisibility(View.VISIBLE);
                Main_Player.setVisibility(View.GONE);
                break;
        }
    }

    private void StartPlaying() {
        if (!PlayerIsOpen) {
            Main_Player.setVisibility(View.VISIBLE);
            Menu.setVisibility(View.GONE);
            replacePlayer(fragment_main_player);
            PlayerIsOpen = true;
        }
    }

    private final BroadcastReceiver broadcastReceiverPlayer = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null)
                return;
            int actionPlayer = intent.getIntExtra("action_player", -1);
            handlerPlayer(actionPlayer);
        }

        private void handlerPlayer(int actionPlayer) {
            switch (actionPlayer) {
                case Player_Maximize:
                    miniPlayer.setVisibility(View.GONE);
                    break;
                case Player_Minimize:
                    Main_Player.setVisibility(View.GONE);
                    Menu.setVisibility(View.VISIBLE);
                    miniPlayer.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };
    private void SetButton() {
        if (isPlaying)
            play_pause_btn.setImageResource(R.drawable.pause_48px);
        else
            play_pause_btn.setImageResource(R.drawable.play_arrow_48px);
    }

    private void ShowSong(Song song) {

        Picasso.get().load(song.getCover()).into(img_song);
        titleSong.setText(song.getTitle());
        Singer.setText(song.getSinger());
        SetButton();

        play_pause_btn.setOnClickListener(v -> {
            if (isPlaying) {
                Send2Service(ACTION_PAUSE);
            } else
                Send2Service(ACTION_RESUME);
        });
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayerIsOpen = false;
                Send2Service(ACTION_CLEAR);
                miniPlayer.setVisibility(View.GONE);
                Main_Player.setVisibility(View.GONE);
            }
        });
    }


    private void Send2Service(int action) {
        Intent intent = new Intent(this, PlayerController.class);
        intent.putExtra("action_music", action);

        startService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.sub_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragment_main_player = new fragment_main_player();
        Main_Player = findViewById(R.id.MainPlayerLayoutContainer);
        Menu = findViewById(R.id.MenuContainer);
        miniPlayer = findViewById(R.id.miniPlayer);
        miniPlayer.setVisibility(View.GONE);

        Main_Player.setVisibility(View.VISIBLE);
        replacePlayer(fragment_main_player);
        Main_Player.setVisibility(View.GONE);


        replaceMenu(new fragment_home_page());

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverMusic, new IntentFilter("send_data_toActivity"));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverPlayer, new IntentFilter("send_data_toPlayer"));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverLogin, new IntentFilter("send_data_toLogin"));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiverLib, new IntentFilter("send_data_toLib"));


        miniPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                miniPlayer.setVisibility(View.GONE);
                Menu.setVisibility(View.GONE);
                Main_Player.setVisibility(View.VISIBLE);
                PlayerIsOpen = true;
            }
        });

        img_song = findViewById(R.id.img_song);

        titleSong = findViewById(R.id.textViewTittle);
        Singer = findViewById(R.id.textViewSinger);

        play_pause_btn = findViewById(R.id.play);
        close_btn = findViewById(R.id.close);

        home_btn = findViewById(R.id.home);
        home_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("GG", "Click");
                if (PlayerIsOpen) {
                    if (miniPlayer.getVisibility() == View.VISIBLE) {
                        //do nothing
                    } else miniPlayer.setVisibility(View.VISIBLE);
                    if (Main_Player.getVisibility() == View.VISIBLE)
                        Main_Player.setVisibility(View.GONE);
                }
                replaceMenu(fragment_home_page);
            }
        });
        Handler handler = new Handler();
        MainActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null) {
                    seekBar.setMax(mediaPlayer.getDuration() / 1000);
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);

                    int minutes = (mCurrentPosition) / 60;
                    int seconds = (mCurrentPosition) % 60;
                    @SuppressLint("DefaultLocale") String time = String.format("%02d:%02d", minutes, seconds);
                    passes.setText(time);
                }
                handler.postDelayed(this, 1000);
            }
        });

        songList_btn = findViewById(R.id.music);
        /*songList_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("GG", "Click");
                if (PlayerIsOpen) {
                    if (miniPlayer.getVisibility() == View.VISIBLE) {
                        //do nothing
                    } else miniPlayer.setVisibility(View.VISIBLE);
                    if (Main_Player.getVisibility() == View.VISIBLE)
                        Main_Player.setVisibility(View.GONE);
                }
                replaceMenu(fragment_library);
            }
        });*/

        search_btn = findViewById(R.id.search);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("GG", "Click");
                if (PlayerIsOpen) {
                    if (miniPlayer.getVisibility() == View.VISIBLE) {
                        //do nothing
                    } else miniPlayer.setVisibility(View.VISIBLE);
                    if (Main_Player.getVisibility() == View.VISIBLE)
                        Main_Player.setVisibility(View.GONE);
                }
                replaceMenu(fragment_search);
            }
        });

        setting_btn = findViewById(R.id.setting);
        setting_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    ProfileDAO profileDAO = new ProfileDAO();
                    profileDAO.getProfileByEmail(user.getEmail(), new ProfileDAO.GetProfileCallback() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void onSuccess(Profile profile) {
                            // Cập nhật UI với thông tin từ profile
                            if (profile.getAdmin()) {
                                if (PlayerIsOpen) {
                                    if (miniPlayer.getVisibility() == View.VISIBLE) {
                                        //do nothing
                                    } else miniPlayer.setVisibility(View.VISIBLE);
                                    if (Main_Player.getVisibility() == View.VISIBLE)
                                        Main_Player.setVisibility(View.GONE);
                                }
                                replaceMenu(fragment_homePage_admin);
                            }

                            else
                                Toast.makeText(getApplicationContext(), "ADMIN ONLY", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            // Xử lý lỗi nếu cần thiết
                            Toast.makeText(getApplicationContext(), "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }


                else
                    Toast.makeText(getApplicationContext(), "ADMI ONLY", Toast.LENGTH_LONG).show();

            }
        });

    }

    private void replaceLib(Fragment fragment, String Genre) {
        try {
            Menu.setVisibility(View.VISIBLE);
            Bundle args = new Bundle();
            args.putString("genre", genre);
            fragment.setArguments(args);
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.MenuContainer, fragment, null);
            fragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void replaceMenu(Fragment fragment) {
        try {
            Menu.setVisibility(View.VISIBLE);
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.MenuContainer, fragment, null);
            fragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void replacePlayer(Fragment fragment) {
        try {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.MainPlayerLayoutContainer, fragment, null);
            fragmentTransaction.commit();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onDestroy() {
        deleteCache(this);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverMusic);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverPlayer);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverLogin);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiverLib);
        super.onDestroy();
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public void navigateToFragment(Fragment fragment) {
        try {
            Menu.setVisibility(View.VISIBLE);
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.MenuContainer, fragment, null);
            fragmentTransaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}