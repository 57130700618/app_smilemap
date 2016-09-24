package com.blackcatwalk.sharingpower;

import java.text.NumberFormat;

public class Rank {

    private String sequeneNumber;
    private String name;
    private String point;
    private String sumHour;
    private String sex;
    private String nickName;
    private String staus;
    //private String picture;
    private NumberFormat temp = NumberFormat.getInstance();
    private double tempDouble;

    public Rank(){}

    public void setSequeneNumber(String sequeneNumber) {
        this.sequeneNumber = sequeneNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*public void setPicture(String picture) {
        this.picture = picture;
    }*/

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setStaus(String staus) {
        this.staus = staus;
    }

    public void setPoint(int point) {
        if(point > 0){
            tempDouble = (double) point;
            this.point = temp.format(tempDouble);
        }else{
            this.point = "-";
        }
    }

    public void setSumHour(int sumHour) {
        if(sumHour > 0){
            tempDouble = (double) sumHour;
            this.sumHour  = temp.format(tempDouble);
        }else{
            this.sumHour = "-";
        }
    }

    public String getSequeneNumber() { return sequeneNumber; }

    public String getName() { return name; }

    //public String getPicture() { return picture; }

    public String getPoint() { return point; }

    public String getSumHour() { return sumHour; }

    public String getSex() { return sex; }

    public String getNickName() { return nickName; }

    public String getStaus() { return staus; }
}
