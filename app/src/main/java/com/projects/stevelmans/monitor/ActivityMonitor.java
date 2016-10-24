package com.projects.stevelmans.monitor;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by stevelmans on 17/10/16.
 * Monitors the activity and records the spent time
 */

class ActivityMonitor {
    ActivityMonitor() {
        m_ExcludedPrograms = new ArrayList<>();

        m_ExcludedPrograms.add(Constants.EXCLUDE_SELF);
        m_ExcludedPrograms.add(Constants.EXCLUDE_DESKTOP);
    }

    private int m_Quota;
    private int m_MaxQuota;
    private List<String> m_ExcludedPrograms;

    boolean QuotaReached(Context context) {
        boolean result = false;

        if (IsMonitoredAppRunning(context)) {
            m_Quota++;
            SaveQuota(context);

            if (m_Quota >= m_MaxQuota) {
                result = true;
            }
        }

        return result;
    }

    private boolean IsMonitoredAppRunning(Context context) {
        boolean result = true;

        if (m_ExcludedPrograms.contains(GetAppInForeground(context))) {
            result = false;
        }

        return result;
    }

    int QuotaLeft() {
        return m_MaxQuota - m_Quota;
    }

    void AdjustQuota(Context context, int value) {
        m_Quota = value;
        SaveQuota(context);
    }
    void ReadQuota(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                Constants.QUOTA_STORAGE_NAME, Context.MODE_PRIVATE);
        m_Quota = sharedPref.getInt(Constants.QUOTA_STORAGE_VALUE, 0);
        sharedPref = context.getSharedPreferences(
                Constants.QUOTA_STORAGE_MAX, Context.MODE_PRIVATE);
        m_MaxQuota = sharedPref.getInt(Constants.QUOTA_STORAGE_MAX, Constants.DEFAULT_MAX_QUOTA);
    }

    void ResetQuota(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                Constants.QUOTA_STORAGE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(Constants.QUOTA_STORAGE_VALUE, 0);
        editor.apply();
        m_Quota = sharedPref.getInt(Constants.QUOTA_STORAGE_VALUE, 0);
    }

    private void SaveQuota(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                Constants.QUOTA_STORAGE_NAME, Context.MODE_PRIVATE);

        int savedDay = sharedPref.getInt(Constants.QUOTA_STORAGE_DAYOFYEAR, 0);
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        if (currentDay == savedDay) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(Constants.QUOTA_STORAGE_VALUE, m_Quota);
            editor.apply();
        }
        else {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(Constants.QUOTA_STORAGE_DAYOFYEAR, currentDay);
            editor.putInt(Constants.QUOTA_STORAGE_VALUE, 0);
            editor.apply();
        }
    }

    private String GetAppInForeground(Context context) {
        String result = null;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (processInfo.pkgList.length > 0) {
                    result = processInfo.pkgList[0];
                }
            }
        }

        return result;
    }
}
