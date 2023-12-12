package com.example.rizzplayer.DAO;

import static com.example.rizzplayer.Fragment.fragment_upload.newInstance;
import static com.example.rizzplayer.Service.PlayerController.playlist;
import static com.example.rizzplayer.Fragment.fragment_upload.progressBar;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import static com.example.rizzplayer.Fragment.fragment_search.updateRecyclingView;
import static com.example.rizzplayer.Fragment.fragment_library.updateLibRecyclingView;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.example.rizzplayer.R;
import com.example.rizzplayer.Business.Song;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;


public class SongDAO {
    List<Song> uploadList;
    String str = "";
    public static Song song;
    Context context;
    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Music");

    StorageReference mp3StorageRef = FirebaseStorage.getInstance().getReference("Music");

    StorageReference coverStorageRef = FirebaseStorage.getInstance().getReference("Cover");


    public SongDAO(){}

    public void getListByKeyword(Context context, String str) {
        uploadList = new ArrayList<>();
        dbref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dtss : snapshot.getChildren()) {
                    Song song = dtss.getValue(Song.class);
                    if (song.getTitle().toLowerCase().contains(str)) {
                        song.setKey(dtss.getKey());
                        playlist.addSong(song);
                        uploadList.add(song);

                    }
                }
                if (uploadList.isEmpty())
                    Toast.makeText(context, "Không tìm thấy bài hát", Toast.LENGTH_LONG).show();
                updateRecyclingView(uploadList, context);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    public void getList(Context context){
        uploadList = new ArrayList<>();
        dbref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                //get the path or the object create/folder id to add it to a playList.
                for (DataSnapshot dtss : snapshot.getChildren()) {
                    Song song = dtss.getValue(Song.class);
                    song.setKey(dtss.getKey());
                    playlist.addSong(song);
                    uploadList.add(song);
                }
                updateRecyclingView(uploadList, context);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }



    public void deleteSong(Song song, String key, Context context){
        StorageReference mp3Ref = mp3StorageRef.getStorage().getReferenceFromUrl(song.getResource());
        mp3Ref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                StorageReference coverRef = coverStorageRef.getStorage().getReferenceFromUrl(song.getCover());
                coverRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        dbref.child(key).removeValue();
                        Toast.makeText(context, "Delete success",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }



    public void uploadFile(Context context, String fileName, Uri uri, String genre) {

        if (!fileName.equals("")) {
            StorageReference mp3ref = mp3StorageRef.child("Song_" + System.currentTimeMillis() + "." + getFileExtension(context, uri));

            //fragment_upload.song.setCover(downloadUrl.toString());
            StorageTask multiUploads = mp3ref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(0);
                        }

                    }, 500);

                    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                    mediaMetadataRetriever.setDataSource(context, uri);

                    String name = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
                    String artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
                    byte[] art = mediaMetadataRetriever.getEmbeddedPicture();

                    if (name == null)
                        name = "Noname";
                    if (artist == null)
                        artist = "Amongus";
                    if (art == null) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.omg);
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        art = stream.toByteArray();
                    }

                    StorageReference cover = coverStorageRef.child("cover_" + name + "_" + System.currentTimeMillis() + ".jpg");
                    String finalName = name;
                    String finalArtist = artist;
                    cover.putBytes(art).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful()) ;
                            Uri downloadUrl = urlTask.getResult();
                            //fragment_upload.song.setCover(downloadUrl.toString());
                            Song song = new Song(finalName, finalArtist, downloadUrl.toString(), str, genre);
                            String uploadId = dbref.push().getKey();
                            dbref.child(uploadId).setValue(song);
                            Toast.makeText(context, "Upload Success", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                    while (!urlTask.isSuccessful()) ;
                    Uri downloadUrl = urlTask.getResult();
                    str = (downloadUrl.toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context, "Upload Failed", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double Progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    progressBar.setProgress((int) Progress);
                }
            });
        } else {
            Toast.makeText(context, "No File Selected", Toast.LENGTH_SHORT).show();
        }
    }

    public String getFileExtension(Context context, Uri uri) {
        ContentResolver resolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(resolver.getType(uri));
    }

    public void downloadFiles(Context context, Song song){
        DownloadManager downloadManager =  (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(song.getResource());
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_MUSIC, song.getTitle() +".mp3");
        downloadManager.enqueue(request);

    }

    public void getListByGenre(Context context, String genre) {
        uploadList = new ArrayList<>();
        dbref.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dtss : snapshot.getChildren()) {
                    Song song = dtss.getValue(Song.class);
                    if (song.getGenre().contains(genre)) {
                        song.setKey(dtss.getKey());
                        playlist.addSong(song);
                        uploadList.add(song);

                    }
                }
                updateLibRecyclingView(uploadList, context);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
