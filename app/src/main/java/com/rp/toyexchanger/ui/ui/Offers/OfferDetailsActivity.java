package com.rp.toyexchanger.ui.ui.Offers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.rp.toyexchanger.R;
import com.rp.toyexchanger.data.Offer;
import com.rp.toyexchanger.data.OfferWithImage;
import com.rp.toyexchanger.ui.ui.CounterOffer.MakeOfferActivity;

public class OfferDetailsActivity extends AppCompatActivity {

    private ImageView imageView;

    private EditText titleEditText, descriptionEditText, offerAuthorEditText;

    OfferWithImage offerWithImage;

    FirebaseStorage storage;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_details);

        imageView = findViewById(R.id.offer_image);
        titleEditText = findViewById(R.id.offer_title);
        descriptionEditText = findViewById(R.id.offer_description);
        offerAuthorEditText = findViewById(R.id.offer_author);

        Gson gson = new Gson();
        offerWithImage = gson.fromJson(getIntent().getStringExtra("offer"), OfferWithImage.class);

        imageView.setImageBitmap(offerWithImage.image);
        titleEditText.setText(offerWithImage.title);
        descriptionEditText.setText(offerWithImage.description);
        offerAuthorEditText.setText(offerWithImage.userEmail);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        Button makeOfferButton = findViewById(R.id.make_offer_button);
        if (offerWithImage.counterOfferId != null) {
            makeOfferButton.setText("Offer already made");
            makeOfferButton.setEnabled(false);
        }
        makeOfferButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MakeOfferActivity.class);
            Offer offer = new Offer(offerWithImage.id, offerWithImage.title, offerWithImage.description, offerWithImage.imageId, offerWithImage.userEmail);
            String json = gson.toJson(offer);
            intent.putExtra("offer", json);
            startActivity(intent);
        });

    }


}