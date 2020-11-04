package com.example.photogallary.fragments;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
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

        mRepository = new PhotoRepository();

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
}