package com.example.photogallary.netWork.retrofit;

import com.example.photogallary.model.GalleryItem;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface FlickrService {

    @GET(".")
    Call<List<GalleryItem>> listItems(@QueryMap Map<String, String> options);

}
