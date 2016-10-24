package com.projects.stevelmans.monitor;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by stevelmans on 17/10/16.
 */

class ActivityMonitor {
    ActivityMonitor() {
        m_ExcludedPrograms = new ArrayList<>();

        m_ExcludedPrograms.add(Constants.EXCLUDE_SELF);
        m_ExcludedPrograms.add(Constants.EXCLUDE_DESKTOP);
    }

    private int m_Quota;
    private List<String> m_ExcludedPrograms;

    boolean QuotaReached(Context context) {
        boolean result = false;

        if (IsMonitoredAppRunning(context)) {
            m_Quota++;
            SaveQuota(context);

            if (m_Quota >= Constants.DEFAULT_MAX_QUOTA) {
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
        return Constants.DEFAULT_MAX_QUOTA - m_Quota;
    }

    void AdjustQuota(Context context, int extraMinutes) {
        m_Quota = extraMinutes;
        SaveQuota(context);
    }
    void ReadQuota(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                Constants.QUOTA_STORAGE_NAME, Context.MODE_PRIVATE);
        m_Quota = sharedPref.getInt(Constants.QUOTA_STORAGE_VALUE, 0);
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
                for (String activeProcess : processInfo.pkgList) {
                    result = activeProcess;
                    return result;
                }
            }
        }

        return result;
    }
}
