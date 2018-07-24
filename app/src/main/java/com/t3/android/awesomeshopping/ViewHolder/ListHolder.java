package com.t3.android.awesomeshopping.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.t3.android.awesomeshopping.ListDetailsActivity;
import com.t3.android.awesomeshopping.Model.List;
import com.t3.android.awesomeshopping.Model.User;
import com.t3.android.awesomeshopping.R;
import com.t3.android.awesomeshopping.Util.FirebaseUtils;

import agency.tango.android.avatarview.IImageLoader;
import agency.tango.android.avatarview.views.AvatarView;
import agency.tango.android.avatarviewglide.GlideLoader;

public class ListHolder extends RecyclerView.ViewHolder{
    private final TextView mListName;
    private final TextView mPurchased;
    private final AvatarView mAvatar;

    public ListHolder(View itemView) {
        super(itemView);
        mListName = itemView.findViewById(R.id.listName);
        mPurchased = itemView.findViewById(R.id.purchased);
        mAvatar = itemView.findViewById(R.id.avatar);
    }

    public void bind(List list) {
        if (list != null){
            mListName.setText(list.getName());
            mPurchased.setText(list.getPurchasedCount() + " / " + list.getItemsArray().size());
            setIsOwner(list.getOwner().equals(FirebaseUtils.currentUser.getUid()), list);
            itemView.setOnClickListener(view -> {
                ListDetailsActivity.launch(itemView.getContext(), list, false);
            });
        }
    }

    private void setIsOwner(boolean isOwner, List list) {
        mAvatar.setVisibility(isOwner ? View.GONE : View.VISIBLE);
        if (!isOwner) {
            FirebaseUtils.usersRef().child(list.getOwner()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    User ownerUSer = snapshot.getValue(User.class);
                    IImageLoader imageLoader = new GlideLoader();
                    imageLoader.loadImage(mAvatar, ownerUSer.getLargeThumbnailUrl(), ownerUSer.getName());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }
}
