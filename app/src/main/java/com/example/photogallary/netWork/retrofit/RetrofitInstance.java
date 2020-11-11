package com.example.photogallary.netWork.retrofit;

import com.example.photogallary.netWork.NetworkParams;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    public static Retrofit getInstance(){
        return new Retrofit.Builder()
                .baseUrl(NetworkParams.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
