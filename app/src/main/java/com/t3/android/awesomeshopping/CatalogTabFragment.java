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
import com.t3.android.awesomeshopping.Model.Item;
import com.t3.android.awesomeshopping.Model.List;
import com.t3.android.awesomeshopping.Util.FirebaseUtils;
import com.t3.android.awesomeshopping.ViewHolder.CatalogItemHolder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CatalogTabFragment extends Fragment {
    private static final String LIST_KEY = "list_key";
    private static final String CATEGORY_KEY = "Category_key";
    private static final String RECYCLER_LAYOUT = "recycler_layout";
    private FirebaseRecyclerAdapter adapter;
    private String mCategoryKey;
    private List mList;
    private String listKey;
    private Query query;

    private ValueEventListener listListener;

    @BindView(R.id.catalog_items_recycler) RecyclerView mCatalogItemsRV;
    @BindView(R.id.no_items_text) TextView mNoItemsText;

    public CatalogTabFragment() {
        super();
    }

    public void setCategoryKey(String key){
        mCategoryKey = key;
    }

    public void setList(List list){
        mList = list;
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
            listKey = savedInstanceState.getString(LIST_KEY);
            mCategoryKey = savedInstanceState.getString(CATEGORY_KEY);
        } else {
            listKey = mList.getKey();
        }

        listListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mList = dataSnapshot.getValue(List.class);
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        FirebaseUtils.listRef(listKey).addValueEventListener(listListener);

        switch (mCategoryKey){
            case "favorites":
                query = FirebaseUtils.favoritesRef();
                break;
            case "specials":
                query = FirebaseUtils.specialsRef();
                break;
            default:
                query = FirebaseUtils.catalogItemsRef().orderByChild("category").equalTo(mCategoryKey);
                break;
        }

        FirebaseRecyclerOptions<CatalogItem> options = new FirebaseRecyclerOptions.Builder<CatalogItem>().setQuery(query, CatalogItem.class).build();

        adapter = new FirebaseRecyclerAdapter<CatalogItem, CatalogItemHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CatalogItemHolder holder, int position, @NonNull CatalogItem catalogItem) {
                holder.bind(catalogItem, mList);
            }

            @NonNull
            @Override
            public CatalogItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                CatalogItemHolder catalogItemHolder = new CatalogItemHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.catalog_list_item, parent, false));
                catalogItemHolder.setOnClickListener((view, catalogItem) -> {
                    Integer itemCount = mList.getItemCount(catalogItem.getKey());
                    if ( itemCount > 0){
                        FirebaseUtils.listItemCountRef(listKey, catalogItem.getKey()).setValue(itemCount + 1);
                    } else {
                        FirebaseUtils.listItemRef(listKey, catalogItem.getKey()).setValue(new Item(catalogItem));
                    }
                });

                return catalogItemHolder;
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
        FirebaseUtils.listRef(listKey).removeEventListener(listListener);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CATEGORY_KEY, mCategoryKey);
        outState.putString(LIST_KEY, listKey);
        outState.putParcelable(RECYCLER_LAYOUT, mCatalogItemsRV.getLayoutManager().onSaveInstanceState());
    }
}