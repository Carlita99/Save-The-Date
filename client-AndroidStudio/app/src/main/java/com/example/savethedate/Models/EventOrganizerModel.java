package com.example.savethedate.Models;

public class EventOrganizerModel extends UserModel {

    public String organizerName;

    public UserModel getUser(){
        return new EventOrganizerModel();
    }

    public void updateUserInfo(){

    }
}
