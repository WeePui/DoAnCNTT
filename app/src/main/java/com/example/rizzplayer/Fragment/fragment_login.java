package com.example.rizzplayer.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rizzplayer.DAO.UserDAO;
import com.example.rizzplayer.MainActivity;
import com.example.rizzplayer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_login#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_login extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_login() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_login.
     */
    // TODO: Rename and change types and number of parameters

    private TextView btn_signup, btn_back;
    private Button btn_login;
    private EditText edt_email, edt_password;

    public static fragment_login newInstance(String param1, String param2) {
        fragment_login fragment = new fragment_login();
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
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_signup = view.findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(MainActivity.SIGNUP);
            }
        });

        btn_back = view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(MainActivity.BACK);
            }
        });

        btn_login = (Button) view.findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logIn();
            }
        });
    }

    private void logIn() {
        edt_email = (EditText) getView().findViewById(R.id.edt_email);
        edt_password = (EditText) getView().findViewById(R.id.edt_password);

        String email = edt_email.getText().toString().toLowerCase().trim();
        String password = edt_password.getText().toString().trim();

        UserDAO.signIn(email, password, new UserDAO.SignInCallback() {
            @Override
            public void onComplete(boolean isSuccess, String errorMessage) {
                if (isAdded()) {
                    if (isSuccess) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        Toast.makeText(requireActivity(), "Đăng nhập thành công !!!\n" + user.getEmail(),
                                Toast.LENGTH_SHORT).show();
                        changeFragment(MainActivity.BACK);
                    } else {
                        Toast.makeText(requireActivity(), "Lỗi đăng nhập, vui lòng kiểm tra lại email và mật khẩu",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void changeFragment(int action) {
        Intent intent = new Intent("send_data_toLogin");
        intent.putExtra("action_login", action);

        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }
}