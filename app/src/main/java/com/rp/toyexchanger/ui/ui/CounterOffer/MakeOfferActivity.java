package com.rp.toyexchanger.ui.ui.CounterOffer;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.rp.toyexchanger.R;
import com.rp.toyexchanger.data.Counteroffer;
import com.rp.toyexchanger.data.Offer;
import com.rp.toyexchanger.ui.ui.MainActivity;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class MakeOfferActivity extends AppCompatActivity {

    private int CAMERA_PERMISION_CODE = 1;
    private int CAMERA = 2;

    private ImageView imageView;

    private EditText titleEditText, descriptionEditText;

    Offer offer;

    private Uri imagePath;
    private Bitmap cameraImage;

    FirebaseStorage storage;
    StorageReference storageReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_offer);

        Gson gson = new Gson();
        offer = gson.fromJson(getIntent().getStringExtra("offer"), Offer.class);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        descriptionEditText = findViewById(R.id.offer_description);
        titleEditText = findViewById(R.id.offer_title);
        imageView = findViewById(R.id.offer_image);
        Button cameraButton = findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA);
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_PERMISION_CODE
                );
            }
        });

        Button makeOffer = findViewById(R.id.make_offer_button);
        makeOffer.setOnClickListener(v -> {
            makeOffer();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK)
            if (requestCode == CAMERA) {
                cameraImage = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(cameraImage);
                imagePath = getImageUri(getApplicationContext(), cameraImage);
            }
    }

    private void makeOffer() {
        String description = descriptionEditText.getText().toString().trim();
        String title = titleEditText.getText().toString().trim();

        boolean error = false;

        if (title.isEmpty()) {
            titleEditText.setError("Title is required");
            titleEditText.requestFocus();
            error = true;
        }

        if (description.isEmpty()) {
            descriptionEditText.setError("Description is required");
            descriptionEditText.requestFocus();
            error = true;
        }

        if (error)
            return;

        uploadOffer(description, title);

    }

    private void uploadOffer(String description, String title) {
        if (imagePath != null) {

            ProgressDialog progressDialog
                    = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            String uuId = UUID.randomUUID().toString();
            StorageReference ref
                    = storageReference
                    .child(
                            "images/"
                                    + uuId);

            ref.putFile(imagePath)
                    .addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    String offerId = UUID.randomUUID().toString();
                                    Counteroffer counteroffer = new Counteroffer(offerId, title, description, uuId, firebaseUser.getEmail(), offer.id);
                                    counteroffer.status = "Waiting";
                                    FirebaseDatabase.getInstance().getReference("Counteroffers")
                                            .child(offerId)
                                            .setValue(counteroffer).addOnCompleteListener(task -> {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(MakeOfferActivity.this, "Counteroffer created!", Toast.LENGTH_LONG).show();
                                                    offer.counterOfferId = offerId;
                                                    offer.updatedOrCreated = "No";
                                                    FirebaseDatabase.getInstance().getReference("Offers").child(offer.id).setValue(offer);
                                                    startActivity(new Intent(MakeOfferActivity.this, MainActivity.class));
                                                } else {
                                                    Toast.makeText(MakeOfferActivity.this, "Something went wrong.", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                    progressDialog.dismiss();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            progressDialog.dismiss();
                            Toast
                                    .makeText(MakeOfferActivity.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int) progress + "%");
                                }
                            });
        } else {
            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            String offerId = UUID.randomUUID().toString();
            Counteroffer counteroffer = new Counteroffer(offerId, title, description, "Empty image", firebaseUser.getEmail(), offer.id);
            counteroffer.status = "Waiting";
            FirebaseDatabase.getInstance().getReference("Counteroffers")
                    .child(offerId)
                    .setValue(counteroffer).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(MakeOfferActivity.this, "Counteroffer created!", Toast.LENGTH_LONG).show();
                            offer.counterOfferId = offerId;
                            offer.updatedOrCreated = "No";
                            FirebaseDatabase.getInstance().getReference("Offers").child(offer.id).setValue(offer);
                            startActivity(new Intent(MakeOfferActivity.this, MainActivity.class));
                        } else {
                            Toast.makeText(MakeOfferActivity.this, "Something went wrong.", Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


}