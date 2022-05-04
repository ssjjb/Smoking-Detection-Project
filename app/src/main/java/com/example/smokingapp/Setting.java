package com.example.smokingapp;

import android.graphics.drawable.Drawable;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class Setting {
    String title;
    Drawable icon;
    boolean flag;
    boolean toggle;

    public Setting(String title, Drawable icon, boolean flag, boolean toggle) {
        this.title = title;
        this.icon = icon;
        this.flag = flag;
        this.toggle = toggle;
    }

    public String getTitle() { return title; }

    public void setTitle(String title) {
        this.title = title;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean isToggle() { return toggle; }

    public void setToggle(boolean toggle) { this.toggle = toggle; }}
