package com.msaproject.catal.myappointment.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.text.SimpleDateFormat;
import java.util.Date;

@IgnoreExtraProperties
public class Reservation {
    private @ServerTimestamp Date timestamp;
    private String user_id;
    private String business_id;
    private @Exclude String startHour;
    private @Exclude int day;
    private @Exclude String monthYear;
    private String businessName;
    private Timestamp reservationBegin;

    public Reservation(Date timestamp, String user_id, String business_id, String startHour, int day, String monthYear, String businessName, Timestamp reservationBegin) {
        this.timestamp = timestamp;
        this.user_id = user_id;
        this.business_id = business_id;
        this.startHour = startHour;
        this.day = day;
        this.monthYear = monthYear;
        this.businessName = businessName;
        this.reservationBegin = reservationBegin;
    }

    public Reservation(){}


    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

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

    public String getBusiness_id() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }

    public Timestamp getReservationBegin() {
        return reservationBegin;
    }

    public String getReservationTime() {
        SimpleDateFormat hourdaymonthyear = new SimpleDateFormat("h:mm a, dd MMMM");
        return hourdaymonthyear.format(reservationBegin.toDate());
    }


    public void setReservationBegin(Timestamp reservationBegin) {
        this.reservationBegin = reservationBegin;
    }

    @Exclude
    public String getStartHour() {
        return startHour;
    }

    @Exclude
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
