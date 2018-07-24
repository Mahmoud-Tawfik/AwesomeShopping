package com.t3.android.awesomeshopping.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.t3.android.awesomeshopping.Util.FirebaseUtils;
import com.t3.android.awesomeshopping.ListDetailsActivity;
import com.t3.android.awesomeshopping.Model.List;
import com.t3.android.awesomeshopping.R;

public class LastListWidget extends AppWidgetProvider {
    public static final String WIDGET_IDS_KEY ="recipe_widget_provider_widget_ids";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(WIDGET_IDS_KEY)) {
            int[] ids = intent.getExtras().getIntArray(WIDGET_IDS_KEY);
            this.onUpdate(context, AppWidgetManager.getInstance(context), ids);
        } else
            super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            Intent serviceIntent = new Intent(context, WidgetService.class);
            serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.last_list_widget);
            views.setRemoteAdapter(R.id.widget_list_view, serviceIntent);

            Intent activityIntent = new Intent(context, ListDetailsActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent, 0);
            views.setOnClickPendingIntent(R.id.list_name, pendingIntent);

            String listKey = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.desired_list_key), "");
            FirebaseUtils.enablePersistence();
            FirebaseUtils.listRef(listKey).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    List list = snapshot.getValue(List.class);
                    views.setTextViewText(R.id.list_name, list.getName() == null ? context.getString(R.string.widget_no_list_selected) : list.getName());
                    appWidgetManager.updateAppWidget(appWidgetId, views);
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
            String listName = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.desired_list_name), "");
            views.setTextViewText(R.id.list_name, listName.isEmpty() ? context.getString(R.string.widget_no_list_selected) : listName);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);
    }
}

