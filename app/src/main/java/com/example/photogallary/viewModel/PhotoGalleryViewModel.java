package com.example.photogallary.viewModel;

import android.app.Application;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.photogallary.model.GalleryItem;
import com.example.photogallary.repository.PhotoRepository;
import com.example.photogallary.utilities.QueryPreferences;

import java.util.List;

public class PhotoGalleryViewModel extends AndroidViewModel {

    private PhotoRepository mRepository;
    private final LiveData<List<GalleryItem>> msearchItemsLiveData;

    public PhotoGalleryViewModel(@NonNull Application application) {
        super(application);
        mRepository = new PhotoRepository();
        //beacase write observer
        msearchItemsLiveData = mRepository.getSearchItemsLiveData();
    }

    public LiveData<List<GalleryItem>> getsearchItemsLiveData() {
        return msearchItemsLiveData;
    }

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

    public void fetchItems() {
        String query = QueryPreferences.getSearchQuery(getApplication());
        fetchSearchItemsAsync(query);
        //TODO
        /*if (query != null) {
            fetchSearchItemsAsync(query);
        } else {
            fetchPopularItemsAsync();
        }*/
    }
}
