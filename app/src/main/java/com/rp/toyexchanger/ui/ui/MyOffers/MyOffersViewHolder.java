package com.rp.toyexchanger.ui.ui.MyOffers;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rp.toyexchanger.R;

public class MyOffersViewHolder {

    ImageView itemImage;
    TextView offerTitle;

    MyOffersViewHolder(View v) {
        itemImage = v.findViewById(R.id.offer_image_view);
        offerTitle = v.findViewById(R.id.title_text_view);
    }
}
