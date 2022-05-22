package com.rp.toyexchanger.ui.ui.Offers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.rp.toyexchanger.R;
import com.rp.toyexchanger.data.Offer;
import com.rp.toyexchanger.data.OfferWithImage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class OffersFragment extends Fragment {

    ListView listView;

    private DatabaseReference mDatabase;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;

    List<OfferWithImage> offersWithImage = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_offers, container, false);
        listView = view.findViewById(R.id.offers_list_view);
        mDatabase = FirebaseDatabase.getInstance().getReference("Offers");
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int snapshotChildrenSize = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Offer offer = dataSnapshot.getValue(Offer.class);
                        if (!offer.userEmail.equals(firebaseUser.getEmail()))
                            snapshotChildrenSize++;
                    }
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Offer offer = dataSnapshot.getValue(Offer.class);
                        if (!offer.userEmail.equals(firebaseUser.getEmail())) {
                            StorageReference ref = storage.getReference().child("images/" + offer.imageId);
                            try {
                                final File localFile = File.createTempFile("Images", "jpeg");
                                int finalSnapshotChildrenSize = snapshotChildrenSize;
                                ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        Bitmap offerImage = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                        offersWithImage.add(new OfferWithImage(offer.id, offer.title, offer.description, offer.imageId, offer.userEmail, offerImage, offer.counterOfferId));
                                        if (finalSnapshotChildrenSize == offersWithImage.size()) {
                                            OffersAdapter offersAdapter = new OffersAdapter(getActivity(), offersWithImage);
                                            listView.setAdapter(offersAdapter);
                                            offersAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }
}