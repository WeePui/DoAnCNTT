package com.example.rizzplayer.Fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rizzplayer.Business.User;
import com.example.rizzplayer.DAO.UserDAO;
import com.example.rizzplayer.MainActivity;
import com.example.rizzplayer.Business.Profile;
import com.example.rizzplayer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_signup#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_signup extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_signup() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_signup.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_signup newInstance(String param1, String param2) {
        fragment_signup fragment = new fragment_signup();
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

    private EditText edt_username, edt_password, edt_passwordagain, edt_email, edt_dob;
    private RadioGroup radio_group_gender;
    private Button btn_signup;
    private TextView btn_back;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_signup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn_back = (TextView) view.findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(MainActivity.LOGIN);
            }
        });

        edt_dob = (EditText) view.findViewById(R.id.edt_dob);
        edt_dob.setShowSoftInputOnFocus(false);
        edt_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        edt_email = (EditText) view.findViewById(R.id.edt_email);
        edt_password = (EditText) view.findViewById(R.id.edt_passwordS);
        edt_passwordagain = (EditText) view.findViewById(R.id.edt_password_again);

        btn_signup = (Button) view.findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        edt_username = (EditText) view.findViewById(R.id.edt_nickname);
        radio_group_gender = (RadioGroup) view.findViewById(R.id.radio_group_gender);
    }

    private void signUp() {
        String nickname = edt_username.getText().toString().trim();
        String email = edt_email.getText().toString().toLowerCase().trim();
        String password = edt_password.getText().toString().trim();
        String passwordAgain = edt_passwordagain.getText().toString().trim();
        String doB = edt_dob.getText().toString().trim();
        String gender = "";

        int checkedRadioButtonId = radio_group_gender.getCheckedRadioButtonId();

        if (checkedRadioButtonId != -1) {
            RadioButton radioButton = (RadioButton) getView().findViewById(checkedRadioButtonId);
            gender = radioButton.getText().toString();
        } else {
            Toast.makeText(requireContext().getApplicationContext(), "Chưa chọn giới tính !!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(nickname)) {
            Toast.makeText(requireContext().getApplicationContext(), "Nhập vào nickname !!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nickname.length() > 8) {
            Toast.makeText(requireContext().getApplicationContext(), "Nickname quá dài hãy nhập nhiều nhất là 8 kí tự !!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(requireContext().getApplicationContext(), "Nhập vào email !!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(requireContext().getApplicationContext(), "Nhập vào mật khẩu !!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(passwordAgain)) {
            Toast.makeText(requireContext().getApplicationContext(), "Nhập lại mật khẩu không đúng !!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(requireContext().getApplicationContext(), "Mật khẩu quá ngắn hãy nhập ít nhất 6 kí tự !!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(doB)) {
            Toast.makeText(requireContext().getApplicationContext(), "Chưa chọn ngày sinh !!!!", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User(email, password);
        user.accessData().createUser(user, new UserDAO.CreateUserCallback() {
            @Override
            public void onComplete(String email, String errorMessage) {
                if (email != null) {
                    Toast.makeText(requireActivity(), "Đăng kí thành công !!!",
                            Toast.LENGTH_SHORT).show();
                    changeFragment(MainActivity.BACK);
                } else {
                    if (errorMessage.equals("ERROR_EMAIL_ALREADY_IN_USE")){
                        Toast.makeText(requireActivity(), "Địa chỉ email đã có người sử dụng",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireActivity(), "Authentication failed." + errorMessage,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        String encodedEmail = email.replace(".", ",");

        Profile profile = new Profile(encodedEmail, doB, gender, nickname, false);
        profile.accessData().saveProfile(profile);
    }

    private void changeFragment(int action) {
        Intent intent = new Intent("send_data_toLogin");
        intent.putExtra("action_login", action);

        LocalBroadcastManager.getInstance(getContext()).sendBroadcast(intent);
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt_dob.getWindowToken(), 0);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDayOfMonth) {
                        calendar.set(selectedYear, selectedMonth, selectedDayOfMonth);

                        if (calendar.after(Calendar.getInstance())) {
                            Toast.makeText(requireContext().getApplicationContext(), "Ngày sinh không hợp lệ !!!!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        String formattedDate = dateFormat.format(calendar.getTime());

                        edt_dob.setText(formattedDate);
                    }
                }, year, month, dayOfMonth);

        datePickerDialog.show();
    }
}