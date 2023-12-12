package com.example.rizzplayer.Business;

import com.example.rizzplayer.DAO.UserDAO;

import java.util.Date;
import java.util.List;

public class User {
    String UID;
    String email;
    String password;

    Date createdDate;
    Date signedInDate;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
        UID = null;
        createdDate = null;
        signedInDate = null;
    }

    public User(String UID, String email, String password, Date createdDate, Date signedInDate) {
        this.UID = UID;
        this.email = email;
        this.password = password;
        this.createdDate = createdDate;
        this.signedInDate = signedInDate;
    }


    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getSignedInDate() {
        return signedInDate;
    }

    public void setSignedInDate(Date signedInDate) {
        this.signedInDate = signedInDate;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public UserDAO accessData() {
        UserDAO userDAO = new UserDAO();
        return userDAO;
    }
}
