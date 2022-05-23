package com.rp.toyexchanger.data;

public class Offer {

    public String id;
    public String title;
    public String description;
    public String imageId;
    public String userEmail;
    public String counterOfferId;
    public String updatedOrCreated;

    public Offer() {

    }

    public Offer(String id, String title, String description, String imageId, String userEmail) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageId = imageId;
        this.userEmail = userEmail;
        updatedOrCreated = "Yes";
    }
}
