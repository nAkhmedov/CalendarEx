package com.calendaridex.database.schemas;

import com.calendaridex.database.SchemaVersion;

import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class Version1 extends SchemaVersion {

    public Version1(boolean current) {
        super(current);
        addEntities(getSchema());
    }

    @Override
    public int getVersionNumber() {
        return 1;
    }

    private static void addEntities(Schema schema) {

        Entity event = schema.addEntity("Event");
        event.addIdProperty()
                .columnName("event_id");
        event.addStringProperty("title")
                .columnName("title");
        event.addDateProperty("startDate")
                .columnName("start_date");
        event.addDateProperty("endDate")
                .columnName("end_date");
        event.addBooleanProperty("adminEvent")
                .columnName("admin_event")
                .notNull();
        event.addStringProperty("alarmTime")
                .columnName("alarm_time");
        event.addIntProperty("alarmRepeatPosition")
                .columnName("alarm_repeat_position")
        .notNull();
        /* Relations */

    }
}
