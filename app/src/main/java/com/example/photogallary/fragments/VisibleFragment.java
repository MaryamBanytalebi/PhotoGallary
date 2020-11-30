package com.example.photogallary.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.example.photogallary.PhotoGalleryApplication;
import com.example.photogallary.R;
import com.example.photogallary.event.NotificationEvent;
import com.example.photogallary.receiver.PGNotificationReceiver;
import com.example.photogallary.utilities.ServicesUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class VisibleFragment extends Fragment {

    /*private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(PGNotificationReceiver.TAG,"fragment is visible"+intent);
            Toast.makeText(context,
                    "The app is visible and just receive a notofication event",
                    Toast.LENGTH_SHORT)
                    .show();

            setResultCode(Activity.RESULT_CANCELED);
        }
    };*/

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

        /*IntentFilter intentFilter = new IntentFilter(ServicesUtils.ACTION_PRIVATE_NOTIFICATION);
        getActivity().registerReceiver(mReceiver,
                intentFilter,
                ServicesUtils.PERMISSION_PRIVATE_NOTIFICATION,
                null);*/
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);

        // getActivity().unregisterReceiver(mReceiver);
    }

    @Subscribe(threadMode = ThreadMode.POSTING, priority = 2)
    public void onNotificationEventListener(NotificationEvent notificationEvent){
        String msg = "Application received the notification event";
        Log.d(PhotoGalleryApplication.TAG_EVENT_BUS,msg);

        EventBus.getDefault().cancelEventDelivery(notificationEvent);

    }
}