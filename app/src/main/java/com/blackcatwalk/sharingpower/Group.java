package com.blackcatwalk.sharingpower;

public class Group {
    private String name;
    private String detail;
    private String thumbnail;
    private String creator;
    private String date;
    private int numOfGroup;

    public Group() {
    }

    public Group(String name, String detail, String thumbnail, String Creator, int numOfGroup, String date) {
        this.name = name;
        this.detail = detail;
        this.thumbnail = thumbnail;
        this.creator = Creator;
        this.numOfGroup = numOfGroup;
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void numOfGroup(int numOfGroup) {
        this.numOfGroup = numOfGroup;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getName() {
        return name;
    }

    public String getDetail() {
        return detail;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getCreator() {
        return creator;
    }

    public int getNumOfSongs() {
        return numOfGroup;
    }

    public String getDate() {
        return date;
    }
}