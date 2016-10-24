package com.projects.stevelmans.monitor;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

/**
 * This {@code IntentService} does the app's actual work.
 * {@code SampleAlarmReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class SchedulingService extends IntentService {
    public SchedulingService() {
        super("SchedulingService");
        m_Monitor = new ActivityMonitor();
    }

    // An ID used to post the notification.
    public static final int NOTIFICATION_ID = 1;

    public ActivityMonitor m_Monitor;
    private Intent m_LogingIntent;

    @Override
    protected void onHandleIntent(Intent intent) {
        if ( intent.getAction() == (Constants.ACTIONS.RELEASE)) {
            int minutes = intent.getIntExtra(Constants.QUOTA_EXTRA_VALUE, 0);
            m_Monitor.AdjustQuota(getBaseContext(), minutes);
        }
        else {
            m_Monitor.ReadQuota(getBaseContext());
            if (m_Monitor.QuotaReached(getBaseContext())) {
                sendNotification(getString(R.string.quota_reached_message), 0);

                // start obnoxious activity
                m_LogingIntent = new Intent(this, LoginActivity.class);
                m_LogingIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                handler.postDelayed(runnable, 100);
            } else {
                String message;
                int minutesLeft = m_Monitor.QuotaLeft();
                if (minutesLeft == 1) {
                    message = getString(R.string.quota_available_minute_message);
                } else {
                    message = String.format(getString(R.string.quote_available_message), minutesLeft);
                }
                sendNotification(message, minutesLeft);
            }

            // Release the wake lock provided by the BroadcastReceiver.
            AlarmReceiver.completeWakefulIntent(intent);
            // END_INCLUDE(service_onhandle)
        }
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // restart in 1000 milliseconds
            handler.postDelayed(this, 1000);

            if (!Tracker.LoginRunning) {
                // check if not allowed app is running
                m_Monitor.ReadQuota(getBaseContext());
                if (m_Monitor.QuotaReached(getBaseContext())) {
                    startActivity(m_LogingIntent);
                }
            }
        }
    };



    // Post a notification
    private void sendNotification(String msg, int minutesLeft) {
        NotificationManager mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.lighthouse)
                        .setContentTitle(getString(R.string.notification_title))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

        if (minutesLeft <= Constants.VIBRATION_THRESHOLD) {
            Vibrator v = (Vibrator) getBaseContext().getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            int time = (Constants.VIBRATION_THRESHOLD - minutesLeft) * 100;
            v.vibrate(time);
        }
    }
}
