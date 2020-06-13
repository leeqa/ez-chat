package com.abidingtech.rednewsapp.services;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.abidingtech.rednewsapp.R;
import com.abidingtech.rednewsapp.callback.ProgressCallback;

public class ForegroundService extends Service {
    private static final String LOG_TAG = "ForegroundService";
    NotificationManagerCompat notificationManager;
    final int progressMax = 100;
    // This is the object that receives interactions from clients.
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
       public ForegroundService getService() {
            return ForegroundService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
        Log.e(LOG_TAG, "Received Start Foreground Intent ");
        notificationManager = NotificationManagerCompat.from(this);
        final NotificationCompat.Builder notification = new NotificationCompat.Builder(this, "channelDownload")
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("Download")
                .setContentText("Download in progress")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setProgress(progressMax, 0, false);

        notificationManager.notify(2, notification.build());


        new Upload(this, intent.getStringExtra("fileName"), Utils.URL + intent.getStringExtra("uploadPath")).multipartFileUpload(new ProgressCallback<String>() {
            @Override
            public void onCompleted(String s) {
                Log.e(LOG_TAG, "Received Stop Foreground Intent");
                stopForeground(true);
                stopSelf();

                notification.setContentText("Download finished")
                        .setProgress(0, 0, false)
                        .setOngoing(false);
                notificationManager.notify(2, notification.build());
                onBind(intent);
            }

            @Override
            public void onProgress(double progress) {
//                int progress = 0;
                notification.setProgress(progressMax, (int) progress, false);
                notificationManager.notify(2, notification.build());
                Log.e(LOG_TAG, "progress => " + progress);

            }

            @Override
            public void onError(String msg) {

            }
        });
        return START_STICKY;
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(LOG_TAG, "In onDestroy");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }



}