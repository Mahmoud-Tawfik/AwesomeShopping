package com.t3.android.awesomeshopping.Util;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.t3.android.awesomeshopping.Model.Item;
import com.t3.android.awesomeshopping.Model.User;
import com.t3.android.awesomeshopping.R;
import com.t3.android.awesomeshopping.widget.LastListWidget;

import java.util.Arrays;
import java.util.List;

public class FirebaseUtils {
    public static final int RC_SIGN_IN = 123;
    private static Boolean persistenceEnabled = false;

    private static List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.FacebookBuilder().build());

    public static User currentUser;

    public static FirebaseDatabase database() {return FirebaseDatabase.getInstance();}
    public static DatabaseReference catalogCategoriesRef() { return database().getReference("catalog").child("categories");}
    public static DatabaseReference catalogItemsRef() { return database().getReference("catalog").child("items");}

    public static DatabaseReference usersRef() { return database().getReference("users");}
    public static DatabaseReference userRef () { return  usersRef().child(currentUser.getUid());}
    public static DatabaseReference listsRef() { return database().getReference("lists");}
    public static DatabaseReference listRef(String listKey) { return listsRef().child(listKey);}
    public static DatabaseReference listItemsRef(String listKey) { return listRef(listKey).child("items");}
    public static DatabaseReference listItemRef(String listKey, String itemKey) { return listItemsRef(listKey).child(itemKey);}
    public static DatabaseReference listItemCountRef(String listKey, String itemKey) { return listItemRef(listKey, itemKey).child("count");}
    public static DatabaseReference listParticipantsRef(String listKey) { return listRef(listKey).child("participants");}
    public static DatabaseReference favoritesRef () { return  userRef().child("favorites");}
    public static DatabaseReference specialsRef () { return  userRef().child("specials");}

    public static void launchSignin(Activity activity){
        activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.ic_logo)
                        .setTheme(R.style.AppTheme_NoActionBar)
                        .build(),
                RC_SIGN_IN);
    }

    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static void signout(Runnable onSignoutComplete){
        FirebaseAuth.getInstance().signOut();
        if (onSignoutComplete != null){
            onSignoutComplete.run();
        }
    }

    public static void enablePersistence(){
        if (!persistenceEnabled){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            persistenceEnabled = true;
        }
    }

    public static void updateWidget(Context context, com.t3.android.awesomeshopping.Model.List list){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor prefsEditor = prefs.edit();
        String items = "";
        for(int i = 0; i < list.getItemsArray().size(); i++){
            Item item = list.getItemsArray().get(i);
            items += item.getName() + (item.getCount() > 1 ? " (" + item.getCount() + ") ": "") + (i == (list.getItemsArray().size() - 1) ? "" : ";,");
        }
        prefsEditor.putString(context.getString(R.string.desired_list_items), items);
        prefsEditor.putString(context.getString(R.string.desired_list_name), list.getName());
        prefsEditor.putString(context.getString(R.string.desired_list_key), list.getKey());
        prefsEditor.commit();

        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] ids = manager.getAppWidgetIds(new ComponentName(context,LastListWidget.class));
        Intent updateIntent = new Intent();
        updateIntent.putExtra(LastListWidget.WIDGET_IDS_KEY, ids);
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        context.sendBroadcast(updateIntent);
    }
}
