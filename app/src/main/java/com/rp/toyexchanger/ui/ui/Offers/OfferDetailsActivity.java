package com.rp.toyexchanger.ui.ui.Offers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.rp.toyexchanger.R;
import com.rp.toyexchanger.data.Offer;
import com.rp.toyexchanger.data.OfferWithImage;
import com.rp.toyexchanger.ui.ui.CounterOffer.MakeOfferActivity;
import com.rp.toyexchanger.ui.ui.MyOffers.MyOfferDetailsActivity;

import java.io.File;
import java.io.IOException;

public class OfferDetailsActivity extends AppCompatActivity {

    private ImageView imageView;

    private EditText titleEditText, descriptionEditText;

    Offer offer;
    String offerId = "";

    FirebaseStorage storage;
    StorageReference storageReference;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_details);

        imageView = findViewById(R.id.offer_image);
        titleEditText = findViewById(R.id.offer_title);
        descriptionEditText = findViewById(R.id.offer_description);

        offerId = getIntent().getStringExtra("offer");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        Button makeOfferButton = findViewById(R.id.make_offer_button);

        mDatabase = FirebaseDatabase.getInstance().getReference("Offers").child(offerId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    offer = snapshot.getValue(Offer.class);
                    descriptionEditText.setText(offer.description);
                    titleEditText.setText(offer.title);
                    if (offer.counterOfferId != null) {
                        makeOfferButton.setText("Offer has been already made");
                        makeOfferButton.setEnabled(false);
                    }
                    makeOfferButton.setOnClickListener(v -> {
                        Intent intent = new Intent(OfferDetailsActivity.this, MakeOfferActivity.class);
                        Gson gson = new Gson();
                        String json = gson.toJson(offer);
                        intent.putExtra("offer", json);
                        startActivity(intent);
                    });
                    StorageReference ref = storage.getReference().child("images/" + offer.imageId);
                    try {
                        final File localFile = File.createTempFile("Images", "jpeg");
                        ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                Bitmap offerImage = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                imageView.setImageBitmap(offerImage);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(OfferDetailsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}