package com.rp.toyexchanger.ui.ui.CounterOffer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
import com.rp.toyexchanger.R;
import com.rp.toyexchanger.data.Counteroffer;

import java.io.File;
import java.io.IOException;

public class CounterofferDetailsActivity extends AppCompatActivity {

    private ImageView imageView;

    private EditText descriptionEditText, offerAuthorEditText;


    FirebaseStorage storage;
    StorageReference storageReference;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counteroffer_details);

        imageView = findViewById(R.id.offer_image);
        descriptionEditText = findViewById(R.id.offer_description);
        offerAuthorEditText = findViewById(R.id.offer_author);

        String counterOfferId = getIntent().getStringExtra("counterOfferId");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference("Counteroffers").child(counterOfferId);
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Counteroffer counteroffer = snapshot.getValue(Counteroffer.class);
                    descriptionEditText.setText(counteroffer.description);
                    offerAuthorEditText.setText(counteroffer.userEmail);
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

        Button acceptOfferButton = findViewById(R.id.accept_offer_button);
        acceptOfferButton.setOnClickListener(v -> {

        });

        Button declineOfferButton = findViewById(R.id.decline_offer_button);
        declineOfferButton.setOnClickListener(v -> {

        });

    }
}