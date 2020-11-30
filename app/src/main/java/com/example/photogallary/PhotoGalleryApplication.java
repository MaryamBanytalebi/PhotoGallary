package com.example.photogallary;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;

import com.example.photogallary.event.NotificationEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.function.Consumer;

public class PhotoGalleryApplication extends Application {

    public static final String TAG_EVENT_BUS = "EventBus";
    private static final String TAG = "PhotoGalleryApplication";
    private Context mContext = this;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreat");
        creatNotificationChannel();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        EventBus.getDefault().unregister(this);
    }

    private void creatNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){//api +26

            String channelId = getString(R.string.channel_id);
            String name = getString(R.string.channel_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            String description = getString((R.string.channel_description));
            long[] pattern = {100,200,300,400,500,400,300,200,400};
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();


            NotificationChannel channel = new NotificationChannel(channelId,name,importance);
            channel.setDescription(description);
            channel.setSound(alarmSound,audioAttributes);
            channel.setVibrationPattern(pattern);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 1)
    public void onNotificationEvent(NotificationEvent notificationEvent){

        String msg = "The fragment received the notification event";
        Log.d(TAG_EVENT_BUS,msg);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat
                .from(this);
        notificationManagerCompat.notify(notificationEvent.getNotificationId(),
                notificationEvent.getNotification());
    }
}

