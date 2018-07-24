package com.t3.android.awesomeshopping;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.t3.android.awesomeshopping.Model.List;
import com.t3.android.awesomeshopping.Model.User;
import com.t3.android.awesomeshopping.Util.FirebaseUtils;
import com.t3.android.awesomeshopping.ViewHolder.ListHolder;

import agency.tango.android.avatarview.IImageLoader;
import agency.tango.android.avatarview.views.AvatarView;
import agency.tango.android.avatarviewglide.GlideLoader;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MyListsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MyListsActivity";

    @BindView(R.id.adView) AdView mAdView;
    @BindView(R.id.rv_lists) RecyclerView mlistsRV;
    @BindView(R.id.drawer_layout) DrawerLayout drawer;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.nav_view) NavigationView navigationView;

    FirebaseRecyclerAdapter adapter;

    public static void launch(Context context) {
        Intent intent = new Intent(context, MyListsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_lists);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        updateUI();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void updateUI(){
        FirebaseUtils.currentUser = new User(FirebaseAuth.getInstance().getCurrentUser());

        TextView userName = navigationView.getHeaderView(0).findViewById(R.id.username);
        AvatarView avatarView = navigationView.getHeaderView(0).findViewById(R.id.avatar);
        IImageLoader imageLoader = new GlideLoader();
        imageLoader.loadImage(avatarView, FirebaseUtils.currentUser.getLargeThumbnailUrl(), FirebaseUtils.currentUser.getName());
        userName.setText(FirebaseUtils.currentUser.getName());


        Query query = FirebaseUtils.listsRef().orderByChild("participants/"+FirebaseUtils.currentUser.getUid()).equalTo(true);
        FirebaseRecyclerOptions<List> options = new FirebaseRecyclerOptions.Builder<List>().setQuery(query, List.class).build();

        adapter = new FirebaseRecyclerAdapter<List, ListHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ListHolder holder, int position, @NonNull List list) {
                holder.bind(list);
            }

            @NonNull
            @Override
            public ListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
                return new ListHolder(view);
            }

            @Override
            public void onDataChanged() {
                findViewById(R.id.no_lists_text).setVisibility(adapter.getItemCount() > 0 ? View.GONE : View.VISIBLE);
            }
        };

        mlistsRV.setAdapter(adapter);
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

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_my_lists) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_favorites) {
            FavoritesActivity.launch(this);
        } else if (id == R.id.nav_specials) {
            SpecialsActivity.launch(this);
        } else if (id == R.id.nav_scan_barcode) {
            new IntentIntegrator(this).initiateScan();
        } else if (id == R.id.nav_logout) {
            FirebaseUtils.signout(() ->{
                SplashActivity.launch(this);
                finish();
            });
        }

        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, R.string.cancelled, Toast.LENGTH_LONG).show();
            } else {
                if(result.getContents().startsWith(getString(R.string.share_prefix))){
                    String listKey = result.getContents().replace(getString(R.string.share_prefix), "");
                    FirebaseUtils.listParticipantsRef(listKey).child(FirebaseUtils.currentUser.getUid()).setValue(true);
                } else {
                    Toast.makeText(this, R.string.wrong_code_message, Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void addNewList(View view) {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setHint(R.string.list_name_hint);

        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_add_list_title)
                .setView(input)
                .setNegativeButton(R.string.dialog_cancel, null)
                .setPositiveButton(R.string.dialog_ok, (dialog, which) -> {
                    String listName = input.getText().toString();
                    if (listName.isEmpty()) listName = getString(R.string.default_list_name);
                    DatabaseReference newListRef = FirebaseUtils.listsRef().push();
                    List newList = new List(listName, FirebaseUtils.currentUser.getUid(), newListRef.getKey());
                    newListRef.setValue(newList)
                            .addOnSuccessListener(aVoid -> ListDetailsActivity.launch(MyListsActivity.this, newList, true));
                }).show();
    }
}
