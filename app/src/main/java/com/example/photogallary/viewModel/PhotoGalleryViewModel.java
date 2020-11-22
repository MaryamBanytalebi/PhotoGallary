package com.example.photogallary.viewModel;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.photogallary.activities.PhotoPageActivity;
import com.example.photogallary.model.GalleryItem;
import com.example.photogallary.netWork.NetworkParams;
import com.example.photogallary.repository.PhotoRepository;
import com.example.photogallary.utilities.QueryPreferences;
import com.example.photogallary.work.PollWorker;

import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryViewModel extends AndroidViewModel {

    public static final String TAG = "PGVM";
    private PhotoRepository mRepository;
    private final LiveData<List<GalleryItem>> msearchItemsLiveData;
    private final LiveData<List<GalleryItem>> mpopularItemsLiveData;

    public PhotoGalleryViewModel(@NonNull Application application) {
        super(application);
        mRepository = new PhotoRepository();
        //beacase write observer
        msearchItemsLiveData = mRepository.getSearchItemsLiveData();
        mpopularItemsLiveData = mRepository.getPopularItemsLiveData();
    }

    public LiveData<List<GalleryItem>> getsearchItemsLiveData() {
        return msearchItemsLiveData;
    }
    public LiveData<List<GalleryItem>> getPopularItemsLiveData() {
        return mpopularItemsLiveData; }

    /*public PhotoGalleryViewModel() {
        mRepository = new PhotoRepository();
        //beacase write observer
        msearchItemsLiveData = mRepository.getSearchItemsLiveData();
    }*/

    //start to dawonload
    public void fetchSearchItemsAsync(String query){
        mRepository.fetchSearchItemsAsync(query);

    }

    public void setQueryInPreferences(String query){

        QueryPreferences.setPrefSearchQuery(getApplication(),query);
    }

    public String getQueryFromPreferences(){

        return QueryPreferences.getSearchQuery(getApplication());
    }

    public void fetchPopularItemsAsync(String query){
        mRepository.fetchPopularItemsAsync();
    }

    public void setPopularQueryInPreferences(String query){

        QueryPreferences.setPrefSearchQuery(getApplication(),query);
    }

    public String getPopularQueryFromPreferences(){

        return QueryPreferences.getSearchQuery(getApplication());
    }

    public void fetchItems() {
        String query = QueryPreferences.getSearchQuery(getApplication());
        fetchSearchItemsAsync(query);
        //TODO
        if (query != null) {
            fetchSearchItemsAsync(query);
        } else {
            fetchPopularItemsAsync(query);
        }
    }

    public List<GalleryItem> getCurrentItems(){

        String query = QueryPreferences.getSearchQuery(getApplication());
        if (query != null && msearchItemsLiveData.getValue() != null) {

            return msearchItemsLiveData.getValue();
        }
        else if (query == null && mpopularItemsLiveData.getValue() != null){
            return mpopularItemsLiveData.getValue();
        }
        else {
            return new ArrayList<>();
        }
    }

    public void togglePolling(){

        boolean isOn = PollWorker.isWorkEnqueued(getApplication());
        PollWorker.enqueueWork(getApplication() , !isOn);
    }

    public boolean isTaskScheduled() {

        return PollWorker.isWorkEnqueued(getApplication());
    }

    public void onImageClicked(int position){

        GalleryItem item = getCurrentItems().get(position);
        Uri photoPageUri = NetworkParams.getPhotoPageUri(item);
        Log.d(TAG,photoPageUri.toString());

        //explicit Intent
        Intent intent = PhotoPageActivity.newIntent(getApplication(),photoPageUri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(intent);

        //Implicit Intent
        /*Intent intent = new Intent(Intent.ACTION_VIEW, photoPageUri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplication().startActivity(intent);*/

    }
}
