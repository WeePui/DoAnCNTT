package com.example.rizzplayer.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rizzplayer.DAO.SongDAO;
import com.example.rizzplayer.MainActivity;
import com.example.rizzplayer.R;
import com.example.rizzplayer.Business.Song;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_Song_Management#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_Song_Management extends Fragment implements SongAdapterAdmin.OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Fragment_Song_Management() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Fragment_Song_Management.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Song_Management newInstance(String param1, String param2) {
        Fragment_Song_Management fragment = new Fragment_Song_Management();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment__song__management, container, false);


        fragment_upload fragmentUpload = new fragment_upload();
        Button uploadBtn = view.findViewById(R.id.open_upload_fragment);
        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).navigateToFragment(fragmentUpload);
            }
        });

        return view;
    }


    RecyclerView recyclerView;
    SongAdapterAdmin songAdapterAdmin;
    EditText searchBar;
    DatabaseReference dbref;
    private StorageReference mp3StorageRef;
    private StorageReference coverStorageRef;

    private ValueEventListener dbListener;

    List<Song> uploadList;




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mp3StorageRef = FirebaseStorage.getInstance().getReference("Music");
        coverStorageRef = FirebaseStorage.getInstance().getReference("Cover");
        dbref = FirebaseDatabase.getInstance().getReference("Music");
        recyclerView = view.findViewById(R.id.recycle_View);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        uploadList = new ArrayList<>();
        ShowList();
    }

    private void ShowList(){
        uploadList.clear();
        dbListener = dbref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                uploadList.clear();
                for (DataSnapshot dtss : snapshot.getChildren()) {
                    Song song = dtss.getValue(Song.class);
                    //MainActivity.playlist.addSong(song);
                    song.setKey(dtss.getKey());
                    uploadList.add(song);
                }
                songAdapterAdmin = new SongAdapterAdmin(getContext(), uploadList);
                songAdapterAdmin.setOnItemClickListener(Fragment_Song_Management.this);
                recyclerView.setAdapter(songAdapterAdmin);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Toast.makeText(getContext(), "Click at position: " + position,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteClick(int position) {
        Song selectedSong = uploadList.get(position);
        String selectedKey = selectedSong.getKey();

        SongDAO songDAO = new SongDAO();
        songDAO.deleteSong(selectedSong, selectedKey, getContext());
        ShowList();
    }

    private void downloadSong(Song song) {
        SongDAO songDAO = new SongDAO();
        songDAO.downloadFiles(getContext(), song);
        Toast.makeText(getContext(), song.getTitle() + " is downloading", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDownloadClick(int position) {
        Song selectedSong = uploadList.get(position);
        downloadSong(selectedSong);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbref.removeEventListener(dbListener);
    }
}