package com.serge.dating.mapenzi.Utils;

import java.io.Serializable;

public class Settings implements Serializable {

    private boolean showMen;
    private boolean showWoMen;

    private String distance;
    private Long minAge;
    private Long maxAge;

    private boolean showNotifications;
    private boolean showMessages;
    private boolean showLikedMessages;

    public Settings() {
    }

    public Settings(boolean showMen, boolean showWoMen, String distance, Long minAge, Long maxAge, boolean showNotifications, boolean showMessages, boolean showLikedMessages) {
        this.showMen = showMen;
        this.showWoMen = showWoMen;
        this.distance = distance;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.showNotifications = showNotifications;
        this.showMessages = showMessages;
        this.showLikedMessages = showLikedMessages;
    }

    public boolean isShowMen() {
        return showMen;
    }

    public void setShowMen(boolean showMen) {
        this.showMen = showMen;
    }

    public boolean isShowWoMen() {
        return showWoMen;
    }

    public void setShowWoMen(boolean showWoMen) {
        this.showWoMen = showWoMen;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Long getMinAge() {
        return minAge;
    }

    public void setMinAge(Long minAge) {
        this.minAge = minAge;
    }

    public Long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Long maxAge) {
        this.maxAge = maxAge;
    }

    public boolean isShowNotifications() {
        return showNotifications;
    }

    public void setShowNotifications(boolean showNotifications) {
        this.showNotifications = showNotifications;
    }

    public boolean isShowMessages() {
        return showMessages;
    }

    public void setShowMessages(boolean showMessages) {
        this.showMessages = showMessages;
    }

    public boolean isShowLikedMessages() {
        return showLikedMessages;
    }

    public void setShowLikedMessages(boolean showLikedMessages) {
        this.showLikedMessages = showLikedMessages;
    }

}
