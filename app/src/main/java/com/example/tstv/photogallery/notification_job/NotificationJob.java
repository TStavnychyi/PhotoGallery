package com.example.tstv.photogallery.notification_job;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;
import com.example.tstv.photogallery.R;
import com.example.tstv.photogallery.activities.PhotoGalleryActivity;
import com.example.tstv.photogallery.data_fetchr.FlickFetchr;
import com.example.tstv.photogallery.model.GalleryItem;
import com.example.tstv.photogallery.preferences.QueryPreferences;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by tstv on 30.06.2017.
 */

public class NotificationJob extends Job {
    static final String TAG = "polling_data_job_tag";

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        Log.e(TAG, "onRunJob");
        if (!QueryPreferences.isAlarmOn(getContext())){
            return Result.FAILURE;
        }

        String query = QueryPreferences.getStoredQuery(getContext());
        String lastResultId = QueryPreferences.getLastResultId(getContext());
        List<GalleryItem> items;

        if (query == null){
            items = new FlickFetchr().fetchRecentPhotos(1);
        }else {
            items = new FlickFetchr().searchPhotos(query, 1);
        }
        if (items.size() == 0){
            return Result.FAILURE;
        }


        String resultId = items.get(0).getId();
        if (resultId.equals(lastResultId)){
            Log.i(TAG, "Got an old result: " + resultId);
        }else {
            Log.i(TAG, "Got a new result: " + resultId);
        }
        QueryPreferences.setLastResultId(getContext(), resultId);

        setupNotification();

        return Result.SUCCESS;
    }

    public static void schedulePeriodic(){
        Log.e(TAG, "schedule");
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        //once a day between 13h - 18h,each day
        long startMs = TimeUnit.MINUTES.toMillis(60 - minute)
                + TimeUnit.HOURS.toMillis((24 - hour) % 24);
        long endMs = startMs + TimeUnit.HOURS.toMillis(5);

        new JobRequest.Builder(NotificationJob.TAG)
                .setExecutionWindow(startMs, endMs)
                .setUpdateCurrent(true)
                .setPersisted(true)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setRequirementsEnforced(true)
                .build()
                .schedule();
    }

    private void setupNotification(){
        Resources resources = getContext().getResources();
        Intent i = PhotoGalleryActivity.newIntent(getContext());
        PendingIntent pi = PendingIntent.getActivity(getContext(), 0, i, 0);

        Notification notification = new NotificationCompat.Builder(getContext())
                .setTicker(resources.getString(R.string.new_pictures_title))
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(resources.getString(R.string.new_pictures_title))
                .setContentText(resources.getString(R.string.new_pictures_text))
                .setContentIntent(pi)
                .build();

        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(getContext());
        notificationManager.notify(0, notification);
    }

}
