package com.serge.dating.mapenzi.Utils;

import java.io.Serializable;

public class Images implements Serializable {

    private String imageUrl;

    public Images() {
    }

    public Images(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
