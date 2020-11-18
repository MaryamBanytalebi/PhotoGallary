package com.example.photogallary.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import android.util.TimeUtils;

import androidx.annotation.LongDef;
import androidx.annotation.RequiresApi;

import com.example.photogallary.model.GalleryItem;
import com.example.photogallary.repository.PhotoRepository;
import com.example.photogallary.utilities.QueryPreferences;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class PollService extends IntentService {

    public static final String TAG = "PollService";

    public static Intent newIntent(Context context){
        return new Intent(context,PollService.class);
    }

    public PollService() {
        super(TAG);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent" + intent);
        if (!isNetWorkAvailableAndConnected()) {
            Log.d(TAG, "NetWor not available");
            return;
        }
        String query = QueryPreferences.getSearchQuery(this);

        PhotoRepository repository = new PhotoRepository();
        List<GalleryItem> items = repository.fetchPopularItems();

        if (query == null)
            items = repository.fetchPopularItems();
        else
            items = repository.fetchSearchItems(query);

        if (items == null || items.size() == 0) {
            Log.d(TAG, "Items from server not fetched");
            return;
        }

        String serverId = items.get(0).getId();
        String lastSaveId = QueryPreferences.getLastId(this);
        if (! serverId.equals(lastSaveId) ) {
            Log.d(TAG,"show notification");
            //show notification
        }
        else
            Log.d(TAG,"od nothing");
        QueryPreferences.setLastId(this,serverId);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean isNetWorkAvailableAndConnected(){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getActiveNetwork() != null
                && connectivityManager.getActiveNetworkInfo().isConnected())
            return true;

        return false;
    }

    public static void scheduleAlarm(Context context){
        Log.d(TAG,"scheduleAlarm");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = newIntent(context);
        PendingIntent operation = PendingIntent.getService(context,0,intent,0);
        context.startService(intent);

        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime(),
                TimeUnit.MINUTES.toMillis(1),
                operation);

        //for sample
        /*alarmManager.set(AlarmManager.RTC,
                System.currentTimeMillis(),
                operation);*/
    }
}