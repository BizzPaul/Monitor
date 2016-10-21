package com.projects.stevelmans.monitor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {
    AlarmReceiver alarm = new AlarmReceiver();
    CheckBox m_RepeatChkBx;
    SeekBar m_QuotaBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_RepeatChkBx = (CheckBox) findViewById( R.id.checkBox );
        m_QuotaBar = (SeekBar) findViewById(R.id.seek_bar);
        Initialise(getBaseContext());

        m_RepeatChkBx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked )
                {
                    alarm.setAlarm(getBaseContext());
                }
                else {
                    alarm.cancelAlarm(getBaseContext());
                }
                SaveSettings(getBaseContext());
            }
        });
    }

    public void Initialise(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                Constants.MONITORING_ENABLED, Context.MODE_PRIVATE);
        m_RepeatChkBx.setChecked(sharedPref.getBoolean(Constants.MONITORING_ENABLED, false));
        sharedPref = context.getSharedPreferences(
                Constants.MAX_QUOTA_NAME, Context.MODE_PRIVATE);
        m_QuotaBar.setProgress(sharedPref.getInt(Constants.MAX_QUOTA_NAME, Constants.MAX_QUOTA));
    }

    public void SaveSettings(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                Constants.MONITORING_ENABLED, Context.MODE_PRIVATE);
       SharedPreferences.Editor editor = sharedPref.edit();
       editor.putBoolean(Constants.MONITORING_ENABLED, m_RepeatChkBx.isChecked());
       editor.apply();
        sharedPref = context.getSharedPreferences(
                Constants.MAX_QUOTA_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putInt(Constants.MAX_QUOTA_NAME, m_QuotaBar.getProgress());
        editor.apply();
    }


}
