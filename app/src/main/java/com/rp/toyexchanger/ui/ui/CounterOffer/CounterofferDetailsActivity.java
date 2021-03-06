package com.rp.toyexchanger.ui.ui.CounterOffer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.rp.toyexchanger.AddOfferActivity;
import com.rp.toyexchanger.R;
import com.rp.toyexchanger.data.Counteroffer;
import com.rp.toyexchanger.data.Offer;
import com.rp.toyexchanger.ui.ui.MainActivity;

import java.io.File;
import java.io.IOException;

public class CounterofferDetailsActivity extends AppCompatActivity {

    private ImageView imageView;

    private EditText descriptionEditText, offerTitleEditText, authorEditText;

    private TextView offerStatusTextView, authorTextView;

    Counteroffer counteroffer;
    Offer offer;


    FirebaseStorage storage;
    StorageReference storageReference;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counteroffer_details);

        imageView = findViewById(R.id.offer_image);
        descriptionEditText = findViewById(R.id.offer_description);
        offerTitleEditText = findViewById(R.id.offer_title);
        authorEditText = findViewById(R.id.offer_author_edit_text);
        authorTextView = findViewById(R.id.offer_author_text_view);
        offerStatusTextView = findViewById(R.id.offer_status_text_view);

        Gson gson = new Gson();
        offer = gson.fromJson(getIntent().getStringExtra("offer"), Offer.class);

        String counterOfferId = getIntent().getStringExtra("counterOfferId");

        Button declineOfferButton = findViewById(R.id.decline_offer_button);
        Button acceptOfferButton = findViewById(R.id.accept_offer_button);

        acceptOfferButton.setOnClickListener(v -> {
            counteroffer.status = "Accepted";
            FirebaseDatabase.getInstance().getReference("Counteroffers").child(counterOfferId).setValue(counteroffer);
            authorEditText.setVisibility(View.VISIBLE);
            offerStatusTextView.setText("Offer has been accepted");
            authorTextView.setVisibility(View.VISIBLE);
            offerStatusTextView.setVisibility(View.VISIBLE);
            acceptOfferButton.setVisibility(View.GONE);
            declineOfferButton.setVisibility(View.GONE);
        });

        declineOfferButton.setOnClickListener(v -> {
            counteroffer.status = "Declined";
            FirebaseDatabase.getInstance().getReference("Counteroffers").child(counterOfferId).setValue(counteroffer);
            offer.counterOfferId = null;
            FirebaseDatabase.getInstance().getReference("Offers").child(offer.id).setValue(offer);
            offerStatusTextView.setText("Offer has been declined");
            offerStatusTextView.setVisibility(View.VISIBLE);
            acceptOfferButton.setVisibility(View.GONE);
            declineOfferButton.setVisibility(View.GONE);
            startActivity(new Intent(CounterofferDetailsActivity.this, MainActivity.class));
        });

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference("Counteroffers").child(counterOfferId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    counteroffer = snapshot.getValue(Counteroffer.class);
                    descriptionEditText.setText(counteroffer.description);
                    offerTitleEditText.setText(counteroffer.title);
                    authorEditText.setText(counteroffer.userEmail);
                    if (counteroffer.status.equals("Accepted")) {
                        offerStatusTextView.setText("Offer has been accepted");
                        offerStatusTextView.setTextColor(Color.GREEN);
                        authorEditText.setVisibility(View.VISIBLE);
                        offerStatusTextView.setVisibility(View.VISIBLE);
                        authorTextView.setVisibility(View.VISIBLE);
                        acceptOfferButton.setVisibility(View.GONE);
                        declineOfferButton.setVisibility(View.GONE);
                    } else if (counteroffer.status.equals("Declined")) {
                        offerStatusTextView.setText("Offer has been declined");
                        offerStatusTextView.setVisibility(View.VISIBLE);
                        declineOfferButton.setVisibility(View.GONE);
                        acceptOfferButton.setVisibility(View.GONE);
                    }
                    if (!counteroffer.imageId.equals("Empty image")) {
                        StorageReference ref = storage.getReference().child("images/" + counteroffer.imageId);
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
                                    Toast.makeText(CounterofferDetailsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}