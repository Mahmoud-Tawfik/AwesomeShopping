package com.t3.android.awesomeshopping.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.t3.android.awesomeshopping.CatalogTabFragment;
import com.t3.android.awesomeshopping.Model.CatalogCategory;
import com.t3.android.awesomeshopping.Model.List;

import java.util.ArrayList;

public class CategoriesPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private ArrayList<CatalogCategory> mCatalogCategories;
    private List mList;

    public CategoriesPagerAdapter(FragmentManager fm, int NumOfTabs, ArrayList<CatalogCategory> category, List list) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        mCatalogCategories = category;
        mList = list;
    }

    @Override
    public Fragment getItem(int position) {
        CatalogTabFragment fragment = new CatalogTabFragment();
        if (position == 0){
            fragment.setCategoryKey("favorites");
        } else if (position == getCount() - 1){
            fragment.setCategoryKey("specials");
        } else {
            fragment.setCategoryKey(mCatalogCategories.get(position-1).getKey());
        }
        fragment.setList(mList);
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
