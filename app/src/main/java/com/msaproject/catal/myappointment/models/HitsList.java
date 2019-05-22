package com.msaproject.catal.myappointment.models;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@IgnoreExtraProperties
public class HitsList {

    @SerializedName("hits")
    @Expose
    private List<BusinessSource> businessIndex;

    public List<BusinessSource> getBusinessIndex() {
        return businessIndex;
    }

    public void setBusinessIndex(List<BusinessSource> businessIndex) {
        this.businessIndex = businessIndex;
    }
}
