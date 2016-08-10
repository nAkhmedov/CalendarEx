package com.calendaridex.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;

import com.calendaridex.R;
import com.calendaridex.activity.MainActivity;
import com.calendaridex.constants.ContextConstants;
import com.calendaridex.service.WidgetService;
import com.calendaridex.service.WidgetUpdateDateService;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Navruz on 18.06.2016.
 */
public class CalendarWidget extends AppWidgetProvider {

    private static final String TAG = "CalendarWidget";

    private SimpleDateFormat monthDateFormat = new SimpleDateFormat("d MMMM", ContextConstants.currentCountry);
    private static int uniqueIndex = 0;
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        context.startService(new Intent(context, WidgetUpdateDateService.class));
        Log.i(TAG, "onEnabled method called");

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        Log.i(TAG, "onDeleted method called");
        super.onDeleted(context, appWidgetIds);
        context.stopService(new Intent(context, WidgetUpdateDateService.class));
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.i(TAG, "onDisabled method called");
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
        Log.i(TAG, "onAppWidgetOptionsChanged method called");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive method called");
        super.onReceive(context, intent);

    }

    @Override
    public void onRestored(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        super.onRestored(context, oldWidgetIds, newWidgetIds);
        Log.i(TAG, "onRestored method called");
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        Log.i(TAG, "onUpdate method called");

        uniqueIndex++;
        for(int appWidgetId : appWidgetIds) {
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            Intent svcIntent = new Intent(context, WidgetService.class);

            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            svcIntent.setData(Uri.fromParts("content", String.valueOf(uniqueIndex), null));

            RemoteViews widget = new RemoteViews(context.getPackageName(),
                    R.layout.widget_layout);

            widget.setTextViewText(R.id.month_view, monthDateFormat.format(new Date()));
//        widget.setTextViewText(R.id.date_view, dateFormat.format(new Date()));
            widget.setRemoteAdapter(R.id.eventList, svcIntent);
            widget.setEmptyView(R.id.eventList, R.id.empty_view);
            widget.setOnClickPendingIntent(R.id.widget_parent, pendingIntent);

//        // Get the layout for the App Widget and attach an on-click listener
//        // to the button
//        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
//        views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);
//
//        String eventTitle = context.getResources().getString(R.string.no_events);
//        if (eventsList != null && eventsList.size() > 0)
//            eventTitle = eventsList.get(staticIndex).getTitle();
//
//        views.setTextViewText(R.id.eventList, eventTitle);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, widget);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }
}