package com.projects.stevelmans.monitor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    private EditText mPasswordView;
    SeekBar m_QuotaBar;
    EditText m_QuotaText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                attemptLogin();
                return true;
            }
        });
        m_QuotaBar = (SeekBar) findViewById(R.id.seekBarExtraTime);
        m_QuotaText = (EditText) findViewById(R.id.extraMinutesText);
        m_QuotaBar.setOnSeekBarChangeListener(new QuotaBarListener());

        Tracker.LoginRunning = true;
        Tracker.Locked = true;
    }

    private class QuotaBarListener implements SeekBar.OnSeekBarChangeListener {
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            m_QuotaText.setText(progress);
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {}
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Tracker.LoginRunning = false;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        // Store values at the time of the login attempt.
        String enteredPassword = mPasswordView.getText().toString();

        SharedPreferences sharedPref = getBaseContext().getSharedPreferences(
                Constants.PASSWORD, Context.MODE_PRIVATE);
        String password = sharedPref.getString(Constants.PASSWORD, "");

        if (enteredPassword.equals(password))
        {
            Tracker.Locked = false;
            Intent intent = new Intent(this, SchedulingService.class);
            intent.setAction(Constants.ACTIONS.RELEASE);
            intent.putExtra(Constants.QUOTA_EXTRA_VALUE, m_QuotaBar.getProgress());
            startService(intent);
        }
    }
}

