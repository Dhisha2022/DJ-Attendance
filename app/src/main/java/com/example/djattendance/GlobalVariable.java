package com.example.djattendance;

import android.app.Application;

public class GlobalVariable extends Application {

    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String name) {
        this.userName = name;
    }
}
