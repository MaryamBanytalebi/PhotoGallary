package com.example.photogallary.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.photogallary.fragments.PhotoPageFragment;

public class PhotoPageActivity extends SingleFragmentActivity {

    public static final String EXTRA_PHOTO_PAGE_URI = "com.example.photoGallery.photoPageUri";

    public static Intent newIntent(Context context , Uri uri){
        Intent intent = new Intent(context,PhotoGalleryActivity.class);
        intent.putExtra(EXTRA_PHOTO_PAGE_URI, uri);
        return intent;
    }

    @Override
    public Fragment createFragment() {
        Uri photoPageUri = getIntent().getParcelableExtra(EXTRA_PHOTO_PAGE_URI);
        return PhotoPageFragment.newInstance(photoPageUri);
    }

}