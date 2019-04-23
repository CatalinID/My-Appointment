package com.msaproject.catal.myappointment.models;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class User {
    private String userName;
    private String userEmail;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String userID;

    private static User userInstance = null;

    private User(){
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        this.userName = user.getDisplayName();
        this.userEmail = user.getEmail();
        this.userID = user.getUid();
    }

    public static User getInstance(){
        if (userInstance == null){
            userInstance = new User();
        }
        return userInstance;
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
