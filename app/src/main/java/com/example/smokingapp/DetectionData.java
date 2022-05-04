package com.example.smokingapp;

import android.graphics.drawable.Drawable;

public class DetectionData {
    private Drawable icon;
    private String title;
    private String time;

    public DetectionData(Drawable icon, String title, String time) {
        this.icon = icon;
        this.title = title;
        this.time = time;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
