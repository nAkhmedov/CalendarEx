package com.calendaridex.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.calendaridex.R;
import com.calendaridex.constants.ActionNames;
import com.calendaridex.constants.ContextConstants;
import com.calendaridex.util.AndroidUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by navruz on 9/8/16.
 */
public class LockScreenActivity extends BaseCEActivity {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM  yyyy", ContextConstants.currentCountry);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON|
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.lock_sceen);

        ((TextView) findViewById(R.id.lock_date)).setText(dateFormat.format(AndroidUtil.getCurrentDate()));
        Calendar cal = Calendar.getInstance();
        int selectedHour = cal.get(Calendar.HOUR_OF_DAY);
        int selectedMinute = cal.get(Calendar.MINUTE);
        String hours = selectedHour + "";
        String minutes = selectedMinute + "";
        if(selectedHour < 10) {
            hours = "0"+ selectedHour;
        }
        if(selectedMinute < 10)
            minutes = "0"+selectedMinute;

        ((TextView) findViewById(R.id.alarm_time)).setText(hours + " : " + minutes);
        ImageView dismissAlarmView = (ImageView) findViewById(R.id.lock_dismiss_alarm);
        dismissAlarmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(new Intent(ActionNames.ACTION_DISMISS));
                Intent reLaunchMain = new Intent(LockScreenActivity.this, MainActivity.class);
                reLaunchMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(reLaunchMain);

                finish();
            }
        });
    }
}
