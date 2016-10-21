package com.projects.stevelmans.monitor;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static java.security.AccessController.getContext;

/**
 * Created by stevelmans on 17/10/16.
 */

public class ActivityMonitor {
    public ActivityMonitor() {
        m_ExcludedPrograms = new ArrayList<>();

        m_ExcludedPrograms.add(Constants.EXCLUDE_SELF);
        m_ExcludedPrograms.add(Constants.EXCLUDE_DESKTOP);
    }

    private int m_Quota;
    private List<String> m_ExcludedPrograms;

    public boolean QuotaReached(Context context) {
        boolean result = false;

        if (IsMonitoredAppRunning(context)) {
            m_Quota++;
            SaveQuota(context);

            if (m_Quota >= Constants.MAX_QUOTA) {
                result = true;
            }
        }

        return result;
    }

    public boolean IsMonitoredAppRunning(Context context) {
        boolean result = true;

        if (m_ExcludedPrograms.contains(GetAppInForeground(context))) {
            result = false;
        }

        return result;
    }

    public int QuotaLeft(Context context) {
        return Constants.MAX_QUOTA - m_Quota;
    }

    public void ReadQuota(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                Constants.QUOTA_STORAGE_NAME, Context.MODE_PRIVATE);
        m_Quota = sharedPref.getInt(Constants.QUOTA_STORAGE_VALUE, 0);;
    }

    public void ResetQuota(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                Constants.QUOTA_STORAGE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(Constants.QUOTA_STORAGE_VALUE, 0);
        editor.commit();
        m_Quota = sharedPref.getInt(Constants.QUOTA_STORAGE_VALUE, 0);
    }

    public void SaveQuota(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                Constants.QUOTA_STORAGE_NAME, Context.MODE_PRIVATE);

        int savedDay = sharedPref.getInt(Constants.QUOTA_STORAGE_DAYOFYEAR, 0);
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        if (currentDay == savedDay) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(Constants.QUOTA_STORAGE_VALUE, m_Quota);
            editor.commit();
        }
        else {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(Constants.QUOTA_STORAGE_DAYOFYEAR, currentDay);
            editor.putInt(Constants.QUOTA_STORAGE_VALUE, 0);
            editor.commit();
        }
    }

    public String GetAppInForeground(Context context) {
        String result = null;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (String activeProcess : processInfo.pkgList) {
                    result = activeProcess;
                    return result;
                }
            }
        }

        return result;
    }
}
