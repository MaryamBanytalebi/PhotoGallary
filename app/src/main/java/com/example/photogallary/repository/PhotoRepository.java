package com.example.photogallary.repository;

import android.util.Log;

import com.example.photogallary.model.GalleryItem;
import com.example.photogallary.netWork.FlickrFetcher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhotoRepository {

    public static final String TAG = "photRepository";
    private FlickrFetcher mFetcher;

    public PhotoRepository() {
        mFetcher= new FlickrFetcher();
    }

    public List<GalleryItem> fetchItems() {

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
