package com.t3.android.awesomeshopping.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.t3.android.awesomeshopping.FavoritesTabFragment;
import com.t3.android.awesomeshopping.Model.CatalogCategory;
import com.t3.android.awesomeshopping.Model.CatalogItem;

import java.util.ArrayList;

public class FavoritesPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private ArrayList<CatalogCategory> mCatalogCategories;
    private ArrayList<CatalogItem> mFavorites;

    public FavoritesPagerAdapter(FragmentManager fm, int NumOfTabs, ArrayList<CatalogCategory> category, ArrayList<CatalogItem> favorites) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        mCatalogCategories = category;
        mFavorites = favorites;
    }

    @Override
    public Fragment getItem(int position) {
        FavoritesTabFragment fragment = new FavoritesTabFragment();
        if (position == getCount() - 1){
            fragment.setCategoryKey("specials");
        } else {
            fragment.setCategoryKey(mCatalogCategories.get(position).getKey());
        }
        fragment.setFavorites(mFavorites);
        return fragment;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
