package com.example.photogallary.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.photogallary.service.PollService;
import com.example.photogallary.utilities.QueryPreferences;

public class BootReceiver extends BroadcastReceiver {

    public static final String TAG = "PhotoGalleryBootReceiver";

    @SuppressLint("LongLogTag")
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "received intent: "+intent.getAction());

        //start alarm if it was schedule

        boolean isAlarmOn = QueryPreferences.isAlarmOn(context);

        if (isAlarmOn){
            Log.d(TAG, " alarm is schedule");

            PollService.scheduleAlarm(context,isAlarmOn);
        }else {
            Log.d(TAG, "there is no alarm schedule");
        }

    }
}