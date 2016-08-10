package com.calendaridex.activity;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.calendaridex.constants.ActionNames;
import com.calendaridex.constants.NotificationConstants;

/**
 * Created by Navruz on 25.05.2016.
 */
public abstract class BaseCEActivity extends AppCompatActivity {

    private static ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
}
