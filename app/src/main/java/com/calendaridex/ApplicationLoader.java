package com.calendaridex;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.calendaridex.database.DaoMaster;
import com.calendaridex.database.DaoSession;
import com.calendaridex.database.UpdateHelper;


/**
 * Created by Navruz on 20.06.2016.
 */
public class ApplicationLoader extends Application {

    private DaoSession daoSession;
    private static Context appContext;

    @Override
    public void onCreate() {
        super.onCreate();

        appContext = getApplicationContext();

        UpdateHelper helper = new UpdateHelper(this, "event_store.s3db", null);
        SQLiteDatabase localDb = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(localDb);
        daoSession = daoMaster.newSession();
    }

    public static ApplicationLoader getApplication(Context context) {
        if (context instanceof ApplicationLoader) {
            return (ApplicationLoader) context;
        }
        return (ApplicationLoader) context.getApplicationContext();
    }

    public static Context getAppContext() {
        return appContext;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
