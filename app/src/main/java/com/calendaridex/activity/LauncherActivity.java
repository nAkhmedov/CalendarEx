package com.calendaridex.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.calendaridex.R;
import com.calendaridex.util.ImageUtil;

/**
 * Created by Navruz on 01.07.2016.
 */
public class LauncherActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_layout);
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final ImageView imageView = (ImageView) findViewById(R.id.launch_img);
                        imageView.setImageBitmap(ImageUtil.decodeSampledBitmapFromResource(
                                getResources(), R.drawable.calendar_icon, 100, 100));
                    }
                });
            }
        }).start();
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                startActivity(new Intent(LauncherActivity.this, MainActivity.class));
                finish();
            }
        }, 150);

    }
}
