package com.t3.android.awesomeshopping.Model;

public class CatalogCategory {
    private String mName;
    private String mKey;

    public CatalogCategory() {}

    public CatalogCategory(String name, String key) {
        mName = name;
        mKey = key;
    }

    public String getKey() { return mKey; }

    public void setKey(String key) { mKey = key; }

    public String getName() { return mName; }

    public void setName(String name) { mName = name; }

}
