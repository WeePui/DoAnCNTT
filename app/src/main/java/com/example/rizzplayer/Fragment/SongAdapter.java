package com.example.rizzplayer.Fragment;

import  static com.example.rizzplayer.Service.PlayerController.playlist;
import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rizzplayer.Business.Song;
import com.example.rizzplayer.DAO.SongDAO;
import com.example.rizzplayer.R;
import com.example.rizzplayer.Service.PlayerController;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ImageViewHolder>{

    public Context context;
    public List<Song> uploadList;
    String str;

    public SongAdapter(Context context, List<Song> uploadList){
        this.context = context;
        this.uploadList = uploadList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.song_item, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        if(uploadList.isEmpty()){
            Toast.makeText(context, "Not found!", Toast.LENGTH_SHORT).show();
        }
            Song currentSong = uploadList.get(position);
            holder.Name.setText(currentSong.getTitle());
            holder.Artist.setText(currentSong.getSinger());
            Picasso.get().load(currentSong.getCover()).into(holder.cover);

    }

    @Override
    public int getItemCount() {
        if(uploadList != null)
            return this.uploadList.size();
        else return 0;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{

        public TextView Name, Artist;
        public ImageView cover;
        public LinearLayout layout;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.test);
            Artist = itemView.findViewById(R.id.textViewArtist);
            Name = itemView.findViewById(R.id.textViewName);
            cover = itemView.findViewById(R.id.cover);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Song song = uploadList.get(getAdapterPosition());
                    playlist.setCurrent(getAdapterPosition());
                    StartMusic(song);
                }
            });
            layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(context, layout);
                    popupMenu.inflate(R.menu.sub_menu);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            Song song = uploadList.get(getAdapterPosition());
                            int id = menuItem.getItemId();
                            if (id == R.id.download){
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                if (user != null) {
                                    downloadSong(song);
                                }
                                else
                                    Toast.makeText(context, "Hãy đăng nhập để tải về bài hát", Toast.LENGTH_LONG).show();
                            }
                            if (id == R.id.addToFav) {
                               //To do thêm vào danh sách phát là List String trong profile business và update profile trong realtime DB
                                Toast.makeText(context, "Tính năng chưa được hoàn thiện", Toast.LENGTH_LONG).show();
                            }
                            else if( id == R.id.listen){
                                    playlist.setCurrent(getAdapterPosition());
                                    StartMusic(song);
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                    return true;
                }
            });
        }
    }


    private void downloadSong(Song song) {
        SongDAO songDAO = new SongDAO();
        songDAO.downloadFiles(context, song);
        Toast.makeText(context, song.getTitle() + " is downloading", Toast.LENGTH_SHORT).show();
    }

    private void StartMusic(Song song) {
        Intent intent = new Intent(context, PlayerController.class);
        Bundle bundle = new Bundle();

        bundle.putSerializable("song_Object", song);
        intent.putExtras(bundle);

        context.startService(intent);
    }

}
