package com.t3.android.awesomeshopping.ViewHolder;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.t3.android.awesomeshopping.Util.FirebaseUtils;
import com.t3.android.awesomeshopping.Model.CatalogItem;
import com.t3.android.awesomeshopping.R;

public class SpecialItemHolder extends RecyclerView.ViewHolder {
    private final TextView mItemName;
    private final ImageButton mDeleteButton;

    public SpecialItemHolder(View itemView) {
        super(itemView);
        mItemName = itemView.findViewById(R.id.item_name);
        mDeleteButton = itemView.findViewById(R.id.delete_button);
    }

    public void bind(CatalogItem item) {
        mItemName.setText(item.getName());
        mDeleteButton.setOnClickListener(view -> {
            new AlertDialog.Builder(itemView.getContext())
                    .setMessage(R.string.dialog_delete_item_message)
                    .setNegativeButton(R.string.dialog_cancel, null)
                    .setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
                        FirebaseUtils.specialsRef().child(item.getKey()).setValue(null);
                    }).show();
        });
    }
}
