package com.rp.toyexchanger.ui.ui.Offers;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rp.toyexchanger.R;

public class OffersViewHolder {

    ImageView itemImage;
    TextView offerTitle;

    OffersViewHolder(View v) {
        itemImage = v.findViewById(R.id.offer_image_view);
        offerTitle = v.findViewById(R.id.title_text_view);
    }
}
