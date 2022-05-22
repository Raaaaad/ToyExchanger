package com.rp.toyexchanger.ui.ui.Offers;

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
import com.rp.toyexchanger.data.Offer;
import com.rp.toyexchanger.data.OfferWithImage;
import com.rp.toyexchanger.ui.ui.MainActivity;
import com.rp.toyexchanger.ui.ui.MyOffers.MyOfferDetailsActivity;

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

import java.io.ByteArrayOutputStream;
import java.util.UUID;

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

        storage =  FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        Button makeOfferButton = findViewById(R.id.make_offer_button);

    }



}