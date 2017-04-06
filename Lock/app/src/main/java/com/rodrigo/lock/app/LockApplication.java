package com.rodrigo.lock.app;

import android.app.Application;
import android.content.Context;

import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

/**
 * Created by Rodrigo on 19/11/2016.
 */

public class LockApplication  extends Application {

    // private static final String TAG = Tags.getTag(SpringApplication.class);
    private static Context context;

    //TODO: configure AndroLog
    @Override
    public void onCreate() {
        super.onCreate();
        LockApplication.context = getApplicationContext();


    }



    public static Context getAppContext() {
        return LockApplication.context;
    }


}
