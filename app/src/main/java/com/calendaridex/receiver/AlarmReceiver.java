package com.calendaridex.receiver;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.calendaridex.R;
import com.calendaridex.constants.ActionNames;
import com.calendaridex.constants.ContextConstants;
import com.calendaridex.constants.ExtraNames;
import com.calendaridex.constants.NotificationConstants;

/**
 * Created by Navruz on 04.08.2016.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        try {

            Intent dismissIntent = new Intent();
            dismissIntent.setAction(ActionNames.ACTION_DISMISS);
            PendingIntent piDismiss = PendingIntent.getBroadcast(context, 0, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Bundle bundle = intent.getExtras();
            String message = bundle.getString(ExtraNames.ALARM_MESSAGE);
            int iconId = R.drawable.call_ntfy;
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setContentTitle(context.getResources().getString(R.string.app_name))
                    .setContentText(message)
                    .setSmallIcon(iconId)
                    .setWhen(System.currentTimeMillis())
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setOnlyAlertOnce(true)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(message))
                    .addAction(R.drawable.ic_content_remove, context.getString(R.string.dismiss), piDismiss);;

            Intent nIntent = context.getPackageManager().
                    getLaunchIntentForPackage(ContextConstants.PACKAGE_NAME);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, nIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder.setContentIntent(pendingIntent);
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);;
//            boolean isSoundEnabled = sharedPrefs.getBoolean("sound_control", true);
            String soundValue = sharedPrefs.getString("alarm_sound", R.raw.anthem_id + "");
            if (!soundValue.startsWith("android.resource://")) {
                soundValue = "android.resource://" + context.getPackageName() + "/" + soundValue;
            }

            Uri soundPath = Uri.parse(soundValue);
            notificationBuilder.setSound(soundPath, RingtoneManager.TYPE_ALARM);

            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(NotificationConstants.ALARM_SERVICE, notificationBuilder.build());
        } catch (Exception e) {
            Toast.makeText(context, "There was an error somewhere, but we still received an alarm", Toast.LENGTH_SHORT).show();
            e.printStackTrace();

        }
    }
}