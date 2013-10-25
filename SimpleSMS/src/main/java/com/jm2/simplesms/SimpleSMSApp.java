package com.jm2.simplesms;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

/**
 * Created by jpugliesi12 on 6/25/13.
 */
public class SimpleSMSApp extends Application {

    public static Context context;
    SMSCursorAdapter messageBoxAdapter;

    StartingNotiManager startingNotiManager;


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SimpleSMSApp.context = getApplicationContext();
        messageBoxAdapter = new SMSCursorAdapter(getApplicationContext(), null);

        startingNotiManager = new StartingNotiManager(context);
    }

    public SMSCursorAdapter getMessageBoxAdapter(){

        return messageBoxAdapter;

    }

    public StartingNotiManager getStartingNotiManager(){

        return startingNotiManager;
    }


}
