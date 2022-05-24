package com.rp.toyexchanger.ui.ui.MyOffers;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import com.rp.toyexchanger.AddOfferActivity;
import com.rp.toyexchanger.R;
import com.rp.toyexchanger.data.Counteroffer;
import com.rp.toyexchanger.data.Offer;
import com.rp.toyexchanger.data.OfferWithImage;
import com.rp.toyexchanger.ui.ui.CounterOffer.MyCounterofferActivity;
import com.rp.toyexchanger.ui.ui.MainActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyOffersFragment extends Fragment {

    ListView listView;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseOfferStatusChangedListener;
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

        View view = inflater.inflate(R.layout.fragment_my_offers, container, false);
        listView = view.findViewById(R.id.my_offers_list_view);
        mDatabase = FirebaseDatabase.getInstance().getReference("Offers");
        mDatabaseOfferStatusChangedListener = FirebaseDatabase.getInstance().getReference("Counteroffers");
        storage = FirebaseStorage.getInstance();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        FloatingActionButton addOfferButton = view.findViewById(R.id.add_offer_button);
        addOfferButton.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AddOfferActivity.class));
        });

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int snapshotChildrenSize = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Offer offer = dataSnapshot.getValue(Offer.class);
                        if (offer.userEmail.equals(firebaseUser.getEmail()))
                            snapshotChildrenSize++;
                    }
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Offer offer = dataSnapshot.getValue(Offer.class);
                        if (offer.userEmail.equals(firebaseUser.getEmail())) {
                            StorageReference ref = storage.getReference().child("images/" + offer.imageId);
                            try {
                                final File localFile = File.createTempFile("Images", "jpeg");
                                int finalSnapshotChildrenSize = snapshotChildrenSize;
                                ref.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                        Bitmap offerImage = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                        offersWithImage.add(new OfferWithImage(offer.id, offer.title, offer.description, offer.imageId, offer.userEmail, offerImage));
                                        if (finalSnapshotChildrenSize == offersWithImage.size()) {
                                            MyOffersAdapter offersAdapter = new MyOffersAdapter(getActivity(), offersWithImage);
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
                if (snapshot.exists()) {
                    Offer offer = snapshot.getValue(Offer.class);
                    if (offer.updatedOrCreated.equals("No") && offer.userEmail.equals(mAuth.getCurrentUser().getEmail()) && offer.counterOfferId != null) {
                        Intent resultIntent = new Intent(getActivity(), MyOfferDetailsActivity.class);
                        resultIntent.putExtra("offer", offer.id);
                        int pendingFlags;
                        if (Build.VERSION.SDK_INT >= 23) {
                            pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
                        } else {
                            pendingFlags = PendingIntent.FLAG_UPDATE_CURRENT;
                        }
                        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 1, resultIntent, pendingFlags);

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), COUNTEROFFER_CHANNEL_ID);
                        builder.setContentTitle("New counteroffer!");
                        builder.setContentText("Someone wants to make a deal with you!");
                        builder.setSmallIcon(R.drawable.info_icon);
                        builder.setAutoCancel(true);
                        builder.setContentIntent(pendingIntent);

                        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getActivity());
                        managerCompat.notify(1, builder.build());
                    }
                }
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

        mDatabaseOfferStatusChangedListener.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.exists()) {
                    Counteroffer counteroffer = snapshot.getValue(Counteroffer.class);
                    if ((counteroffer.status.equals("Accepted") || counteroffer.status.equals("Declined")) && counteroffer.userEmail.equals(mAuth.getCurrentUser().getEmail())) {
                        Intent resultIntent = new Intent(getActivity(), MyCounterofferActivity.class);
                        resultIntent.putExtra("counterOfferId", counteroffer.id);
                        resultIntent.putExtra("offerId", counteroffer.offerId);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 1, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), COUNTEROFFER_CHANNEL_ID);
                        builder.setContentTitle("Counteroffer status changed!");
                        builder.setContentText("Someone answered to you counteroffer!");
                        builder.setSmallIcon(R.drawable.info_icon);
                        builder.setAutoCancel(true);
                        builder.setContentIntent(pendingIntent);

                        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getContext());
                        managerCompat.notify(1, builder.build());
                    }
                }
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