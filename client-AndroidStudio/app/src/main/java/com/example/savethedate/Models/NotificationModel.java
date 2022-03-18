package com.example.savethedate.Models;

import java.util.Date;

public class NotificationModel {

    private int id;
    private String user;
    private Date DateNot;
    private String title;
    private String body;
    private String Status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Date getDateNot() {
        return DateNot;
    }

    public void setDateNot(Date dateNot) {
        DateNot = dateNot;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }
}
