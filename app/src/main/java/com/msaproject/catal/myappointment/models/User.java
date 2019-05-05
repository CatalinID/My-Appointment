package com.msaproject.catal.myappointment.models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class User {
    private String userName;
    private String userEmail;
    private FirebaseUser user;
    private String userID;

    public User(FirebaseAuth auth){

        user = auth.getCurrentUser();

        this.userName = user.getDisplayName();
        this.userEmail = user.getEmail();
        this.userID = user.getUid();
    }

    public String getUserName() {
        return userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserID() {
        return userID;
    }
}
