package com.example.rizzplayer.Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.rizzplayer.DAO.SongDAO;
import com.example.rizzplayer.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_upload#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_upload extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public fragment_upload() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Upload.
     */
    // TODO: Rename and change types and number of parameters
    public static fragment_upload newInstance(String param1, String param2) {
        fragment_upload fragment = new fragment_upload();
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
        return inflater.inflate(R.layout.fragment_upload, container, false);
    }
    TextView filename;

    String genre;
    Button songBtn, coverBtn, UploadBtn;
    ImageView imgView;
    public static ProgressBar progressBar;

    private StorageReference mp3StorageRef, coverStorageRef;
    private DatabaseReference databaseReference;
    private StorageTask multiUploads;
    Uri Mp3_uri;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        genre = "Undefined";
        filename = view.findViewById(R.id.fileName);
        songBtn = view.findViewById(R.id.songBtn);
        UploadBtn = view.findViewById(R.id.uploadBTN);
        imgView = view.findViewById(R.id.imgView);
        progressBar = view.findViewById(R.id.progessbar);
        //get the spinner from the xml.
        Spinner dropdown = view.findViewById(R.id.spinner1);
        String[] items = new String[]{"Phonk", "Dubstep", "Rock", "Trữ Tình", "Ballad", "RnB", "EDM"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                genre = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                genre = "Undefined";
            }
        });

        songBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFile("audio/*");
            }
        });


        UploadBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onClick(View view) {
                if (multiUploads != null && multiUploads.isInProgress()) {
                    Toast.makeText(getContext(), "Upload is in progress!", Toast.LENGTH_LONG).show();
                } else{
                    SongDAO songDAO = new SongDAO();
                    songDAO.uploadFile(getContext(), filename.getText().toString(), Mp3_uri, genre);
                }
            }
        });

    }

    private void openFile(String str) {
        Intent intent = new Intent();
        intent.setType(str);
        intent.setAction(intent.ACTION_GET_CONTENT);
        someActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Uri uri = data.getData();

                        Mp3_uri = data.getData();

                        Cursor returnCursor = getContext().getContentResolver().query(Mp3_uri, null, null, null, null);

                        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        returnCursor.moveToFirst();
                        filename.setText(returnCursor.getString(nameIndex));
                    }
                }
            });

}