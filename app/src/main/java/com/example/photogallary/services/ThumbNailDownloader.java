package com.example.photogallary.services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.photogallary.fragments.PhotoGalleryFragment;
import com.example.photogallary.netWork.FlickrFetcher;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ThumbNailDownloader<T> extends HandlerThread {

    public static final String TAG = "ThumbNailDownloader";
    public static final int WHAT_THUMBNAIL_DOWNLOAD = 1;

    private Handler mHandlerRequest;
    private Handler mHandlerResponse;
    private Map<T,String> mRequestMap = new HashMap<>();

    private ThumbNailDownloaderListener mListener;

    /*public Handler getHandlerResponse() {
        return mHandlerResponse;
    }

    public void setHandlerResponse(Handler handlerResponse) {
        mHandlerResponse = handlerResponse;
    }
*/


    public ThumbNailDownloaderListener getListener() {
        return mListener;
    }

    public void setListener(ThumbNailDownloaderListener listener) {
        mListener = listener;
    }

    public ThumbNailDownloader(Handler uiHandler) {
        super(TAG);

        mHandlerResponse = uiHandler;

    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();

        mHandlerRequest = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                try {
                    handlerDownloader(msg);
                } catch (IOException e) {
                    Log.e(TAG,e.getMessage(),e);
                }
            }
        };
    }

    private void handlerDownloader(@NonNull Message msg) throws IOException {
        if (msg.what == WHAT_THUMBNAIL_DOWNLOAD) {
            if (msg.obj == null)
                return;


            T target = (T) msg.obj;
            String url = mRequestMap.get(target);
            FlickrFetcher flickrFetcher = new FlickrFetcher();
            byte[] bitmapBytes = flickrFetcher.getUrlBytes(url);

            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);

            mHandlerResponse.post(new Runnable() {
                @Override
                public void run() {

                    if (mRequestMap.get(target) != url)
                        return;

                    if (target instanceof PhotoGalleryFragment.PhotoHolder) {
                        PhotoGalleryFragment.PhotoHolder photoHolder = (PhotoGalleryFragment.PhotoHolder) target;
                        photoHolder.bindBitmap(bitmap);
                    }

                }
            });

        }
    }

    public void queueThumbNail(T target ,String url){

        mRequestMap.put(target,url);
        //creat message and send it to looper to put in queue
        Message message = mHandlerRequest.obtainMessage(WHAT_THUMBNAIL_DOWNLOAD,target);
        message.sendToTarget();
    }

    public interface ThumbNailDownloaderListener<T>{

        void onThumbnailDownloaded(T target, Bitmap bitmap);
    }
}
