package com.example.photogallary.receiver;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import com.example.photogallary.utilities.ServicesUtils;

import static com.example.photogallary.utilities.ServicesUtils.EXTRA_NOTIFICATION;

public class PGNotificationReceiver extends BroadcastReceiver {

    public static final String TAG = "PGNotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "received intent:" + intent);

        int notificationId = intent.getIntExtra(ServicesUtils.EXTRA_NOTIFICATION_ID,0);
        Notification notification = intent.getParcelableExtra(ServicesUtils.EXTRA_NOTIFICATION);

        NotificationManagerCompat notificationManagerCompat =
                NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(notificationId,notification);
    }
}