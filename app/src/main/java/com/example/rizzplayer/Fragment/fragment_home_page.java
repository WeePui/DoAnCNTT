package com.example.rizzplayer.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.rizzplayer.Business.Profile;
import com.example.rizzplayer.DAO.ProfileDAO;
import com.example.rizzplayer.MainActivity;
import com.example.rizzplayer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import static com.example.rizzplayer.MainActivity.Lib_rock;
import static com.example.rizzplayer.MainActivity.Lib_pop;
import static com.example.rizzplayer.MainActivity.Lib_hiphop;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_home_page#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_home_page extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private CardView pop, rock, hiphop;

    private TextView nickname, logout;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_home_page() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_home_page.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_home_page newInstance(String param1, String param2) {
        fragment_home_page fragment = new fragment_home_page();
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
        handler = new Handler(Looper.getMainLooper());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_page, container, false);
    }

    Handler handler;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pop = view.findViewById(R.id.pop_music);
        pop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(Lib_pop, "Ballad");
            }
        });

        rock = view.findViewById(R.id.rock_music);
        rock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeFragment(Lib_rock, "Rock");
            }
        });

        hiphop = view.findViewById(R.id.hiphop_music);
        hiphop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                changeFragment(Lib_hiphop, "Phonk");
            }
        });

        nickname = view.findViewById(R.id.user);
        nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(MainActivity.LOGIN);
            }
        });

        logout = (TextView) view.findViewById(R.id.btn_logout);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            logout.setVisibility(View.VISIBLE);

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signOut();
                }
            });

            ProfileDAO profileDAO = new ProfileDAO();
            profileDAO.getProfileByEmail(Objects.requireNonNull(user.getEmail()), new ProfileDAO.GetProfileCallback() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onSuccess(Profile profile) {
                    // Cập nhật UI với thông tin từ profile
                    if (profile.getAdmin())
                        nickname.setText("Admin");
                    else
                        nickname.setText(profile.getNickname());
                }

                @Override
                public void onFailure(String errorMessage) {
                    // Xử lý lỗi nếu cần thiết
                    Toast.makeText(getContext(), "Lỗi: " + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            logout.setVisibility(View.GONE);
        }
    }

    private void signOut(){
        FirebaseAuth.getInstance().signOut();
        changeFragment(MainActivity.BACK);
        Toast.makeText(getContext(), "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show();
    }
    private void changeFragment(int action) {
        Intent intent = new Intent("send_data_toLogin");
        intent.putExtra("action_login", action);

        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }

    private void changeFragment(int action, String gerne) {
        Intent intent = new Intent("send_data_toLib");
        intent.putExtra("action_Lib", action);
        intent.putExtra( "type", gerne);

        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }


}