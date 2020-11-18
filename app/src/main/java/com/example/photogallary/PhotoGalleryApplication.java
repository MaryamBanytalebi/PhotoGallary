package com.example.photogallary;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

public class PhotoGalleryApplication extends Application {

    private static final String TAG = "PhotoGalleryApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreat");
        creatNotificationChannel();
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
}
