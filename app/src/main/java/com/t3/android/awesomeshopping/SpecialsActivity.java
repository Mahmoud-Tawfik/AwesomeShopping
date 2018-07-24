package com.t3.android.awesomeshopping;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.t3.android.awesomeshopping.Model.CatalogItem;
import com.t3.android.awesomeshopping.Util.FirebaseUtils;
import com.t3.android.awesomeshopping.ViewHolder.SpecialItemHolder;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SpecialsActivity extends AppCompatActivity {
    private static final String RECYCLER_LAYOUT = "recycler_layout";

    private FirebaseRecyclerAdapter adapter;

    @BindView(R.id.adView) AdView mAdView;
    @BindView(R.id.items_recycler) RecyclerView mItemsRecycler;

    public static void launch(Context context) {
        Intent intent = new Intent(context, SpecialsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specials);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Query query = FirebaseUtils.specialsRef();
        FirebaseRecyclerOptions<CatalogItem> options = new FirebaseRecyclerOptions.Builder<CatalogItem>().setQuery(query, CatalogItem.class).build();

        adapter = new FirebaseRecyclerAdapter<CatalogItem, SpecialItemHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SpecialItemHolder holder, int position, @NonNull CatalogItem item) {
                holder.bind(item);
            }

            @NonNull
            @Override
            public SpecialItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.special_list_item, parent, false);
                return new SpecialItemHolder(view);
            }

            @Override
            public void onDataChanged() {
                findViewById(R.id.no_items_text).setVisibility(adapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
                if (savedInstanceState != null && savedInstanceState.getParcelable(RECYCLER_LAYOUT) != null){
                    mItemsRecycler.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(RECYCLER_LAYOUT));
                    savedInstanceState.putParcelable(RECYCLER_LAYOUT, null);
                }
            }
        };

        mItemsRecycler.setAdapter(adapter);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addSpecial(View view){
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint(R.string.item_name_hint);

        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_add_item_title)
                .setView(input)
                .setNegativeButton(R.string.dialog_cancel, null)
                .setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
                    String itemName = input.getText().toString();
                    if (itemName.isEmpty()) itemName = getString(R.string.default_item_name);
                    DatabaseReference newItemRef = FirebaseUtils.specialsRef().push();
                    CatalogItem newItem = new CatalogItem(itemName, null, newItemRef.getKey());
                    newItemRef.setValue(newItem);
                }).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RECYCLER_LAYOUT, mItemsRecycler.getLayoutManager().onSaveInstanceState());
    }
}