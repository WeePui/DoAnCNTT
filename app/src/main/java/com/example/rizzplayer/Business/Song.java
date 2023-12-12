package com.example.rizzplayer.Business;

import android.media.MediaMetadataRetriever;

import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Song implements Serializable {
    private String title, singer, resource, cover, genre, ID;
    private String key;

    @Exclude
    public String getKey() {
        return key;
    }

    @Exclude
    public void setKey(String key) {
        this.key = key;
    }

    public Song(){
    }
    public Song(String title, String singer, String cover, String resource, String genre) {
        this.title = title;
        this.singer = singer;
        this.cover = cover;
        this.genre = genre;
        this.resource = resource;
    }
    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getCover() { return cover; }
    public String getTitle() {
        return title;
    }
    public String getSinger() {
        return singer;
    }
    public String getResource() {
        return resource;
    }


    public void setSinger(String singer) {
        this.singer = singer;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }
}
