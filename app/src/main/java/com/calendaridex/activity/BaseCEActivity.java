package com.calendaridex.activity;

import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.calendaridex.R;
import com.calendaridex.widget.CalendarWidget;

/**
 * Created by Navruz on 25.05.2016.
 */
public abstract class BaseCEActivity extends AppCompatActivity {

    private static ProgressDialog dialog;
    public SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(BaseCEActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void showDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog == null) {
                    dialog = new ProgressDialog(BaseCEActivity.this);
//                    dialog.setMessage(getResources().getString(R.string.loading));
                    dialog.setCancelable(false);
                    dialog.show();
                }
            }
        });
    }

    public void showCustomDialog(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog == null) {
                    dialog = new ProgressDialog(BaseCEActivity.this);
                    dialog.setMessage(text);
                    dialog.setCancelable(false);
                    dialog.show();
                }
            }
        });
    }

    public void dismissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    public void hideKeyboard(EditText view) {
        view.clearFocus();
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void setActionBarColor() {
        int themeColor = prefs.getInt("app_theme_color", ContextCompat.getColor(BaseCEActivity.this, R.color.colorPrimary) );
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Integer.valueOf(themeColor)));
    }

    public void updateAllWidgets() {
        AppWidgetManager man = AppWidgetManager.getInstance(BaseCEActivity.this);
        int[] ids = man.getAppWidgetIds(
                new ComponentName(BaseCEActivity.this, CalendarWidget.class));
        Intent intent = new Intent(this,CalendarWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);
    }
}
