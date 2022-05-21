package com.rp.toyexchanger.ui.ui.MyOffers;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.gson.Gson;
import com.rp.toyexchanger.AddOfferActivity;
import com.rp.toyexchanger.R;
import com.rp.toyexchanger.data.OfferWithImage;

import java.util.List;

public class MyOffersAdapter extends ArrayAdapter<OfferWithImage> {
    Context context;

    List<OfferWithImage> offersWithImage;

    public MyOffersAdapter(Context context, List<OfferWithImage> offersWithImage) {
        super(context, R.layout.single_offer, R.id.title_text_view, offersWithImage);
        this.context = context;
        this.offersWithImage = offersWithImage;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View singleItem = convertView;
        MyOffersViewHolder holder = null;
        if (singleItem == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            singleItem = layoutInflater.inflate(R.layout.single_offer, parent, false);
            holder = new MyOffersViewHolder(singleItem);
            singleItem.setTag(holder);
        } else {
            holder = (MyOffersViewHolder) singleItem.getTag();
        }
        holder.itemImage.setImageBitmap(offersWithImage.get(position).image);
        holder.offerTitle.setText(offersWithImage.get(position).title);
        singleItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyOfferDetailsActivity.class);
                Gson gson = new Gson();
                String json = gson.toJson(offersWithImage.get(position));
                intent.putExtra("offer", json);
                context.startActivity(intent);
            }
        });

        return singleItem;
    }

}
