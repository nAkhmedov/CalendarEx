package com.calendaridex.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class UpdateHelper extends DaoMaster.OpenHelper {
//    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateHelper.class);

    public UpdateHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        LOGGER.info("UpdateHelper - Creating tables for schema version " + DaoMaster.SCHEMA_VERSION);
        DaoMaster.createAllTables(db, true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (newVersion) {
            case 2:
//                new MigrationV1ToV2().applyMigration(db, oldVersion);
                break;
            default:
                break;
        }
    }
}
