package com.abidingtech.rednewsapp.services;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

//import com.abidingtech.rednewsapp.activity.ConnectivityError;
import com.droidnet.DroidListener;
import com.droidnet.DroidNet;

import java.util.Locale;


public class MyApplication extends Application implements DroidListener, Application.ActivityLifecycleCallbacks {
    public DroidNet mDroidNet;
    //    Activity activity;
    static MyApplication _this;
    static boolean isActive;
    boolean isPause;
    boolean isConnected = true;
    SharedPreferences preferences;
    public static int userId;
    public static final String UPDATE_OBJECT_EXTRA = "updateData";

    public static int getUserId() {
        return userId;
    }

    public static MyApplication getInstance() {
        return _this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _this = this;
        DroidNet.init(this);
        registerActivityLifecycleCallbacks(this);

        mDroidNet = DroidNet.getInstance();
//        mDroidNet.removeAllInternetConnectivityChangeListeners();


        mDroidNet.addInternetConnectivityListener(this);

    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
/*        mDroidNet = DroidNet.getInstance();
//        mDroidNet.removeAllInternetConnectivityChangeListeners();
        mDroidNet.addInternetConnectivityListener(this);*/
    }

    @Override
    public void onActivityStarted(Activity activitys) {
//        this.activity = activitys;

//        mDroidNet.removeAllInternetConnectivityChangeListeners();


    }

    @Override
    public void onActivityResumed(Activity activity) {
        isPause = false;
        manageConnectivity();

        Log.e("qwertyui", "onActivityResumed: " + activity.getLocalClassName());

//        mDroidNet.addInternetConnectivityListener(this);

//        mDroidNet.removeAllInternetConnectivityChangeListeners();
//        mDroidNet.addInternetConnectivityListener(this);
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.e("qwertyui", "onActivityPaused: ");

        isPause = true;

//        mDroidNet.removeAllInternetConnectivityChangeListeners();

    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }


    @Override
    public void onActivityDestroyed(Activity activity) {

    }


    @Override
    public void onInternetConnectivityChanged(boolean isConnected) {
        this.isConnected = isConnected;
        Log.e("qwertyui", "onInternetConnectivityChanged: " + isConnected);
        manageConnectivity();
    }

    private void manageConnectivity() {
        if (isConnected) {
//            if (this.activity instanceof ConnectivityError) {
//                activity.finish();
//                isActive = false;
//            }
        } else {
//            if (!isActive && !isPause) {
//                isActive = true;
//
//                Intent intent = new Intent(this, ConnectivityError.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
        }
    }


    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.e("qwertyui", "onLowMemory: ");
        mDroidNet.removeAllInternetConnectivityChangeListeners();

    }

    public static void setLocale(Context context, String language) {

        Locale locale = new Locale("ur");
        Configuration config = new Configuration(context.getResources().getConfiguration());
        Locale.setDefault(locale);
        config.setLocale(locale);
        config.setLayoutDirection(locale);
        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());
    }

}
