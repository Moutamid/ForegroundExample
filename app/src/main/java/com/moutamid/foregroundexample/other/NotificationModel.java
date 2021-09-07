package com.moutamid.foregroundexample.other;

public class NotificationModel {
    private String uid, name, message, profileUrl, pushKey;

    public NotificationModel(String uid, String name, String message, String profileUrl, String pushKey) {
        this.uid = uid;
        this.name = name;
        this.message = message;
        this.profileUrl = profileUrl;
        this.pushKey = pushKey;
    }

    public String getPushKey() {
        return pushKey;
    }

    public void setPushKey(String pushKey) {
        this.pushKey = pushKey;
    }

    public NotificationModel() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
