package com.example.rizzplayer.Business;

import com.example.rizzplayer.DAO.ProfileDAO;

import java.util.List;

public class Profile {
    String email;
    String DoB;
    String gender;
    String nickname;
    List<String> favList;
    boolean admin;

    public Profile(){

    }

    public Profile(String email, String doB, String gender, String nickname, boolean admin) {
        this.email = email;
        DoB = doB;
        this.gender = gender;
        this.nickname = nickname;
        this.admin = admin;
    }
    public void addSong(String song) {
        favList.add(song);
    }

    public List<String> getFavList() {
        return favList;
    }

    public String getEmail() {
        return email;
    }
    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean getAdmin() { return admin;}

    public String getDoB() {
        return DoB;
    }

    public String getGender() {
        return gender;
    }

    public ProfileDAO accessData(Profile this){
        ProfileDAO profileDAO = new ProfileDAO();
        return profileDAO;
    }
}
