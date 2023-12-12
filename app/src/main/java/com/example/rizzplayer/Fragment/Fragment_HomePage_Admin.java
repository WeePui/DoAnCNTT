package com.example.rizzplayer.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.example.rizzplayer.MainActivity;
import com.example.rizzplayer.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_HomePage_Admin#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_HomePage_Admin extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_HomePage_Admin() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_HomePage_Admin.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_HomePage_Admin newInstance(String param1, String param2) {
        Fragment_HomePage_Admin fragment = new Fragment_HomePage_Admin();
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
        View view = inflater.inflate(R.layout.fragment__home_page__admin, container, false);

        Fragment_Song_Management fragment_song_management = new Fragment_Song_Management();

        LinearLayout layout_song_management;
        layout_song_management = view.findViewById(R.id.song_management);
        layout_song_management.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).navigateToFragment(fragment_song_management);
            }
        });

        return view;
    }
}