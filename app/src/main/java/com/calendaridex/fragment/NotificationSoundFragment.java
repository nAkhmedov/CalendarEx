package com.calendaridex.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import com.calendaridex.R;
import com.calendaridex.activity.BaseCEActivity;
import com.rarepebble.colorpicker.ColorPreference;

/**
 * Created by Navruz on 04.08.2016.
 */
public class NotificationSoundFragment extends PreferenceFragment {

    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private SharedPreferences prefs;
    private ColorPreference appThemeColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_notification_sound);
        setHasOptionsMenu(true);

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        appThemeColor = (ColorPreference) findPreference("app_theme_color");

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                switch (key) {
                    case "alarm_sound": {
//                        currentContent.setNotifyIncoming(sharedPreferences.getBoolean(key, false));
                        break;
                    }
                    case "app_theme_color": {
                        int colorValue = sharedPreferences.getInt(key, 0);
//                        String hexColor = String.format("#%06X", (0xFFFFFF & colorValue));
                        ((BaseCEActivity) getActivity()).setActionBarColor();
                        break;
                    }
                }
            }
        };

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        prefs.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onPause() {
        super.onPause();
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
    }
}
