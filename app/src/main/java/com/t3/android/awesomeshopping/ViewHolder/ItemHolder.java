package com.t3.android.awesomeshopping.ViewHolder;

import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.t3.android.awesomeshopping.Model.Item;
import com.t3.android.awesomeshopping.R;
import com.t3.android.awesomeshopping.Util.FirebaseUtils;

public class ItemHolder extends RecyclerView.ViewHolder {
    private final TextView mItemName;
    private final TextView mItemCount;

    public ItemHolder(View itemView) {
        super(itemView);
        mItemName = itemView.findViewById(R.id.itemName);
        mItemCount = itemView.findViewById(R.id.itemCount);
    }

    public void bind(String listKey, Item item) {
        mItemName.setText(item.getName());
        mItemCount.setText(item.getCount() > 1 ? String.valueOf(item.getCount()) : "");
        setPurchased(item.getPurchased());

        itemView.setOnClickListener(view -> {
            item.setPurchased(!item.getPurchased());
            FirebaseUtils.listItemsRef(listKey).child(item.getKey()).child("purchased").setValue(item.getPurchased());
        });
    }

    private void setPurchased(boolean purchased) {
        mItemName.setPaintFlags(purchased ? Paint.STRIKE_THRU_TEXT_FLAG : Paint.ANTI_ALIAS_FLAG);
        mItemCount.setPaintFlags(purchased ? Paint.STRIKE_THRU_TEXT_FLAG : Paint.ANTI_ALIAS_FLAG);

        mItemName.setTextAppearance(itemView.getContext(), purchased ? R.style.AppTheme_secondary_text_medium : R.style.AppTheme_primary_text_medium);
        mItemCount.setTextAppearance(itemView.getContext(), purchased ? R.style.AppTheme_secondary_text_small : R.style.AppTheme_primary_text_small);
    }
}
