package com.t3.android.awesomeshopping;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.t3.android.awesomeshopping.Model.CatalogItem;
import com.t3.android.awesomeshopping.Util.FirebaseUtils;
import com.t3.android.awesomeshopping.ViewHolder.FavoriteItemHolder;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesTabFragment extends Fragment {
    private static final String CATEGORY_KEY = "Category_key";
    private static final String RECYCLER_LAYOUT = "recycler_layout";
    private FirebaseRecyclerAdapter adapter;
    private String mCategoryKey;
    private ArrayList<CatalogItem> mFavorites  = new ArrayList<>();
    private Query query;

    private ValueEventListener favoritesListener;

    @BindView(R.id.catalog_items_recycler) RecyclerView mCatalogItemsRV;
    @BindView(R.id.no_items_text) TextView mNoItemsText;

    public FavoritesTabFragment() {
        super();
    }

    public void setCategoryKey(String key){
        mCategoryKey = key;
    }

    public void setFavorites(ArrayList<CatalogItem> favorites){
        mFavorites = favorites;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_catalog_tab, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null){
            mCategoryKey = savedInstanceState.getString(CATEGORY_KEY);
        }

        favoritesListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mFavorites.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    mFavorites.add(snapshot.getValue(CatalogItem.class));
                }
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        FirebaseUtils.favoritesRef().addValueEventListener(favoritesListener);

        switch (mCategoryKey){
            case "specials":
                query = FirebaseUtils.specialsRef();
                break;
            default:
                query = FirebaseUtils.catalogItemsRef().orderByChild("category").equalTo(mCategoryKey);
                break;
        }

        FirebaseRecyclerOptions<CatalogItem> options = new FirebaseRecyclerOptions.Builder<CatalogItem>().setQuery(query, CatalogItem.class).build();

        adapter = new FirebaseRecyclerAdapter<CatalogItem, FavoriteItemHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FavoriteItemHolder holder, int position, @NonNull CatalogItem catalogItem) {
                holder.bind(catalogItem, mFavorites);
            }

            @NonNull
            @Override
            public FavoriteItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new FavoriteItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_list_item, parent, false));
            }

            @Override
            public void onDataChanged() {
                mNoItemsText.setVisibility(adapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
                if (savedInstanceState != null && savedInstanceState.getParcelable(RECYCLER_LAYOUT) != null){
                    mCatalogItemsRV.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(RECYCLER_LAYOUT));
                    savedInstanceState.putParcelable(RECYCLER_LAYOUT, null);
                }
            }
        };
        mCatalogItemsRV.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
        FirebaseUtils.favoritesRef().removeEventListener(favoritesListener);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CATEGORY_KEY, mCategoryKey);
        outState.putParcelable(RECYCLER_LAYOUT, mCatalogItemsRV.getLayoutManager().onSaveInstanceState());
    }
}
