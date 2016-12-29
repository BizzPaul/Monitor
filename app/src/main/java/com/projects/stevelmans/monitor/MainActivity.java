package com.projects.stevelmans.monitor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    AlarmReceiver alarm = new AlarmReceiver();
    CheckBox m_RepeatChkBx;
    SeekBar m_QuotaBar;
    EditText m_Password;
    EditText m_QuotaText;
    EditText m_MinutesLeftText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        m_RepeatChkBx = (CheckBox) findViewById( R.id.checkBox );
        m_QuotaBar = (SeekBar) findViewById(R.id.seekBar);
        m_Password = (EditText) findViewById(R.id.passwordText);
        m_Password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                attemptLogin();
                return true;
            }
        });
        m_QuotaText = (EditText) findViewById(R.id.quotaText);
        m_MinutesLeftText = (EditText) findViewById(R.id.remainingText);
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

        m_QuotaBar.setOnSeekBarChangeListener(new QuotaBarListener());
    }

    protected void onResume () {
        super.onResume();
        SharedPreferences sharedPref = getBaseContext().getSharedPreferences(Constants.QUOTA_STORAGE_NAME, Context.MODE_PRIVATE);
        int quota = sharedPref.getInt(Constants.QUOTA_STORAGE_VALUE, 0);
        sharedPref = getBaseContext().getSharedPreferences(Constants.QUOTA_STORAGE_MAX, Context.MODE_PRIVATE);
        int maxQuota = sharedPref.getInt(Constants.QUOTA_STORAGE_MAX, Constants.DEFAULT_MAX_QUOTA);
        m_MinutesLeftText.setText("Minutes left " + String.valueOf(maxQuota - quota));

        m_Password.setText(null);
        m_RepeatChkBx.setEnabled(false);
        m_QuotaBar.setEnabled(false);
        m_QuotaText.setEnabled(false);
    }

    private class QuotaBarListener implements SeekBar.OnSeekBarChangeListener {
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
            m_QuotaText.setText(String.valueOf(progress));
        }

        public void onStartTrackingTouch(SeekBar seekBar) {}

        public void onStopTrackingTouch(SeekBar seekBar) {
            SaveSettings(getBaseContext());
        }
    }

    public void Initialise(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                Constants.MONITORING_ENABLED, Context.MODE_PRIVATE);
        m_RepeatChkBx.setChecked(sharedPref.getBoolean(Constants.MONITORING_ENABLED, false));
        sharedPref = context.getSharedPreferences(
                Constants.QUOTA_STORAGE_MAX, Context.MODE_PRIVATE);
        int maxQuota = sharedPref.getInt(Constants.QUOTA_STORAGE_MAX, Constants.DEFAULT_MAX_QUOTA);
        m_QuotaBar.setProgress(maxQuota);
        m_QuotaText.setText(String.valueOf(maxQuota));
        sharedPref = getBaseContext().getSharedPreferences(Constants.QUOTA_STORAGE_NAME, Context.MODE_PRIVATE);
        int quota = sharedPref.getInt(Constants.QUOTA_STORAGE_VALUE, 0);
        sharedPref = context.getSharedPreferences(
                Constants.PASSWORD_STORAGE_NAME, Context.MODE_PRIVATE);
        m_Password.setText(sharedPref.getString(Constants.PASSWORD_STORAGE_NAME, Constants.DEFAULT_PASSWORD));
        sharedPref = context.getSharedPreferences(
                Constants.QUOTA_STORAGE_VALUE, Context.MODE_PRIVATE);
        m_MinutesLeftText.setText("Minutes left: " + String.valueOf(maxQuota - quota));
    }

    public void SaveSettings(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                Constants.MONITORING_ENABLED, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(Constants.MONITORING_ENABLED, m_RepeatChkBx.isChecked());
        editor.apply();
        sharedPref = context.getSharedPreferences(
                Constants.QUOTA_STORAGE_MAX, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        int test = m_QuotaBar.getProgress();
        editor.putInt(Constants.QUOTA_STORAGE_MAX, m_QuotaBar.getProgress());
        editor.apply();
        sharedPref = context.getSharedPreferences(
                Constants.PASSWORD_STORAGE_NAME, Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        editor.putString(Constants.PASSWORD_STORAGE_NAME, m_Password.getText().toString());
        editor.commit();
    }

    private void attemptLogin() {
        // Store values at the time of the login attempt.
        String enteredPassword = m_Password.getText().toString();

        SharedPreferences sharedPref = getBaseContext().getSharedPreferences(
                Constants.PASSWORD, Context.MODE_PRIVATE);
        String password = sharedPref.getString(Constants.PASSWORD, Constants.DEFAULT_PASSWORD);

        if (enteredPassword.equals(Constants.DEFAULT_PASSWORD))
        {
            Tracker.Locked = false;
            m_RepeatChkBx.setEnabled(true);
            m_QuotaBar.setEnabled(true);
            m_QuotaText.setEnabled(true);
        }
    }
}
