package com.example.photogallary.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.photogallary.model.GalleryItem;
import com.example.photogallary.netWork.FlickrFetcher;
import com.example.photogallary.netWork.NetworkParams;
import com.example.photogallary.netWork.retrofit.FlickrService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotoRepository {

    public static final String TAG = "photRepository";
    private final MutableLiveData<List<GalleryItem>> mSearchItemsLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<GalleryItem>> mPopularItemsLiveData = new MutableLiveData<>();

    private FlickrFetcher mFetcher;
    private FlickrService mFlickrService;

    public PhotoRepository() {
        mFetcher= new FlickrFetcher();
    }

    public MutableLiveData<List<GalleryItem>> getSearchItemsLiveData() {
        return mSearchItemsLiveData;
    }

    public MutableLiveData<List<GalleryItem>> getPopularItemsLiveData() {
        return mPopularItemsLiveData;
    }

    public List<GalleryItem> fetchPopularItems() {

        String url = mFetcher.getRecentUrl();

        try {
            String response = mFetcher.getUrlString(url);
            Log.d(TAG, "response" + response);

            JSONObject bodyObject = new JSONObject(response);
            List<GalleryItem> items = parsejson(bodyObject);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();


            return items;
        } catch (IOException | JSONException e) {
            Log.e(TAG, e.getMessage(), e);

            return null;
        }
    }

    public void fetchPopularItemsAsync(String query){
        Call<List<GalleryItem>> call =
                mFlickrService.listItems(NetworkParams.getSearchOptions(query));

        call.enqueue(new Callback<List<GalleryItem>>() {

            //this run on main thread
            @Override
            public void onResponse(Call<List<GalleryItem>> call, Response<List<GalleryItem>> response) {
                List<GalleryItem> items = response.body();

                //update adapter of recyclerview
                mSearchItemsLiveData.setValue(items);
            }

            //this run on main thread
            @Override
            public void onFailure(Call<List<GalleryItem>> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t);
            }
        });
    }

    public List<GalleryItem> fetchSearchItems(String query) {

        String url = mFetcher.getRecentUrl();

        try {
            String response = mFetcher.getUrlString(url);
            Log.d(TAG, "response" + response);

            JSONObject bodyObject = new JSONObject(response);
            List<GalleryItem> items = parsejson(bodyObject);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();


            return items;
        } catch (IOException | JSONException e) {
            Log.e(TAG, e.getMessage(), e);

            return null;
        }
    }



    public void fetchSearchItemsAsync(String query){
        Call<List<GalleryItem>> call =
                mFlickrService.listItems(NetworkParams.getSearchOptions(query));

        call.enqueue(new Callback<List<GalleryItem>>() {

            //this run on main thread
            @Override
            public void onResponse(Call<List<GalleryItem>> call, Response<List<GalleryItem>> response) {
                List<GalleryItem> items = response.body();

                //update adapter of recyclerview
                mSearchItemsLiveData.setValue(items);
            }

            //this run on main thread
            @Override
            public void onFailure(Call<List<GalleryItem>> call, Throwable t) {
                Log.e(TAG, t.getMessage(), t);
            }
        });
    }


    private List<GalleryItem> parsejson(JSONObject bodyObject) throws JSONException {
        List<GalleryItem> items = new ArrayList<>();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();


        JSONObject photosObject = bodyObject.getJSONObject("photos");
        JSONArray photoArray = photosObject.getJSONArray("photo");

        for (int i = 0; i < photoArray.length(); i++) {

            //Aray of object---> getJsonObject
            JSONObject photoObject = photoArray.getJSONObject(i);

            if (!photoObject.has("url_s"))
                continue;

            /*String id = photoObject.getString("id");
            String title = photoObject.getString("title");
            String url_s = photoObject.getString("url_s");

            GalleryItem item = new GalleryItem(id,title,url_s);*/
            GalleryItem item = gson.fromJson(String.valueOf(photoObject),GalleryItem.class);

            items.add(item);

        }
        return items;
    }
}
