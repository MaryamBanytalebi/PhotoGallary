package com.example.photogallary.fragments;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;

import com.example.photogallary.databinding.FragmentPhotoGalleryBinding;
import com.example.photogallary.model.GalleryItem;
import com.example.photogallary.R;
import com.example.photogallary.repository.PhotoRepository;
import com.example.photogallary.viewModel.PhotoGalleryViewModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PhotoGalleryFragment extends Fragment {

    public static final int SPAN_COUNT = 2;
    public static final String TAG = "PGF";

    private PhotoRepository mRepository;
    private Handler mHandlerUI;
    private PhotoGalleryViewModel mViewModel;

    //private ThumbNailDownloader<PhotoHolder> mThumbNailDownloader;
    private LruCache<String, Bitmap> memoryCache;
    private FragmentPhotoGalleryBinding mBinding;


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
        setHasOptionsMenu(true);

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

       /* FlickrTask flickrTask = new FlickrTask();
        flickrTask.execute();*/

        mRepository.fetchPopularItemsAsync(new PhotoRepository.Callbacks() {
            @Override
            public void onItemResponse(List<GalleryItem> items) {
                setupAdapter(items);
            }
        });

       /* Handler uiHandler = new Handler();

        mThumbNailDownloader = new ThumbNailDownloader(uiHandler);
        mThumbNailDownloader.start();
        mThumbNailDownloader.getLooper();
        mThumbNailDownloader.setListener(new ThumbNailDownloader.ThumbNailDownloaderListener<PhotoHolder>() {
            @Override
            public void onThumbnailDownloaded(PhotoHolder target, Bitmap bitmap) {

                target.bindBitmap(bitmap);
            }
        });*/

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

   /* @Override
    public void onDestroy() {
        super.onDestroy();

        mThumbNailDownloader.quit();
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_photo_gallery,
                container,
                false);
        initViews();
        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.fragment_photo_gallery,menu);
        MenuItem searchMenuItem = menu.findItem(R.id.menu_item_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        setSearchViewListeners(searchView);

        MenuItem toggleMenuItem = menu.findItem(R.id.menu_item_poll_toggling);
        if (mViewModel.isTaskScheduled()){
            toggleMenuItem.setTitle("stop polling");
        }
        else toggleMenuItem.setTitle("start polling");
    }

    private void setSearchViewListeners(SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //QueryPreferences.setTestQuery(getContext(),true);
                mViewModel.fetchPopularItemsAsync(query);
                mViewModel.fetchSearchItemsAsync(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = mViewModel.getQueryFromPreferences();
                if (query != null)
                    //false beacause of dont submit again just show the history of search
                    searchView.setQuery(query,false);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_clear:
                //null or remove
                mViewModel.setQueryInPreferences(null);
                mViewModel.fetchItems();
                return true;
            case R.id.menu_item_poll_toggling:

                //transfer to viewmodel
                /*boolean isOn = PollService.isAlarmSet(getActivity());
                PollService.scheduleAlarm(getActivity(), !isOn);*/
                mViewModel.togglePolling();
                getActivity().invalidateOptionsMenu();//build option menu again
                return true;
            default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void initViews() {
        mBinding.recyclerViewPhotoGallery.setLayoutManager(new GridLayoutManager(getContext(), SPAN_COUNT));
    }

    private void setLiveDataObservers(){
        mViewModel.getsearchItemsLiveData().observe(this, new Observer<List<GalleryItem>>() {
            @Override
            public void onChanged(List<GalleryItem> items) {
                setupAdapter(items);
            }
        });

        mViewModel.getPopularItemsLiveData().observe(this, new Observer<List<GalleryItem>>() {
            @Override
            public void onChanged(List<GalleryItem> items) {
                setupAdapter(items);
            }
        });

    }

    private void setupAdapter(List<GalleryItem> items) {
        PhotoAdapter adapter = new PhotoAdapter(mViewModel);
        mBinding.recyclerViewPhotoGallery.setAdapter(adapter);
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
            //mThumbNailDownloader.queueThumbNail(this,item.getUrl());

            //use picasso
            Picasso.get()
                    .load(item.getUrl())
                    //.placeholder(R.mipmap.ic_android_placeholder)
                    .into(mImageViewItem);

        }

       /* public void loadBitmap(int resId, ImageView imageView) {
            final String imageKey = String.valueOf(resId);

            final Bitmap bitmap = getBitmapFromMemCache(imageKey);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            } else {
                //imageView.setImageResource(R.mipmap.ic_android_placeholder);
                BitmapWorkerTask task = new BitmapWorkerTask(imageView);
                task.execute(resId);
            }
        }*/
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

        private List<GalleryItem> mItems;

        public List<GalleryItem> getItems() {
            return mItems;
        }

        public void setItems(List<GalleryItem> items) {
            mItems = items;
        }

        public PhotoAdapter(PhotoGalleryViewModel viewModel) {
            mViewModel = viewModel;
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

    // retrofit handle  thread so we do not need AsynTask!
    /*private class FlickrTask extends AsyncTask<Void,Void,List<GalleryItem>>{

        //this method runs on background thread
        @Override
        protected List<GalleryItem> doInBackground(Void... voids) {

            List<GalleryItem> items = mRepository.fetchPopularItems();

            return items;
        }

        //this method runs on background thread
        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            super.onPostExecute(items);

            setupAdapter(items);
        }
    }*/
}