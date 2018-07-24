package com.t3.android.awesomeshopping.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.t3.android.awesomeshopping.Model.CatalogItem;
import com.t3.android.awesomeshopping.R;
import com.t3.android.awesomeshopping.Util.FirebaseUtils;

import java.util.ArrayList;

public class FavoriteItemHolder extends RecyclerView.ViewHolder {
    private final TextView mItemName;
    private final ImageButton mAddedFav;
    private final View mDivider;

    public FavoriteItemHolder(View itemView) {
        super(itemView);
        mItemName = itemView.findViewById(R.id.item_name);
        mAddedFav = itemView.findViewById(R.id.added_fav);
        mDivider = itemView.findViewById(R.id.divider);
    }

    public void bind(CatalogItem catalogItem, ArrayList<CatalogItem> favorites) {
        mItemName.setText(catalogItem.getName());
        Boolean itemInFav = findItemInFav(catalogItem, favorites);
        setFav(itemInFav);

        itemView.setOnClickListener(view -> FirebaseUtils.favoritesRef().child(catalogItem.getKey()).setValue(itemInFav ? null : catalogItem));
    }

    private void setFav(boolean added) {
        mDivider.setVisibility(added ? View.VISIBLE : View.GONE);
        mAddedFav.setVisibility(added ? View.VISIBLE : View.GONE);

        mItemName.setTextAppearance(itemView.getContext(), added ? R.style.AppTheme_primary_text_medium : R.style.AppTheme_secondary_text_medium);
    }

    private Boolean findItemInFav(CatalogItem catalogItem, ArrayList<CatalogItem> favorites){
        for (CatalogItem item : favorites){
            if (item.getKey().equals(catalogItem.getKey()))
                return true;
        }
        return false;
    }

}
