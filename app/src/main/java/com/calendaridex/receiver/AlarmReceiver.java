package com.calendaridex.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.calendaridex.constants.ExtraNames;
import com.calendaridex.service.AlarmService;

/**
 * Created by Navruz on 04.08.2016.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Intent alarmServiceIntent = new Intent(context, AlarmService.class);
            alarmServiceIntent.putExtra(ExtraNames.ALARM_MESSAGE, intent.getExtras().getString(ExtraNames.ALARM_MESSAGE));
            context.startService(alarmServiceIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}