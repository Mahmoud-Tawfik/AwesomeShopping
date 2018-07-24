package com.t3.android.awesomeshopping;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.t3.android.awesomeshopping.Model.CatalogCategory;
import com.t3.android.awesomeshopping.Model.Item;
import com.t3.android.awesomeshopping.Util.FirebaseUtils;
import com.t3.android.awesomeshopping.ViewHolder.ItemHolder;
import com.t3.android.awesomeshopping.Model.List;
import com.t3.android.awesomeshopping.Model.User;
import com.t3.android.awesomeshopping.adapter.CategoriesPagerAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ListDetailsActivity extends AppCompatActivity {
    private static final String SELECTED_TAB = "selected_tab";
    private static final String RECYCLER_LAYOUT = "recycler_layout";

    private Boolean isOwner = false;
    private static Boolean mEditMode = false;
    private User mCurrentUser;

    private static List mList;

    private FirebaseRecyclerAdapter normalModeAdapter;
    private CategoriesPagerAdapter categoriesPagerAdapter;
    private ArrayList<CatalogCategory> mCatalogCategories;

    @BindView(R.id.adView) AdView mAdView;
    @BindView(R.id.items_recycler_normal) RecyclerView mItemsNormal;
    @BindView(R.id.fab) FloatingActionButton mFAB;
    @BindView(R.id.normal_mode) RelativeLayout mNormalLayout;
    @BindView(R.id.edit_mode) RelativeLayout mEditLayout;
    @BindView(R.id.tab_layout) TabLayout mTabLayout;
    @BindView(R.id.pager) ViewPager mViewPager;

    public static void launch(Context context, List list, Boolean editMode) {
        Intent intent = new Intent(context, ListDetailsActivity.class);
        mList = list;
        mEditMode = editMode;

        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_details);
        ButterKnife.bind(this);

        if (mList != null){
            FirebaseUtils.updateWidget(this, mList);
        } else {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            mList = new List(prefs.getString(getString(R.string.desired_list_name), ""), "",
                    prefs.getString(getString(R.string.desired_list_key), ""));
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(mList.getName());

        mCurrentUser = new User(FirebaseAuth.getInstance().getCurrentUser());
        isOwner = mList.getOwner().equals(mCurrentUser.getUid());

        Query query = FirebaseUtils.listItemsRef(mList.getKey()).orderByChild("purchased");
        FirebaseRecyclerOptions<Item> options = new FirebaseRecyclerOptions.Builder<Item>().setQuery(query, Item.class).build();

        normalModeAdapter = new FirebaseRecyclerAdapter<Item, ItemHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ItemHolder holder, int position, @NonNull Item item) {
                holder.bind(mList.getKey(), item);
            }

            @NonNull
            @Override
            public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_item, parent, false);
                return new ItemHolder(view);
            }

            @Override
            public void onDataChanged() {
                findViewById(R.id.no_list_items_text).setVisibility(normalModeAdapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
                if (savedInstanceState != null && savedInstanceState.getParcelable(RECYCLER_LAYOUT) != null){
                    mItemsNormal.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(RECYCLER_LAYOUT));
                    savedInstanceState.putParcelable(RECYCLER_LAYOUT, null);
                }
            }
        };

        mItemsNormal.setAdapter(normalModeAdapter);

        FirebaseUtils.catalogCategoriesRef().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                mCatalogCategories = new ArrayList<>();
                mTabLayout.addTab(mTabLayout.newTab().setText("Favorites").setIcon(R.drawable.ic_favorite));
                for (DataSnapshot category : snapshot.getChildren()) {
                    CatalogCategory catalogCategory = new CatalogCategory(category.getValue().toString(), category.getKey());
                    mCatalogCategories.add(catalogCategory);
                    mTabLayout.addTab(mTabLayout.newTab().setText(catalogCategory.getName()).setIcon(R.drawable.ic_list));
                }
                mTabLayout.addTab(mTabLayout.newTab().setText("Specials").setIcon(R.drawable.ic_list_add));
                categoriesPagerAdapter = new CategoriesPagerAdapter(getSupportFragmentManager(), mTabLayout.getTabCount(), mCatalogCategories, mList);
                mViewPager.setAdapter(categoriesPagerAdapter);
                if (savedInstanceState != null){
                    Integer selectedTab = savedInstanceState.getInt(SELECTED_TAB);
                    mTabLayout.getTabAt(selectedTab).select();
                } else {
                    mTabLayout.getTabAt(1).select();
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

        updateEditMode();

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onStart() {
        super.onStart();
        normalModeAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        normalModeAdapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(isOwner ? R.menu.details_owner : R.menu.details_other, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_invite:
                InviteActivity.launch(this, mList.getKey());
                return true;
            case R.id.action_delete:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_delete_title)
                        .setMessage(R.string.dialog_delete_message)
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .setPositiveButton(R.string.dialog_delete, (dialog, which) -> {
                            FirebaseUtils.updateWidget(this, new List());
                            FirebaseUtils.listRef(mList.getKey()).setValue(null);
                            finish();
                        }).show();
                return true;
            case R.id.action_leave:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.dialog_leave_title)
                        .setMessage(R.string.dialog_leave_message)
                        .setNegativeButton(R.string.dialog_cancel, null)
                        .setPositiveButton(R.string.dialog_leave, (dialog, which) -> {
                            FirebaseUtils.listParticipantsRef(mList.getKey()).child(mCurrentUser.getUid()).setValue(null);
                            finish();
                        }).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateEditMode() {
        mFAB.setImageDrawable(mEditMode ? getResources().getDrawable(R.drawable.ic_done) : getResources().getDrawable(R.drawable.ic_edit));
        mFAB.setContentDescription(mEditMode ? getString(R.string.content_description_end_edit_mode) : getString(R.string.content_description_start_edit_mode));
        mNormalLayout.setVisibility(mEditMode ? View.GONE : View.VISIBLE);
        mEditLayout.setVisibility(mEditMode ? View.VISIBLE : View.GONE);
    }

    public void edit(View view) {
        mEditMode = !mEditMode;
        updateEditMode();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_TAB, mTabLayout.getSelectedTabPosition());
        outState.putParcelable(RECYCLER_LAYOUT, mItemsNormal.getLayoutManager().onSaveInstanceState());
    }
}