package com.rp.toyexchanger.ui.ui.CounterOffer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.rp.toyexchanger.R;
import com.rp.toyexchanger.data.Counteroffer;
import com.rp.toyexchanger.data.Offer;

import java.io.File;
import java.io.IOException;

public class MyCounterofferActivity extends AppCompatActivity {

    private ImageView imageView;

    private EditText descriptionEditText, offerTitleEditText, offerAuthorEditText;

    private TextView offerStatusTextView, offerAuthorTextView;


    FirebaseStorage storage;
    StorageReference storageReference;
    private DatabaseReference mDatabase, offerDatabaseReference;

    private Offer offer;
    private Counteroffer counteroffer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_counteroffer);

        imageView = findViewById(R.id.offer_image);
        descriptionEditText = findViewById(R.id.offer_description);
        offerTitleEditText = findViewById(R.id.offer_title);
        offerStatusTextView = findViewById(R.id.offer_status_text_view);
        offerAuthorTextView = findViewById(R.id.offer_author_text_view);
        offerAuthorEditText = findViewById(R.id.offer_author_edit_text);

        String counterOfferId = getIntent().getStringExtra("counterOfferId");
        String offerId = getIntent().getStringExtra("offerId");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference("Counteroffers").child(counterOfferId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Counteroffer counteroffer = snapshot.getValue(Counteroffer.class);
                    descriptionEditText.setText(counteroffer.description);
                    offerTitleEditText.setText(counteroffer.title);
                    offerStatusTextView.setText("Offer status: " + counteroffer.status);
                    if (counteroffer.status.equals("Waiting")) {
                        offerStatusTextView.setTextColor(Color.MAGENTA);
                    } else if (counteroffer.status.equals("Accepted")) {
                        offerStatusTextView.setTextColor(Color.GREEN);
                        offerAuthorTextView.setVisibility(View.VISIBLE);
                        offerAuthorEditText.setVisibility(View.VISIBLE);
                    } else {
                        offerStatusTextView.setTextColor(Color.RED);
                    }
                    offerStatusTextView.setVisibility(View.VISIBLE);
                    if (counteroffer.imageId != null && !counteroffer.imageId.equals("Empty image")) {
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
                                    Toast.makeText(MyCounterofferActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

        offerDatabaseReference = FirebaseDatabase.getInstance().getReference("Offers").child(offerId);
        offerDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Offer offer = snapshot.getValue(Offer.class);
                    offerAuthorEditText.setText(offer.userEmail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}