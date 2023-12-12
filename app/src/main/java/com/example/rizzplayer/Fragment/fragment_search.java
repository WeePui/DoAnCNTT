package com.example.rizzplayer.Fragment;

import  static com.example.rizzplayer.Service.PlayerController.playlist;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rizzplayer.DAO.SongDAO;
import com.example.rizzplayer.R;
import com.example.rizzplayer.Business.Song;

import java.util.ArrayList;
import java.util.List;


public class fragment_search extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_search() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fragment_search.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_search newInstance(String param1, String param2) {
        fragment_search fragment = new fragment_search();
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
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    static RecyclerView recyclerView;
    static SongAdapter songAdapter;
    ImageView Search;
    EditText searchBar;
    List<Song> uploadList;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recycle_View);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Search = view.findViewById(R.id.search_icon);

        uploadList = new ArrayList<>();
        ShowList();
        searchBar = view.findViewById(R.id.search_bar);
        searchBar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    ShowList(searchBar.getText().toString().toLowerCase().trim());
                    return true;
                }
                return false;
            }
        });
        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowList(searchBar.getText().toString().toLowerCase().trim());
            }
        });
    }

    private void ShowList(String str){
        uploadList.clear();
        playlist.clear();

        SongDAO songDAO = new SongDAO();

        songDAO.getListByKeyword(getContext(), str.toLowerCase().trim());

    }
    private void ShowList(){
        uploadList.clear();
        playlist.clear();

        SongDAO songDAO= new SongDAO();

        songDAO.getList(getContext());


    }
    public static void updateRecyclingView(List<Song> uploadList, Context context){
        songAdapter = new SongAdapter(context, uploadList);
        recyclerView.setAdapter(songAdapter);
    }
}