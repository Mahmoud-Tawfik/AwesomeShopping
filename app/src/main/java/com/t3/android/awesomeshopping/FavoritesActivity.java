package com.t3.android.awesomeshopping;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.t3.android.awesomeshopping.Model.CatalogCategory;
import com.t3.android.awesomeshopping.Model.CatalogItem;
import com.t3.android.awesomeshopping.Util.FirebaseUtils;
import com.t3.android.awesomeshopping.adapter.FavoritesPagerAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesActivity extends AppCompatActivity {
    private static final String SELECTED_TAB = "selected_tab";

    private FavoritesPagerAdapter pagerAdapter;
    private ArrayList<CatalogCategory> mCatalogCategories;

    private ArrayList<CatalogItem> mFavorites = new ArrayList<>();

    @BindView(R.id.adView) AdView mAdView;
    @BindView(R.id.tab_layout) TabLayout mTabLayout;
    @BindView(R.id.pager) ViewPager mViewPager;

    public static void launch(Context context) {
        Intent intent = new Intent(context, FavoritesActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseUtils.catalogCategoriesRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                mCatalogCategories = new ArrayList<>();
                for (DataSnapshot category : snapshot.getChildren()) {
                    CatalogCategory catalogCategory = new CatalogCategory(category.getValue().toString(), category.getKey());
                    mCatalogCategories.add(catalogCategory);
                    mTabLayout.addTab(mTabLayout.newTab().setText(catalogCategory.getName()).setIcon(R.drawable.ic_list));
                }
                mTabLayout.addTab(mTabLayout.newTab().setText("Specials").setIcon(R.drawable.ic_list_add));
                pagerAdapter = new FavoritesPagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount(), mCatalogCategories, mFavorites);
                mViewPager.setAdapter(pagerAdapter);
                if (savedInstanceState != null){
                    Integer selectedTab = savedInstanceState.getInt(SELECTED_TAB);
                    mTabLayout.getTabAt(selectedTab).select();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_TAB, mTabLayout.getSelectedTabPosition());
    }
}