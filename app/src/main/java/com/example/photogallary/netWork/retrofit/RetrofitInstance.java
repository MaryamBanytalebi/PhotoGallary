package com.example.photogallary.netWork.retrofit;

import com.example.photogallary.model.GalleryItem;
import com.example.photogallary.netWork.NetworkParams;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static RetrofitInstance sInstance;
    private Retrofit mRetrofit;

    public static RetrofitInstance getInstance(){

        if (sInstance == null)
        sInstance = new RetrofitInstance();
        return sInstance;
    }

    private static Converter.Factory createGsonConverter() {
        Object typeAdapter = new GetGalleryItemDeserializer();
        Type type = new TypeToken<List<GalleryItem>>(){}.getType();

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(type, new GetGalleryItemDeserializer());
        Gson gson = gsonBuilder.create();

        return GsonConverterFactory.create(gson);
    }

    public Retrofit getRetrofit() {
        return mRetrofit;
    }

    private RetrofitInstance(){
        mRetrofit = new Retrofit.Builder()
                .baseUrl(NetworkParams.BASE_URL)
                //htis is for automatically
                //.addConverterFactory(GsonConverterFactory.create())
                //this for customize
                .addConverterFactory(createGsonConverter())
                .build();

    }
}
