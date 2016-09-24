package com.blackcatwalk.sharingpower;


public class Favorite  {

    private String type;
    private String time;
    private String detail;
    private Double lat;
    private Double lng;

    public void setType(String type) {
        this.type = type;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getType() {
        return type;
    }

    public String getTime() {
        return time;
    }

    public String getDetail() {
        return detail;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

}
