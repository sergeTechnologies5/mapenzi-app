package com.serge.dating.mapenzi.Utils;

public class Friends {

    public String user_id;
    public String username;
    public Long onlineStatus;
    public String profileImageUrl;
    public String receiver;

    public Friends(){

    }

    public Friends(String user_id, String username, Long onlineStatus, String profileImageUrl,String receiver) {
        this.user_id = user_id;
        this.username = username;
        this.onlineStatus = onlineStatus;
        this.profileImageUrl = profileImageUrl;
        this.receiver = receiver;

    }
}
