package com.msaproject.catal.myappointment.models;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class Reservation {
    private @ServerTimestamp Date timestamp;
    private String user_id;
    private String startHour;
    private @Exclude int day;
    private @Exclude String monthYear;

    public Reservation(Date timestamp, String user_id, String startHour, int day, String monthYear) {
        this.timestamp = timestamp;
        this.user_id = user_id;
        this.startHour = startHour;
        this.day = day;
        this.monthYear = monthYear;
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

    public String getStartHour() {
        return startHour;
    }

    public void setStartHour(String startHour) {
        this.startHour = startHour;
    }

    @Exclude
    public int getDay() {
        return day;
    }

    @Exclude
    public void setDay(String day) {
        this.day = Integer.parseInt(day);
    }

    @Exclude
    public String getMonthYear() {
        return monthYear;
    }

    @Exclude
    public void setMonthYear(String monthYear) {
        this.monthYear = monthYear;
    }
}
