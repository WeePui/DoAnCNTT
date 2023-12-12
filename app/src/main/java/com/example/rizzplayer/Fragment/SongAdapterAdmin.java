package com.example.rizzplayer.Fragment;

import android.content.Context;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rizzplayer.Business.Song;
import com.example.rizzplayer.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SongAdapterAdmin extends RecyclerView.Adapter<SongAdapterAdmin.ImageViewHolder> {

    public Context context;
    public List<Song> uploadList;
    private OnItemClickListener listener;


    public SongAdapterAdmin(Context context, List<Song> uploadList) {
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
        if (uploadList.isEmpty()) {

            Toast.makeText(context, "Not found!", Toast.LENGTH_SHORT).show();
        }
            Song currentSong = uploadList.get(position);
            holder.Name.setText(currentSong.getTitle());
            holder.Artist.setText(currentSong.getSinger());
            Picasso.get().load(currentSong.getCover()).into(holder.cover);

    }

    @Override
    public int getItemCount() {
        return uploadList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        public TextView Name, Artist;
        public ImageView cover;
        public LinearLayout layout;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            layout = itemView.findViewById(R.id.test);
            Artist = itemView.findViewById(R.id.textViewArtist);
            Name = itemView.findViewById(R.id.textViewName);
            cover = itemView.findViewById(R.id.cover);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);

        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuItem delete = contextMenu.add(Menu.NONE,1,1,"Xóa");
            MenuItem download = contextMenu.add(Menu.NONE,2,2,"Tải về");
            delete.setOnMenuItemClickListener(this);
            download.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(@NonNull MenuItem menuItem) {
            if(listener != null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    switch (menuItem.getItemId()){
                        case 1:
                            listener.onDeleteClick(position);
                            return true;
                        case 2:
                            listener.onDownloadClick(position);
                            return true;
                    }
                }
            }
            return false;
        }

        @Override
        public void onClick(View view) {
            if(listener != null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    listener.onItemClick(position);
                }
            }
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
        void onDeleteClick(int position);
        void onDownloadClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }
}

