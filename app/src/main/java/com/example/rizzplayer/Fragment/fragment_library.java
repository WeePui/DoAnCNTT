package com.example.rizzplayer.Fragment;


import static com.example.rizzplayer.Service.PlayerController.playlist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rizzplayer.Business.Song;
import com.example.rizzplayer.DAO.SongDAO;
import com.example.rizzplayer.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class fragment_library extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_library() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_library.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_library newInstance(String param1, String param2) {
        fragment_library fragment = new fragment_library();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    LinearLayout layout;

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
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    static RecyclerView recyclerView;
    static TextView LIB;
    String genre;
    static SongAdapter songAdapter;
    List<Song> uploadList;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            genre = args.getString("genre");
        }
        LIB = view.findViewById(R.id.title);
        LIB.setText(genre.toUpperCase());

        recyclerView = view.findViewById(R.id.lib_recycle_View);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        uploadList = new ArrayList<>();
        if(Objects.equals(genre, ""))
            genre = "Rock";
        ShowList(genre);

    }

    private void ShowList(String genre){
        uploadList.clear();
        playlist.clear();

        SongDAO songDAO= new SongDAO();

        songDAO.getListByGenre(getContext(), genre);

    }
    public static void updateLibRecyclingView(List<Song> uploadList, Context context){
        songAdapter = new SongAdapter(context, uploadList);
        recyclerView.setAdapter(songAdapter);

    }

}
