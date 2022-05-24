package com.rp.toyexchanger.data;

public class Counteroffer {

    public String id;
    public String title;
    public String description;
    public String imageId;
    public String userEmail;
    public String offerId;
    public String status;

    public Counteroffer() {

    }

    public Counteroffer(String id, String title, String description, String imageId, String userEmail, String offerId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.imageId = imageId;
        this.userEmail = userEmail;
        this.offerId = offerId;
    }
}
