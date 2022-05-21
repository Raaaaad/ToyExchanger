package com.rp.toyexchanger.data;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class OfferWithImage {
    public String title;
    public String description;
    public String imageId;
    public String userEmail;
    public Bitmap image;

    public OfferWithImage() {

    }

    public OfferWithImage(String title, String description, String imageId, String userEmail, Bitmap image) {
        this.title = title;
        this.description = description;
        this.imageId = imageId;
        this.userEmail = userEmail;
        this.image = image;
    }
}
