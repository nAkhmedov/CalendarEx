package com.calendaridex.service;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.calendaridex.widget.CalendarWidget;

/**
 * Created by Navruz on 10.08.2016.
 */
public class WidgetUpdateDateService extends Service {

    private static final String TAG = "WidgetUpdateDateService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(Intent.ACTION_DATE_CHANGED);
        registerReceiver(dateReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(dateReceiver);
    }

    private BroadcastReceiver dateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_TIME_CHANGED) ||
                    intent.getAction().equals(Intent.ACTION_TIME_TICK) ||
                    intent.getAction().equals(Intent.ACTION_DATE_CHANGED)) {
                Log.i(TAG, "onReceive ACTION_DATE_CHANGED method called");
                updateAllWidgets(context);
            }
        }
    };

    private void updateAllWidgets(Context context) {
        AppWidgetManager man = AppWidgetManager.getInstance(context);
        int[] ids = man.getAppWidgetIds(
                new ComponentName(context, CalendarWidget.class));
        Intent intent = new Intent(context, CalendarWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        context.sendBroadcast(intent);
    }
}
