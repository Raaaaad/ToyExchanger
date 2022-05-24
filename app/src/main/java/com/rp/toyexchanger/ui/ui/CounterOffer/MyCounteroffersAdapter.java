package com.rp.toyexchanger.ui.ui.CounterOffer;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.rp.toyexchanger.R;
import com.rp.toyexchanger.data.OfferWithImage;
import com.rp.toyexchanger.ui.ui.MyOffers.MyOfferDetailsActivity;

import java.util.List;

public class MyCounteroffersAdapter extends ArrayAdapter<OfferWithImage> {
    Context context;

    List<OfferWithImage> offersWithImage;

    public MyCounteroffersAdapter(Context context, List<OfferWithImage> offersWithImage) {
        super(context, R.layout.single_offer, R.id.title_text_view, offersWithImage);
        this.context = context;
        this.offersWithImage = offersWithImage;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View singleItem = convertView;
        MyCounteroffersViewHolder holder = null;
        if (singleItem == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            singleItem = layoutInflater.inflate(R.layout.single_offer, parent, false);
            holder = new MyCounteroffersViewHolder(singleItem);
            singleItem.setTag(holder);
        } else {
            holder = (MyCounteroffersViewHolder) singleItem.getTag();
        }
        holder.itemImage.setImageBitmap(offersWithImage.get(position).image);
        holder.offerTitle.setText(offersWithImage.get(position).title);
        singleItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyCounterofferActivity.class);
                intent.putExtra("counterOfferId", offersWithImage.get(position).id);
                intent.putExtra("offerId", offersWithImage.get(position).counterOfferId);
                context.startActivity(intent);
            }
        });

        return singleItem;
    }

}
