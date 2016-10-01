package com.blackcatwalk.sharingpower;


public class CallEmergency {

    private float mRealdistance;
    private String mName;
    private String mTel;
    private String mDistance;

    public CallEmergency(String _name, String _tel) {
        this.mName = _name;
        this.mTel = _tel;
    }

    public CallEmergency(String _name, String _tel, String _distance , float _realdistance) {
        this.mName = _name;
        this.mTel = _tel;
        this.mDistance = _distance;
        this.mRealdistance = _realdistance;
    }

    public String getmName() {
        return mName;
    }

    public String getmTel() {
        return mTel;
    }

    public String getmDistance() {
        return mDistance;
    }

    public float getmRealdistance() {
        return mRealdistance;
    }
}
