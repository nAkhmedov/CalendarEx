package com.calendaridex.activity;

import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.calendaridex.R;
import com.calendaridex.constants.ExtraNames;

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
        setContentView(R.layout.lock_sceen);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String message = bundle.getString(ExtraNames.ALARM_MESSAGE);
            ((TextView) findViewById(R.id.alarm_txt)).setText(message);
        }
    }
}
