package com.example.chatting.Model;

public class Userdata {
    private String ID, USERNAME, IMAGEURL, STATUS, SEARCH;

    public Userdata(String ID, String USERNAME, String IMAGEURL, String STATUS, String SEARCH) {
        this.ID = ID;
        this.USERNAME = USERNAME;
        this.IMAGEURL = IMAGEURL;
        this.STATUS = STATUS;
        this.SEARCH = SEARCH;
    }

    public Userdata() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public String getIMAGEURL() {
        return IMAGEURL;
    }

    public void setIMAGEURL(String IMAGEURL) {
        this.IMAGEURL = IMAGEURL;
    }

    public String getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public String getSEARCH() {
        return SEARCH;
    }

    public void setSEARCH(String SEARCH) {
        this.SEARCH = SEARCH;
    }
}
