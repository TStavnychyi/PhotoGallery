package com.example.tstv.photogallery.activities;

import android.app.Application;
import android.util.Log;

import com.evernote.android.job.JobManager;
import com.example.tstv.photogallery.notification_job.NotificationJobCreator;

/**
 * Created by tstv on 30.06.2017.
 */

public class MainApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("TAG", "onCreateMainApp");
        JobManager.create(this).addJobCreator(new NotificationJobCreator());
    }
}
