package com.rp.toyexchanger.ui.ui.MyOffers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.rp.toyexchanger.data.Offer;
import com.rp.toyexchanger.data.OfferWithImage;
import com.rp.toyexchanger.ui.ui.MainActivity;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class MyOfferDetailsActivity extends AppCompatActivity {

    private int CAMERA_PERMISION_CODE = 1;
    private int CAMERA = 2;

    private ImageView imageView;

    private EditText titleEditText, descriptionEditText;

    OfferWithImage offerWithImage;

    private Uri imagePath;
    private Bitmap cameraImage;

    FirebaseStorage storage;
    StorageReference storageReference;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_offer_details);
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
                        new String[] {Manifest.permission.CAMERA},
                        CAMERA_PERMISION_CODE
                );
            }
        });

        imageView = findViewById(R.id.offer_image);
        titleEditText = findViewById(R.id.offer_title);
        descriptionEditText = findViewById(R.id.offer_description);

        Gson gson = new Gson();
        offerWithImage = gson.fromJson(getIntent().getStringExtra("offer"), OfferWithImage.class);

        imageView.setImageBitmap(offerWithImage.image);
        cameraImage = offerWithImage.image;
        titleEditText.setText(offerWithImage.title);
        descriptionEditText.setText(offerWithImage.description);

        storage =  FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuth = FirebaseAuth.getInstance();

        Button submitChangesButton = findViewById(R.id.submit_changes_button);
        submitChangesButton.setOnClickListener(v -> {
            addOffer();
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISION_CODE) {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA);
            } else {
                Toast.makeText(
                        this,
                        "No permission granted",
                        Toast.LENGTH_LONG
                );
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == Activity.RESULT_OK)
            if(requestCode == CAMERA) {
                cameraImage = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(cameraImage);
                imagePath = getImageUri(getApplicationContext(), cameraImage);
            }
    }

    private void addOffer() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        boolean error = false;

        if (cameraImage == null) {
            Toast.makeText(
                    this,
                    "No image selected.",
                    Toast.LENGTH_LONG
            );
            error = true;
        }

            if (title.isEmpty()) {
                titleEditText.setError("Title is required");
                titleEditText.requestFocus();
                error = true;
            }

            if (description.isEmpty()) {
                descriptionEditText.setError("Password is required");
                descriptionEditText.requestFocus();
                error = true;
            }

        if (error)
            return;

        uploadImage(title, description);

    }

    private void uploadImage(String title, String descrpition)
    {
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
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    String offerId = offerWithImage.id;
                                    Offer offer =  new Offer(offerId, title, descrpition, uuId, firebaseUser.getEmail());
                                    FirebaseDatabase.getInstance().getReference("Offers")
                                            .child(offerId)
                                            .setValue(offer).addOnCompleteListener(task -> {
                                                if(task.isSuccessful()) {
                                                    Toast.makeText(MyOfferDetailsActivity.this, "Offer modified!", Toast.LENGTH_LONG).show();
                                                    startActivity(new Intent(MyOfferDetailsActivity.this, MainActivity.class));
                                                } else {
                                                    Toast.makeText(MyOfferDetailsActivity.this, "Something went wrong.", Toast.LENGTH_LONG).show();
                                                }
                                            });
                                    progressDialog.dismiss();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {

                            progressDialog.dismiss();
                            Toast
                                    .makeText(MyOfferDetailsActivity.this,
                                            "Failed " + e.getMessage(),
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    })
                    .addOnProgressListener(
                            new OnProgressListener<UploadTask.TaskSnapshot>() {

                                @Override
                                public void onProgress(
                                        UploadTask.TaskSnapshot taskSnapshot)
                                {
                                    double progress
                                            = (100.0
                                            * taskSnapshot.getBytesTransferred()
                                            / taskSnapshot.getTotalByteCount());
                                    progressDialog.setMessage(
                                            "Uploaded "
                                                    + (int)progress + "%");
                                }
                            });
        } else {
            FirebaseUser firebaseUser = mAuth.getCurrentUser();
            String offerId = offerWithImage.id;
            Offer offer =  new Offer(offerId, title, descrpition, offerWithImage.imageId, firebaseUser.getEmail());
            FirebaseDatabase.getInstance().getReference("Offers")
                    .child(offerId)
                    .setValue(offer).addOnCompleteListener(task -> {
                        if(task.isSuccessful()) {
                            Toast.makeText(MyOfferDetailsActivity.this, "Offer modified!", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(MyOfferDetailsActivity.this, MainActivity.class));
                        } else {
                            Toast.makeText(MyOfferDetailsActivity.this, "Something went wrong.", Toast.LENGTH_LONG).show();
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