package com.example.photogallary.service;

import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.app.job.JobWorkItem;
import android.content.ComponentName;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.photogallary.utilities.ServicesUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class PollJobService extends JobService {

    private static final String TAG = "PollJobService";
    public static final int JOB_ID = 1;
    private PollTask mPollTask;

    public PollJobService() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        mPollTask = new PollTask(params);
        mPollTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        mPollTask.cancel(true);
        return false;
    }

    public static void scheduleJob(Context context,boolean isOn){
        Log.d(TAG,"scheduleJob");
        JobScheduler jobScheduler =
                (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);

        ComponentName pollJobServiceName = new ComponentName(context,PollService.class);
        JobInfo jobInfo = new JobInfo.Builder(JOB_ID, pollJobServiceName)
                .setPeriodic(TimeUnit.MINUTES.toMillis(15))
                .setPersisted(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .build();

        if (isOn) {
            Log.d(TAG,"schedule on");
            jobScheduler.schedule(jobInfo);
        }
        else {
            Log.d(TAG,"schedule off");
            jobScheduler.cancel(JOB_ID);

        }
    }

    public static boolean isJobSchedule(Context context){
        JobScheduler jobScheduler =
                (JobScheduler) context.getSystemService(JOB_SCHEDULER_SERVICE);

        for (JobInfo jobinfo : jobScheduler.getAllPendingJobs()) {
            if (jobinfo.getId() == JOB_ID)
                return true;
        }
        return false;

    }

    private class PollTask extends AsyncTask<Void,Void,Void>{

        private JobParameters mParameters;

        public PollTask(JobParameters parameters) {
            mParameters = parameters;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            ServicesUtils.pollAndShowNotification(PollJobService.this,TAG);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            jobFinished(mParameters,false);
        }
    }
}