package com.rp.toyexchanger.ui.ui.CounterOffer;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.rp.toyexchanger.R;

public class MyCounteroffersViewHolder {

    ImageView itemImage;
    TextView offerTitle;

    MyCounteroffersViewHolder(View v) {
        itemImage = v.findViewById(R.id.offer_image_view);
        offerTitle = v.findViewById(R.id.title_text_view);
    }
}
