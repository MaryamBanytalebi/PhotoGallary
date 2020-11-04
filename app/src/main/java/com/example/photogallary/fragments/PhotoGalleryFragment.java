package com.example.photogallary.fragments;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.photogallary.model.GalleryItem;
import com.example.photogallary.R;
import com.example.photogallary.netWork.FlickrFetcher;
import com.example.photogallary.repository.PhotoRepository;
import com.example.photogallary.services.ThumbNailDownloader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryFragment extends Fragment {

    public static final int SPAN_COUNT = 2;
    public static final String TAG = "PGF";

    private RecyclerView mRecyclerView;
    private PhotoRepository mRepository;
    private Handler mHandlerUI;
    private ThumbNailDownloader<PhotoHolder> mThumbNailDownloader;
    private LruCache<String, Bitmap> memoryCache;


    public PhotoGalleryFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static PhotoGalleryFragment newInstance() {
        PhotoGalleryFragment fragment = new PhotoGalleryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        mRepository = new PhotoRepository();

        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };

        FlickrTask flickrTask = new FlickrTask();
        flickrTask.execute();

        Handler uiHandler = new Handler();

        ThumbNailDownloader thumbNailDownloader = new ThumbNailDownloader(uiHandler);
        thumbNailDownloader.start();
        mThumbNailDownloader.getLooper();

        mThumbNailDownloader.setListener(new ThumbNailDownloader.ThumbNailDownloaderListener<PhotoHolder>() {
            @Override
            public void onThumbnailDownloaded(PhotoHolder target, Bitmap bitmap) {

                target.bindBitmap(bitmap);
            }
        });

        /*Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                FlickrFetcher flickrFetcher = new FlickrFetcher();
                try {
                    String response = flickrFetcher.getUrlString("https://www.digikala.com/");
                    Log.d(TAG,response);
                } catch (IOException e) {

                    Log.e(TAG,e.getMessage(),e);
                }

            }
        });
        thread.start();*/
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            memoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return memoryCache.get(key);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mThumbNailDownloader.quit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        findViews(view);
        initViews();
        return view;
    }

    private void initViews() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), SPAN_COUNT));
    }

    private void findViews(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view_photo_gallery);
    }

    private void setupAdapter(List<GalleryItem> items) {
        PhotoAdapter adapter = new PhotoAdapter(new ArrayList<>());
        mRecyclerView.setAdapter(adapter);
    }

    public class PhotoHolder extends RecyclerView.ViewHolder {

        private ImageView mImageViewItem;
        private GalleryItem mItem;

        public PhotoHolder(@NonNull View itemView) {
            super(itemView);

            mImageViewItem = itemView.findViewById(R.id.item_image_view);

        }

        public void bindGalleryItem(GalleryItem item) {
            mItem = item;

            //item.getUrl();
            //it is so heavy!
            //mImageViewItem.setImageBitmap(bitmap);
            //gueue the message for download
            mThumbNailDownloader.queueThumbNail(this,item.getUrl());


        }

        public void bindBitmap(Bitmap bitmap){

            mImageViewItem.setImageBitmap(bitmap);

        }

        public void loadBitmap(int resId, ImageView imageView) {
            final String imageKey = String.valueOf(resId);

            final Bitmap bitmap = getBitmapFromMemCache(imageKey);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                //imageView.setImageResource(R.mipmap.ic_android_placeholder);
                BitmapWorkerTask task = new BitmapWorkerTask(imageView);
                task.execute(resId);
            }
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<GalleryItem> mItems;

        public List<GalleryItem> getItems() {
            return mItems;
        }

        public void setItems(List<GalleryItem> items) {
            mItems = items;
        }

        public PhotoAdapter(List<GalleryItem> items) {
            mItems = items;
        }

        @NonNull
        @Override
        public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.list_item_photo_gallery,
                    parent,false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PhotoHolder holder, int position) {
            holder.bindGalleryItem(mItems.get(position));
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }
    }

    private class FlickrTask extends AsyncTask<Void,Void,List<GalleryItem>>{

        @Override
        protected List<GalleryItem> doInBackground(Void... voids) {

            List<GalleryItem> items = mRepository.fetchItems();

            return items;
        }

       /* @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }*/

        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            super.onPostExecute(items);

            setupAdapter(items);
        }
    }

    class BitmapWorkerTask extends AsyncTask<Integer, Void, Bitmap> {

        //        private final WeakReference<ImageView> imageViewReference;
        private ImageView mImageView;
        private int data = 0;
        public BitmapWorkerTask(ImageView imageView) {
            // Use a WeakReference to ensure the ImageView can be garbage collected
//            imageView = new WeakReference<ImageView>(imageView);
            mImageView = imageView;
        }

        // Decode image in background.

        @Override
        protected Bitmap doInBackground(Integer... params) {
            final Bitmap bitmap = decodeSampledBitmapFromResource(
                    getActivity().getResources(), params[0], 100, 100);
            addBitmapToMemoryCache(String.valueOf(params[0]), bitmap);
            return bitmap;
        }
        // Once complete, see if ImageView is still around and set bitmap.

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (mImageView != null && bitmap != null) {
                final ImageView imageView = mImageView;
                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
    }
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}