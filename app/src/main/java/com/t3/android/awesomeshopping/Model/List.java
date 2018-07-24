package com.t3.android.awesomeshopping.Model;

import java.util.ArrayList;
import java.util.HashMap;

public class List {

    private String mName;
    private String mKey;
    private String mOwner;
    private HashMap<String, Boolean> mParticipants;
    private HashMap<String, Item> mItems;

    public List() {
    }

    public List(String name, String owner, String key) {
        mName = name;
        mOwner = owner;
        mKey = key;
        mParticipants = new HashMap<>();
        mParticipants.put(owner, true);
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String owner) {
        mOwner = owner;
    }

    public HashMap<String, Boolean> getParticipants() {
        return mParticipants;
    }

    public void setParticipants(HashMap<String, Boolean> participants) {
        mParticipants = participants;
    }

    public HashMap<String, Item> getItems() {
        return mItems;
    }

    public ArrayList<Item> getItemsArray() {
        return mItems == null ? new ArrayList<>() : new ArrayList<>(mItems.values());
    }

    public void setItems(HashMap<String, Item> items) {
        mItems = items;
    }

    public Integer getPurchasedCount() {
        Integer purchased = 0;
        for (Item item : getItemsArray()) {
            if (item.getPurchased())
                purchased += 1;
        }
        return purchased;
    }

    public Integer getItemCount(String itemKey) {
        for (Item item : getItemsArray()) {
            if (item.getKey().equals(itemKey))
                return item.getCount();
        }
        return 0;
    }

    public void updateTo(List list) {
        if (list != null) {
            mName = list.getName();
            mOwner = list.getOwner();
            mKey = list.getKey();
            mParticipants = list.getParticipants();
            mItems = list.getItems();
        }
    }
}
