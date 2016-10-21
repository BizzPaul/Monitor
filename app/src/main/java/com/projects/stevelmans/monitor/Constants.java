package com.projects.stevelmans.monitor;

import java.sql.Struct;

/**
 * Created by stevelmans on 17/10/16.
 */

public final class Constants {
    // Defines a custom Intent action
    public static final String BROADCAST_ACTION = "com.example.android.threadsample.BROADCAST";
    // Defines the key for the status "extra" in an Intent
    public static final String EXTENDED_DATA_STATUS = "com.example.android.threadsample.STATUS";
    public static final int MAX_QUOTA = 5;
    public static final String QUOTA_STORAGE_NAME = "quota.txt";
    public static final String QUOTA_STORAGE_DAYOFYEAR = "date";
    public static final String QUOTA_STORAGE_VALUE = "value";
    public static final String MONITORING_ENABLED = "monitoring enabled";
    public static final String PASSWORD = "password";
    public static final int VIBRATIONTHRESHOLD = 10;
    public static final String EXCLUDE_SELF = "com.projects.stevelmans.monitor";
    public static final String EXCLUDE_DESKTOP = "com.sec.android.app.launcher";

    public class ACTIONS
    {
        public static final String RELEASE = "release";
        public static final String LOGIN = "login";
    }

}

