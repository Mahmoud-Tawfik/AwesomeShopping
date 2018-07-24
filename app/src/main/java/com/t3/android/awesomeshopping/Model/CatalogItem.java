package com.t3.android.awesomeshopping.Model;

public class CatalogItem {
    private String mName;
    private String mCategory;
    private String mKey;

    public CatalogItem() {}

    public CatalogItem(String name, String category, String key) {
        mName = name;
        mCategory = category;
        mKey = key;
    }

    public String getKey() { return mKey; }

    public void setKey(String key) { mKey = key; }

    public String getCategory() { return mCategory; }

    public void setCategory(String category) { mCategory = category; }

    public String getName() { return mName; }

    public void setName(String name) { mName = name; }
}