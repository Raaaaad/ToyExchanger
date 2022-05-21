package com.rp.toyexchanger.ui.ui.Offers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.rp.toyexchanger.R;
import com.rp.toyexchanger.data.OfferWithImage;

import java.util.List;

public class OffersAdapter extends ArrayAdapter<OfferWithImage> {
    Context context;

    List<OfferWithImage> offersWithImage;

    public OffersAdapter(Context context, List<OfferWithImage> offersWithImage) {
        super(context, R.layout.single_offer, R.id.title_text_view, offersWithImage);
        this.context = context;
        this.offersWithImage = offersWithImage;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View singleItem = convertView;
        OffersViewHolder holder = null;
        if (singleItem == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            singleItem = layoutInflater.inflate(R.layout.single_offer, parent, false);
            holder = new OffersViewHolder(singleItem);
            singleItem.setTag(holder);
        } else {
            holder = (OffersViewHolder) singleItem.getTag();
        }
        holder.itemImage.setImageBitmap(offersWithImage.get(position).image);
        holder.offerTitle.setText(offersWithImage.get(position).title);
        return singleItem;
    }

}
