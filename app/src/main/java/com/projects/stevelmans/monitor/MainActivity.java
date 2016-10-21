package com.projects.stevelmans.monitor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    AlarmReceiver alarm = new AlarmReceiver();
    CheckBox m_RepeatChkBx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_RepeatChkBx = (CheckBox) findViewById( R.id.checkBox );
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
        Initialise(getBaseContext());
    }

    public void Initialise(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                Constants.MONITORING_ENABLED, Context.MODE_PRIVATE);
        m_RepeatChkBx.setChecked(sharedPref.getBoolean(Constants.MONITORING_ENABLED, false));
    }

    public void SaveSettings(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                Constants.MONITORING_ENABLED, Context.MODE_PRIVATE);
       SharedPreferences.Editor editor = sharedPref.edit();
       editor.putBoolean(Constants.MONITORING_ENABLED, m_RepeatChkBx.isChecked());
       editor.commit();
    }


}
