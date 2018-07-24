package com.t3.android.awesomeshopping.Model;

public class Item {
    private String mName;
    private String mCategory;
    private String mKey;
    private Boolean mPurchased;
    private int mCount;

    public Item() {}

    public Item(CatalogItem catalogItem) {
        mName = catalogItem.getName();
        mCategory = catalogItem.getCategory();
        mKey = catalogItem.getKey();
        mPurchased = false;
        mCount = 1;
    }

    public String getKey() { return mKey; }

    public void setKey(String key) { mKey = key; }

    public String getName() { return mName; }

    public void setName(String name) { mName = name; }

    public String getCategory() { return mCategory; }

    public void setCategory(String category) { mCategory = category; }

    public Boolean getPurchased() { return mPurchased; }

    public void setPurchased(Boolean purchased) { mPurchased = purchased; }

    public int getCount() { return mCount; }

    public void setCount(int count) { mCount = count; }
}