package com.example.sdsd;

import android.widget.EditText;

public class MemberInfo {
    private String name;
    private String phoneNum;
    private String birth;
    private String address;
    private int point;

    public MemberInfo(String name, String phoneNum, String birth, String address, int point) {
        this.name = name;
        this.phoneNum = phoneNum;
        this.birth = birth;
        this.address = address;
        this.point = point;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPoint(int point) { this.point = point; }

    public String getName() {
        return name;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public String getBirth() {
        return birth;
    }

    public String getAddress() {
        return address;
    }

    public int getPoint() { return point; }
}
