package com.calendaridex.database;

import de.greenrobot.daogenerator.Schema;

public abstract class SchemaVersion {

    public static final String CURRENT_SCHEMA_PACKAGE = "com.calendaridex.database";

    private Schema schema;

    private boolean current;

    protected SchemaVersion(boolean current) {
        int version = getVersionNumber();
        String packageName = CURRENT_SCHEMA_PACKAGE;
        if (!current) {
            packageName += ".v" + version;
        }
        this.current = current;
        this.schema = new Schema(version, packageName);
        this.schema.enableKeepSectionsByDefault();
    }

    public Schema getSchema() {
        return schema;
    }

    public boolean isCurrent() {
        return current;
    }

    public abstract int getVersionNumber();
}
