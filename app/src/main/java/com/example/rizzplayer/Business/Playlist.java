package com.example.rizzplayer.Business;

import com.example.rizzplayer.DAO.ProfileDAO;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class Playlist {
    private final String name;
    private String email;
    private int current;
    private final ArrayList<Song> songs;

    public Playlist(String name) {
        this.name = name;
        this.songs = new ArrayList<Song>();
    }

    public Playlist(String name, String email) {
        this.name = name;
        this.email = email;
        this.songs = new ArrayList<Song>();
    }

    public String getName() {
        return name;
    }

    public Song getSong(int i) {
        current = i;
        return this.songs.get(i);
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getIndex() {
        return this.songs.size();
    }

    public int getCurrent() {
        return current;
    }

    public String getEmail() {
        return email;
    }

    public void addSong(Song song) {
        this.songs.add(song);
    }

    public void removeSong(int index) {
        this.songs.remove(index);
    }

    public void clear() {
        songs.clear();
    }

    public ProfileDAO accessData(){
        ProfileDAO profileDAO = new ProfileDAO();
        return profileDAO;
    }
}
