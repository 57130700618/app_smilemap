package com.blackcatwalk.sharingpower;

import java.text.NumberFormat;

public class Rank {

    private String mSequeneNumber;
    private String mName;
    private String mPoint;
    private String mSumHour;
    private String mSex;
    private String mNickName;
    private String mStaus;
    //private String picture;
    private NumberFormat mTemp = NumberFormat.getInstance();
    private double mTempDouble;

    public Rank(){}

    public void setmSequeneNumber(String _sequeneNumber) {
        this.mSequeneNumber = _sequeneNumber;
    }

    public void setmName(String _name) {
        this.mName = _name;
    }

    /*public void setPicture(String picture) {
        this.picture = picture;
    }*/

    public void setmSex(String _sex) {
        this.mSex = _sex;
    }

    public void setmNickName(String _nickName) {
        this.mNickName = _nickName;
    }

    public void setmStaus(String _staus) {
        this.mStaus = _staus;
    }

    public void setmPoint(int _point) {
        if(_point > 0){
            mTempDouble = (double) _point;
            this.mPoint = mTemp.format(mTempDouble);
        }else{
            this.mPoint = "-";
        }
    }

    public void setmSumHour(int _sumHour) {
        if(_sumHour > 0){
            mTempDouble = (double) _sumHour;
            this.mSumHour = mTemp.format(mTempDouble);
        }else{
            this.mSumHour = "-";
        }
    }

    public String getmSequeneNumber() { return mSequeneNumber; }

    public String getmName() { return mName; }

    //public String getPicture() { return picture; }

    public String getmPoint() { return mPoint; }

    public String getmSumHour() { return mSumHour; }

    public String getmSex() { return mSex; }

    public String getmNickName() { return mNickName; }

    public String getmStaus() { return mStaus; }
}
