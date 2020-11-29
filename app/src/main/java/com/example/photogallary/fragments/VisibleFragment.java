package com.example.photogallary.fragments;

import android.annotation.SuppressLint;
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
import com.example.photogallary.receiver.PGNotificationReceiver;
import com.example.photogallary.utilities.ServicesUtils;

public class VisibleFragment extends Fragment {

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(PGNotificationReceiver.TAG,"fragment is visible"+intent);
            Toast.makeText(context,
                    "The app is visible and just receive a notofication event",
                    Toast.LENGTH_SHORT)
                    .show();
        }
    };

    @Override
    public void onStart() {
        super.onStart();

        IntentFilter intentFilter = new IntentFilter(ServicesUtils.ACTION_PRIVATE_NOTIFICATION);
        getActivity().registerReceiver(mReceiver,
                intentFilter,
                ServicesUtils.PERMISSION_PRIVATE_NOTIFICATION,
                null);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(mReceiver);
    }
}