package com.rp.toyexchanger.ui.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rp.toyexchanger.AddOfferActivity;
import com.rp.toyexchanger.R;

public class MyOffersFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_my_offers, container, false);
        FloatingActionButton addOfferButton = view.findViewById(R.id.add_offer_button);
        addOfferButton.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), AddOfferActivity.class));
        });
        return view;
    }
}