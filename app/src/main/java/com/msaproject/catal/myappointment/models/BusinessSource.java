package com.msaproject.catal.myappointment.models;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@IgnoreExtraProperties
public class BusinessSource {

    @SerializedName("_source")
    @Expose
    private Business business;


    public Business getBusiness() {
        return business;
    }

    public void setBusiness(Business post) {
        this.business = business;
    }
}
