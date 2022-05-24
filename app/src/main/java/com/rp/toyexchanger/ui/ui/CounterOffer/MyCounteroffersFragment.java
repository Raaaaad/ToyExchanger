package com.rp.toyexchanger.ui.ui.CounterOffer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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
import com.rp.toyexchanger.data.OfferWithImage;
import com.rp.toyexchanger.ui.ui.MyOffers.MyOfferDetailsActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyCounteroffersFragment extends Fragment {

    ListView listView;

    private DatabaseReference mDatabase;
    private FirebaseStorage storage;
    private FirebaseAuth mAuth;

    private String COUNTEROFFER_CHANNEL_ID = "Counteroffer notification";

    List<OfferWithImage> offersWithImage = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(COUNTEROFFER_CHANNEL_ID, COUNTEROFFER_CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getActivity().getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        View view = inflater.inflate(R.layout.fragment_my_counteroffers, container, false);
        listView = view.findViewById(R.id.my_counteroffers_list_view);
        mDatabase = FirebaseDatabase.getInstance().getReference("Counteroffers");
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    offersWithImage = new ArrayList<>();
                    int snapshotChildrenSize = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Counteroffer counteroffer = dataSnapshot.getValue(Counteroffer.class);
                        if (counteroffer.userEmail.equals(firebaseUser.getEmail()))
                            snapshotChildrenSize++;
                    }
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Counteroffer counteroffer = dataSnapshot.getValue(Counteroffer.class);
                        if (counteroffer.userEmail.equals(firebaseUser.getEmail())) {
                            StorageReference ref = storage.getReference().child("images/" + counteroffer.imageId);
                            try {
                                final File localFile = File.createTempFile("Images", "jpeg");
                                int finalSnapshotChildrenSize = snapshotChildrenSize;
                                ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        Bitmap offerImage = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                        OfferWithImage offerWithImage = new OfferWithImage(counteroffer.id, counteroffer.title, counteroffer.description, counteroffer.imageId, counteroffer.userEmail, offerImage);
                                        offerWithImage.counterOfferId = counteroffer.offerId;
                                        offersWithImage.add(offerWithImage);
                                        if (finalSnapshotChildrenSize == offersWithImage.size()) {
                                            MyCounteroffersAdapter offersAdapter = new MyCounteroffersAdapter(getActivity(), offersWithImage);
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

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }

}