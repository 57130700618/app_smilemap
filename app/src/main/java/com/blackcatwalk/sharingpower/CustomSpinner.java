package com.blackcatwalk.sharingpower;


public class CustomSpinner {

    private int iconImage;
    private String name;

    public CustomSpinner(int iconImageId, String nameId) {
        iconImage = iconImageId;
        name = nameId;
    }

    public void setIconImage(int iconImageId) {
        iconImage = iconImageId;
    }

    public void setName(String nameId) {
        name = nameId;
    }

    public int getImageId() {
        return iconImage;
    }

    public String getName() {
        return name;
    }
}
