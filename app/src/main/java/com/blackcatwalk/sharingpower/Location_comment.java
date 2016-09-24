package com.blackcatwalk.sharingpower;

public class Location_comment {

    private String comment;
    private String update_date;
    private String users;
    private String sex;

    public Location_comment(){}

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setUpdate_date(String update_date) {
        this.update_date = update_date;
    }

    public void setUsers(String users) {
        this.users = users;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getComment() {
        return comment;
    }

    public String getUpdate_date() { return update_date; }

    public String getUsers() { return users; }

    public String getSex() { return sex; }

}
