package com.example.smokingapp;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *    모듈 정보 모델 클래스
 */

public class ModuleAccount {
    private String name = "";
    private String ip_address = "";
    private int day_detection_person = 0;         // 오늘 감지된 사람 수
    private int total_detection_person = 0;       // 전체 감지된 사람 수
    private String last_detection_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));       // 마지막 감지된 시간

    public ModuleAccount() { }

    public ModuleAccount(String name, String ip_address){
        this.name = name;
        this.ip_address = ip_address;
    }

    public int getDay_detection_person() {
        return day_detection_person;
    }

    public void setDay_detection_person(int day_detection_person) {
        this.day_detection_person = day_detection_person;
    }

    public void setTotal_detection_person(int total_detection_person) {
        this.total_detection_person = total_detection_person;
    }

    public void setLast_detection_time(String last_detection_time) {
        this.last_detection_time = last_detection_time;
    }

    public int getTotal_detection_person() {
        return total_detection_person;
    }

    public String getLast_detection_time() {
        return last_detection_time;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
