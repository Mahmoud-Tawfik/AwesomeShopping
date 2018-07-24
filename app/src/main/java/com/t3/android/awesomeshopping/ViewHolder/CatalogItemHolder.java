package com.t3.android.awesomeshopping.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.t3.android.awesomeshopping.Util.FirebaseUtils;
import com.t3.android.awesomeshopping.Model.CatalogItem;
import com.t3.android.awesomeshopping.Model.List;
import com.t3.android.awesomeshopping.R;

public class CatalogItemHolder extends RecyclerView.ViewHolder{
    private final TextView mItemName;
    private final TextView mItemCount;
    private final ImageButton mDeleteButton;
    private final View mDivider;

    public CatalogItemHolder(View itemView) {
        super(itemView);
        mItemName = itemView.findViewById(R.id.item_name);
        mItemCount = itemView.findViewById(R.id.item_count);
        mDeleteButton = itemView.findViewById(R.id.delete_button);
        mDivider = itemView.findViewById(R.id.divider);
    }

    public void bind(CatalogItem catalogItem, List list) {
        mItemName.setText(catalogItem.getName());
        Integer itemCount = list.getItemCount(catalogItem.getKey());
        mItemCount.setText(itemCount > 0 ? String.valueOf(itemCount) : "");
        setAdded(itemCount > 0);

        itemView.setOnClickListener(view -> {
            mClickListener.onItemClick(view, catalogItem);
        });

        mDeleteButton.setOnClickListener(view -> {
            FirebaseUtils.listItemRef(list.getKey(), catalogItem.getKey()).setValue(null);
        });
    }

    private CatalogItemHolder.ClickListener mClickListener;

    public interface ClickListener{
        void onItemClick(View view, CatalogItem catalogItem);
    }

    public void setOnClickListener(CatalogItemHolder.ClickListener clickListener){
        mClickListener = clickListener;
    }

    private void setAdded(boolean added) {
        mDivider.setVisibility(added ? View.VISIBLE : View.GONE);
        mDeleteButton.setVisibility(added ? View.VISIBLE : View.GONE);

        mItemName.setTextAppearance(itemView.getContext(), added ? R.style.AppTheme_primary_text_medium : R.style.AppTheme_secondary_text_medium);
        mItemCount.setTextAppearance(itemView.getContext(), added ? R.style.AppTheme_primary_text_small : R.style.AppTheme_secondary_text_small);
    }

}
