package com.example.savethedate.Models;

public class ServiceProviderModel extends UserModel {

    public String providerName;

    public UserModel getUser(){
        return new ServiceProviderModel();
    }

    public void updateUserInfo(){

    }
}
