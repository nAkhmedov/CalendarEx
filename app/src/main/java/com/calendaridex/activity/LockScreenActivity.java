package com.calendaridex.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

import com.calendaridex.R;

/**
 * Created by navruz on 9/8/16.
 */
public class LockScreenActivity extends BaseCEActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.launcher_layout);
    }
}
