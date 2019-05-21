package com.msaproject.catal.myappointment.models;

public class Business {
    private String business_id;
    private String user_id;
    private String description;
    //private String workingHours;
    private String country;
    private String state_province;
    private String city;
    private String price;
    private String email;
    private String phoneNo;
    private String name;
    private String image;

    public Business(String business_id, String user_id, String description, /*String workingHours,*/ String country, String state_province, String city, String price, String email, String phoneNo, String name, String image) {
        this.business_id = business_id;
        this.user_id = user_id;
        this.description = description;
        /*this.workingHours = workingHours;*/
        this.country = country;
        this.state_province = state_province;
        this.city = city;
        this.price = price;
        this.email = email;
        this.phoneNo = phoneNo;
        this.name = name;
        this.image = image;
    }

    public Business(){};

    public String getId() {
        return business_id;
    }

    public void setBusiness_id(String business_id) {
        this.business_id = business_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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

    /*public String getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(String workingHours) {
        this.workingHours = workingHours;
    }*/

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getState_province() {
        return state_province;
    }

    public void setState_province(String state_province) {
        this.state_province = state_province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "Business{" +
                "business_id='" + business_id + '\'' +
                ", user_id='" + user_id + '\'' +
                ", description='" + description + '\'' +
                /*", workingHours='" + workingHours + '\'' +*/
                ", country='" + country + '\'' +
                ", state_province='" + state_province + '\'' +
                ", city='" + city + '\'' +
                ", price='" + price + '\'' +
                ", email='" + email + '\'' +
                ", phoneNo='" + phoneNo + '\'' +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
