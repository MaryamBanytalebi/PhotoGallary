package com.example.photogallary.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.Random;

public class MyService extends Service {

    private final Random mGenerator = new Random();
    private MyBinder mMyBinder = new MyBinder();
    private static final String EXTRA_SERVICE_NUMBER = "org.maktab.photogallery.ServiceNumber";


    public static Intent newIntent(Context context,int number){
        Intent intent = new Intent(context, MyService.class);
        intent.putExtra(EXTRA_SERVICE_NUMBER, number);

        return intent;
    }

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        /*// TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");*/
        return mMyBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("MyService", "service started with:" + intent);
        return super.onStartCommand(intent, flags, startId);
    }

    public int getRandomNumber() {
        return mGenerator.nextInt(100);
    }

    public class MyBinder extends Binder {

        public MyService getMyService(){
            return MyService.this;
        }
    }
}