package com.example.tstv.photogallery.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.tstv.photogallery.fragments.PhotoGalleryFragment;

public class PhotoGalleryActivity extends SingleFragmentActivity {

    public static Intent newIntent(Context context){
        return new Intent(context, PhotoGalleryActivity.class);
    }

    @Override
    protected Fragment createFragment() {
        Log.e("TAG", "PhotoGalleryActivity createFragment()");
        return PhotoGalleryFragment.newInstance();


    }
}
