package com.example.savethedate.Models;

import java.util.Date;

public class ServiceReservationModel {

    private int id;
    private double price;
    private java.sql.Date date;
    private int event;
    private int service;
    private int confirmed;

    public int getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(int confirmed) {
        this.confirmed = confirmed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public java.sql.Date getDate() {
        return date;
    }

    public void setDate(java.sql.Date date) {
        this.date = date;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public int getService() {
        return service;
    }

    public void setService(int service) {
        this.service = service;
    }
}
