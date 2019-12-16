package com.serge.dating.mapenzi.Utils;


/*
NotificationType:
 1) Friend Request



 */


public class NotificationModel {


    private String NotificationMessage;
    private int NotificationType;
    private String EmailFrom;
    private String FirstName;
    private String LastName;
    private String FriendRequestFireBaseKey;
    private String seen;

    public NotificationModel(String notificationMessage, int notificationType, String emailFrom, String firstName, String lastName, String friendRequestFireBaseKey, String seen) {
        NotificationMessage = notificationMessage;
        NotificationType = notificationType;
        EmailFrom = emailFrom;
        FirstName = firstName;
        LastName = lastName;
        FriendRequestFireBaseKey = friendRequestFireBaseKey;
        this.seen = seen;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public NotificationModel() {
    }

    public String getNotificationMessage() {
        return NotificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        NotificationMessage = notificationMessage;
    }

    public int getNotificationType() {
        return NotificationType;
    }

    public void setNotificationType(int notificationType) {
        NotificationType = notificationType;
    }

    public String getEmailFrom() {
        return EmailFrom;
    }

    public void setEmailFrom(String emailFrom) {
        EmailFrom = emailFrom;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getFriendRequestFireBaseKey() {
        return FriendRequestFireBaseKey;
    }

    public void setFriendRequestFireBaseKey(String friendRequestFireBaseKey) {
        FriendRequestFireBaseKey = friendRequestFireBaseKey;
    }
}
