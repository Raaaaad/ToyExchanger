package com.rp.toyexchanger.data;

public class Counteroffer {

    public String id;
    public String description;
    public String imageId;
    public String userEmail;
    public String offerId;

    public Counteroffer() {

    }

    public Counteroffer(String id, String description, String imageId, String userEmail, String offerId) {
        this.id = id;
        this.description = description;
        this.imageId = imageId;
        this.userEmail = userEmail;
        this.offerId = offerId;
    }
}
