package com.example.tstv.photogallery.notification_job;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

/**
 * Created by tstv on 30.06.2017.
 */

public class NotificationJobCreator implements JobCreator {

    @Override
    public Job create(String tag) {
        switch (tag) {
            case NotificationJob.TAG:
                return new NotificationJob();
            default:
                return null;
        }

    }
}
