package com.blackcatwalk.sharingpower;


public class Favorite  {

    private String mType;
    private String mTime;
    private String mDetail;
    private Double mLat;
    private Double mLng;

    public void setType(String _type) {
        this.mType = _type;
    }

    public void setTime(String _time) {
        this.mTime = _time;
    }

    public void setDetail(String _detail) {
        this.mDetail = _detail;
    }

    public void setLat(double _lat) {
        this.mLat = _lat;
    }

    public void setLng(double _lng) {
        this.mLng = _lng;
    }

    public String getType() {
        return mType;
    }

    public String getTime() {
        return mTime;
    }

    public String getDetail() {
        return mDetail;
    }

    public Double getLat() {
        return mLat;
    }

    public Double getLng() {
        return mLng;
    }

}
