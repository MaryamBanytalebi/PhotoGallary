package com.example.photogallary.utilities;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.photogallary.PhotoGalleryApplication;
import com.example.photogallary.R;
import com.example.photogallary.activities.PhotoGalleryActivity;
import com.example.photogallary.event.NotificationEvent;
import com.example.photogallary.model.GalleryItem;
import com.example.photogallary.repository.PhotoRepository;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class ServicesUtils {

    public static final int NOTIFICATION_ID = 1;

    public static final String ACTION_PRIVATE_NOTIFICATION =
            "com.example.photogallary.ACTION.PRIVATE_NOTIFICATION";
    public static final String PERMISSION_PRIVATE_NOTIFICATION =
            "com.example.photogallary.PRIVATE";

    public static final String EXTRA_NOTIFICATION_ID = "com.example.photogallary.notificationId";
    public static final String EXTRA_NOTIFICATION = "com.example.photogallary.notification";

    public static void pollAndShowNotification(Context context, String tag){

        String query = QueryPreferences.getSearchQuery(context);

        PhotoRepository repository = new PhotoRepository();
        List<GalleryItem> items = repository.fetchPopularItems();

        if (query == null)
            items = repository.fetchPopularItems();
        else
            items = repository.fetchSearchItems(query);

        if (items == null || items.size() == 0) {
            Log.d(tag, "Items from server not fetched");
            return;
        }

        String serverId = items.get(0).getId();
        String lastSaveId = QueryPreferences.getLastId(context);
        if (! serverId.equals(lastSaveId) ) {
            Log.d(tag,"show notification");
            //show notification
            //send BR Intent insteadof notification.
            sendNotificationEvent(context);
        }
        else
            Log.d(tag,"do nothing");
        QueryPreferences.setLastId(context,serverId);
    }

    private static void sendNotificationEvent(Context context) {

        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0,
                PhotoGalleryActivity.newIntent(context),
                0);
        String channelId = context.getResources().getString(R.string.channel_id);
        Notification notification = new NotificationCompat.Builder(context,channelId)
                .setContentTitle(context.getResources().getString(R.string.new_pictures_title))
                .setContentText(context.getResources().getString(R.string.new_pictures_text))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        NotificationEvent notificationEvent = new NotificationEvent(NOTIFICATION_ID,notification);
        EventBus.getDefault().post(notificationEvent);

       /* Intent intent = new Intent(ACTION_PRIVATE_NOTIFICATION);
        intent.putExtra(EXTRA_NOTIFICATION_ID,NOTIFICATION_ID);
        intent.putExtra(EXTRA_NOTIFICATION,notification);
        context.sendOrderedBroadcast(intent,
                PERMISSION_PRIVATE_NOTIFICATION,
                null,
                null,
                Activity.RESULT_OK,
                null,
                null);*/

    }

    /*private static void creatAndShowNotification(Context context) {
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                0,
                PhotoGalleryActivity.newIntent(context),
                0);
        String channelId = context.getResources().getString(R.string.channel_id);
        Notification notification = new NotificationCompat.Builder(context,channelId)
                .setContentTitle(context.getResources().getString(R.string.new_pictures_title))
                .setContentText(context.getResources().getString(R.string.new_pictures_text))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();


        NotificationManagerCompat notificationManagerCompat =
                NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(NOTIFICATION_ID,notification);
    }*/
}
