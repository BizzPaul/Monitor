package com.projects.stevelmans.monitor;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    private EditText mPasswordView;
    private View mLoginFormView;
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

        mLoginFormView = findViewById(R.id.login_form);

        Tracker.LoginRunning = true;
        Tracker.Locked = true;
    }

    private class QuotaBarListener implements SeekBar.OnSeekBarChangeListener {
        int progressChanged = 0;

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            m_QuotaText.setText(progress);
        }

        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        public void onStopTrackingTouch(SeekBar seekBar) {
            SetExtraTime(seekBar.getProgress());
        }
    }

    private void SetExtraTime(int value) {
        Intent intent = new Intent(this, SchedulingService.class);
        intent.setAction(Constants.ACTIONS.RELEASE);
        intent.putExtra(Constants.QUOTA_EXTRA_VALUE, value);
        startService(intent);
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
            Intent resetIntent = new Intent(this, SchedulingService.class);
            resetIntent.setAction(Constants.ACTIONS.RELEASE);
            startService(resetIntent);
        }
    }
}

