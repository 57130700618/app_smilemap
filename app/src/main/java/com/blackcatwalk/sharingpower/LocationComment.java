package com.blackcatwalk.sharingpower;

public class LocationComment {

    private String mComment;
    private String mUpdateDate;
    private String mUsers;
    private String mSex;
    private int mId;

    public void setmComment(String _comment) {
        this.mComment = _comment;
    }

    public void setmUpdateDate(String _updateDate) {
        this.mUpdateDate = _updateDate;
    }

    public void setmUsers(String _users) {
        this.mUsers = _users;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }

    public void setmSex(String _sex) {
        this.mSex = _sex;
    }

    public String getmComment() {
        return mComment;
    }

    public String getmUpdateDate() { return mUpdateDate; }

    public String getmUsers() { return mUsers; }

    public String getmSex() { return mSex; }

    public int getmId() {
        return mId;
    }

}
