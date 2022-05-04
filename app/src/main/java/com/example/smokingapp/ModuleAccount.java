package com.example.smokingapp;

/**
 *    모듈 정보 모델 클래스
 */

public class ModuleAccount {

    private int day_detection_person = 0;         // 오늘 감지된 사람 수
    private int total_detection_person = 0;       // 전체 감지된 사람 수
    private String last_detection_time = "2022.04.03 23:12:56";       // 마지막 감지된 시간

    public ModuleAccount() { }

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
}
