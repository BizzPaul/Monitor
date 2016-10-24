package com.projects.stevelmans.monitor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


/**
 * Created by stevelmans on 17/10/16.
 * Receives boot event to set alarm if enabled
 */

public class BootReceiver extends BroadcastReceiver {
    AlarmReceiver alarm = new AlarmReceiver();
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
            SharedPreferences sharedPref = context.getSharedPreferences(
                    Constants.MONITORING_ENABLED, Context.MODE_PRIVATE);
            if (sharedPref.getBoolean(Constants.MONITORING_ENABLED, false)) {
                alarm.setAlarm(context);
            }
        }
    }
}
