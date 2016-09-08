package com.calendaridex.activity;

import android.os.Bundle;

import com.calendaridex.R;
import com.calendaridex.fragment.NotificationSoundFragment;

/**
 * Created by Navruz on 04.08.2016.
 */
public class SettingsActivity extends BaseCEActivity {

    public final static String TAG_FRAGMENT_NOTIFICATION = "TAG_FRAGMENT_NOTIFICATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);
        getFragmentManager().beginTransaction()
                .replace(R.id.content, new NotificationSoundFragment(), TAG_FRAGMENT_NOTIFICATION)
                .commit();
    }


}
