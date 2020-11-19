package com.example.photogallary.utilities;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.photogallary.R;
import com.example.photogallary.activities.PhotoGalleryActivity;
import com.example.photogallary.model.GalleryItem;
import com.example.photogallary.repository.PhotoRepository;

import java.util.List;

import static android.content.ContentValues.TAG;

public class ServicesUtils {

    public static final int NOTIFICATION_ID = 1;

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
            setAndShowNotification(context);
        }
        else
            Log.d(tag,"do nothing");
        QueryPreferences.setLastId(context,serverId);
    }

    private static void setAndShowNotification(Context context) {
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
    }
}
