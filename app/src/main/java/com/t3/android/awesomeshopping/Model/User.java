package com.t3.android.awesomeshopping.Model;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;

public class User {
    private String mName;
    private String mEmail;
    private String mThumbnailUrl;
    private String mUid;

    public User() {}

    public User(FirebaseUser user) {
        mName = user.getDisplayName();
        mEmail = user.getProviderData().get(0).getEmail();
        Uri thumbnailUrl = user.getProviderData().get(0).getPhotoUrl();
        mThumbnailUrl = thumbnailUrl != null ? thumbnailUrl.toString() : "";
        mUid = user.getUid();
    }

    public User(String name, String thumbnailUrl, String uid) {
        mName = name;
        mThumbnailUrl = thumbnailUrl;
        mUid = uid;
    }

    public String getName() { return mName; }

    public void setName(String name) { mName = name; }

    public String getInitials() { return mName; }

    public String getThumbnailUrl() { return mThumbnailUrl; }

    public String getLargeThumbnailUrl() { return mThumbnailUrl.isEmpty() ? "" : mThumbnailUrl + "?type=large"; }

    public void setThumbnailUrl(String thumbnailUrl) { mThumbnailUrl = thumbnailUrl; }

    public String getUid() { return mUid; }

    public void setUid(String uid) { mUid = uid; }

}
