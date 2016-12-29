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
        m_ExcludedPrograms.add(Constants.EXCLUDE_ANDROID);
        m_ExcludedPrograms.add(Constants.EXCLUDE_ANDROID_LAUNCHER);
        m_ExcludedPrograms.add(Constants.EXCLUDE_ANDROID_PROVIDERS);
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

        String appName = FormatName(GetAppInForeground(context));
        if (m_ExcludedPrograms.contains(appName)) {
            result = false;
        }

        return result;
    }

    private String FormatName(String name) {
        String result = name;

        if (name != null)
        {
            int index = nthIndexOf(name, '.', 3);

            if (index > 0) {
                result = name.substring(0, index);
            }
        }

        return result;
    }

    public static int nthIndexOf(String text, char needle, int n)
    {
        for (int i = 0; i < text.length(); i++)
        {
            if (text.charAt(i) == needle)
            {
                n--;
                if (n == 0)
                {
                    return i;
                }
            }
        }
        return -1;
    }

    int QuotaLeft() {
        return m_MaxQuota - m_Quota;
    }

    void AdjustQuota(Context context, int value) {
        m_Quota = m_Quota - value;
        if (m_Quota < 0)
        {
            m_Quota = 0;
        }
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
            editor.commit();
        }
        else {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(Constants.QUOTA_STORAGE_DAYOFYEAR, currentDay);
            editor.putInt(Constants.QUOTA_STORAGE_VALUE, 0);
            editor.commit();
        }
    }

    private String GetAppInForeground(Context context) {
        String result = null;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                if (processInfo.pkgList.length > 0) {
                    if (!m_ExcludedPrograms.contains(FormatName(processInfo.pkgList[0])))
                    {
                        result = processInfo.pkgList[0];
                        break;
                    }
                }
            }
        }

        return result;
    }
}
