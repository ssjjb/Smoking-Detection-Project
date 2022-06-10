package com.example.smokingapp;

import java.util.ArrayList;

public class Module {
    String title;
    String time;
    int detected_person;
    int detected_smoking;
    int color;
    String ip_address;
    String moduleUID;


    public Module(String title, String time, int detected_person, int detected_smoking, int color, String ip_address, String moduleUID) {
        this.title = title;
        this.time = time;
        this.detected_person = detected_person;
        this.detected_smoking = detected_smoking;
        this.color = color;
        this.ip_address = ip_address;
        this.moduleUID = moduleUID;
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

    public int getDetected_person() {
        return detected_person;
    }

    public void setDetected_person(int detected_person) {
        this.detected_person = detected_person;
    }

    public int getDetected_smoking() {
        return detected_smoking;
    }

    public void setDetected_smoking(int detected_smoking) {
        this.detected_smoking = detected_smoking;
    }
    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public String getModuleUID() {
        return moduleUID;
    }

    public void setModuleUID(String moduleUID) {
        this.moduleUID = moduleUID;
    }
}
