package com.msaproject.catal.myappointment.models;

import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class Reservation {
    private @ServerTimestamp Date timestamp;
    private String user_id;
    private String reservationStart;

    public Reservation(Date timestamp, String user_id, String reservationStart) {
        this.timestamp = timestamp;
        this.user_id = user_id;
    }

    public Reservation(){}

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getReservationStart() {
        return reservationStart;
    }

    public void setReservationStart(String reservationStart) {
        this.reservationStart = reservationStart;
    }
}
