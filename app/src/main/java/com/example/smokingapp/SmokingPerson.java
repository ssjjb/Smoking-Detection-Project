package com.example.smokingapp;

/**
 *    감지된 흡연자 클래스
 */

public class SmokingPerson {

    private String detection_name = "";              // 감지 이름
    private String detection_time = "";              // 감지 시간
    private String img_url = "";                     // 감지 사진 담는 주소

    public SmokingPerson(){  }

    public String getDetection_name() {
        return detection_name;
    }

    public void setDetection_name(String detection_name) {
        this.detection_name = detection_name;
    }

    public String getDetection_time() {
        return detection_time;
    }

    public void setDetection_time(String detection_time) {
        this.detection_time = detection_time;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

}
