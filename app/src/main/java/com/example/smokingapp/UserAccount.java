package com.example.smokingapp;

/**
*    사용자 계정 정보 모델 클래스
*/

public class UserAccount {

    private String emailId;             // 이메일 아이디
    private String name;                // 닉네임
    private String idToken;             // Firebase Uid (고유 토큰정보)

    public UserAccount() { }   // firebase는 빈 생성자라도 무조건 만들어줘야 함

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getIdToken() {
        return idToken;
    }
}
