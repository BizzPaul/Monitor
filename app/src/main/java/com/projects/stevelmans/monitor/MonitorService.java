package com.projects.stevelmans.monitor;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by stevelmans on 17/10/16.
 */

public class MonitorService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MonitorService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        /*
     * Creates a new Intent containing a Uri object
     * BROADCAST_ACTION is a custom Intent action
     */
        String status = "";
        Intent localIntent;
        localIntent = new Intent(Constants.BROADCAST_ACTION)
                // Puts the status into the Intent
                .putExtra(Constants.EXTENDED_DATA_STATUS, status);
        // Broadcasts the Intent to receivers in this app.
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

    }

}
