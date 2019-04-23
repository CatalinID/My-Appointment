package com.msaproject.catal.myappointment.models;

public class Business {
    private String id;
    private String description;
    private String workingHours;
    private String address;
    private String prices;
    private String email;
    private String phoneNo;
    private String name;

    public Business(String name, String id, String description, String workingHours, String address, String prices, String email, String phoneNo) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.workingHours = workingHours;
        this.address = address;
        this.prices = prices;
        this.email = email;
        this.phoneNo = phoneNo;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPrices() {
        return prices;
    }

    public void setPrices(String prices) {
        this.prices = prices;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
}
