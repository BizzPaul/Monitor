package com.projects.stevelmans.monitor;

/**
 * Created by stevelmans on 18/10/16.
 */

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Natarajan on 15-06-2015.
 */
public class HUD extends Service {

    LinearLayout oView;

    @Override
    public IBinder onBind(Intent i) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        oView = new LinearLayout(this);
        //oView.setBackgroundColor(0x88ff0000); // The translucent red color
        oView.setBackgroundResource(R.mipmap.mrbean);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                0 | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        //params.height = 100;
        wm.addView(oView, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(oView!=null){
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.removeView(oView);
        }
    }
}


