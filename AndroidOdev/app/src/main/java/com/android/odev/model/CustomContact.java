package com.android.odev.model;

import android.graphics.Bitmap;

public class CustomContact {

    private String name;
    private String phoneNumber;
    private Bitmap profile;
    private String groupName;
    private boolean selected=false;

    public CustomContact() {
    }

    public CustomContact(String phoneNumber, String name, String groupName) {
        this.phoneNumber = phoneNumber;
        this.name = name;
        this.groupName = groupName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Bitmap getProfile() {
        return profile;
    }

    public void setProfile(Bitmap profile) {
        this.profile = profile;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
