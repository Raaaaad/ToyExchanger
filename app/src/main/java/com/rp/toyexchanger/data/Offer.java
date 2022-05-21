package com.rp.toyexchanger.data;

public class Offer {

    public String title;
    public String description;
    public String imageId;
    public String userEmail;

    public Offer() {

    }

    public Offer(String title, String description, String imageId, String userEmail) {
        this.title = title;
        this.description = description;
        this.imageId = imageId;
        this.userEmail = userEmail;
    }
}
