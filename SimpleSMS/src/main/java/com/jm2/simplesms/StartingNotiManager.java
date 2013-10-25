package com.jm2.simplesms;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class StartingNotiManager {

    public final static CharSequence startingNoti = "Open Simple SMS";
    public final static CharSequence startingTickerPinned = "Created Pinned Launch Notification";
    public final static CharSequence startingTickerClearable = "Created Clearable Launch Notification";
    public final static CharSequence cancelLaunchNotification = "Deleted Launch Notification";
    final static String KEY_THEMES_ICON_COLOR = "pref_key_themes_icon_color";

    Context context;
    NotificationManager notiManager;
    SharedPreferences sharedPrefs;

    public StartingNotiManager(Context context){
        this.context = context;
        notiManager = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void createStartingNoti(){
        // Prepare intent which is triggered if the
        // notification is selected
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.putExtra("notiMessage", R.string.starting_noti);
        /*
            FLAG_ACTIVITY_SINGLE_TOP - If set, the activity will not be
            launched if it is already running at the top of the history stack.

            FLAG_ACTIVITY_NEW_TASK - a final int, that, if set, this activity
            will become the start of a new task on this history stack.
            If a task is already running for the activity you are now starting,
            then a new activity will not be started; instead, the current task will
            simply be brought to the front of the screen with the state it was last in.

            FLAG_ACTIVITY_REORDER_TO_FRONT - causes the launched activity to be brought to the front
            of its task's history stack if it is already running
         */
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // See if user wants color icon or white icon
        int iconDrawable = R.drawable.ic_launcher_simple_sms;

        Notification noti = new Notification.Builder(context)
                .setSmallIcon(iconDrawable)
                .setContentTitle(startingNoti)
                .setContentText("Simple SMS")
                .setTicker(startingTickerPinned)
                .setPriority(-2147483648)
                .setContentIntent(pIntent).getNotification();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        noti.flags |= Notification.FLAG_ONGOING_EVENT;

        notificationManager.notify(33333, noti);
    }

    public void cancelStartingNoti(){
        notiManager.cancel(33333);
        Toast toast = Toast.makeText(context, cancelLaunchNotification, Toast.LENGTH_SHORT);
        toast.show();
    }

    public void createClearableStartingNoti(){
        // Prepare intent which is triggered if the
        // notification is selected
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.putExtra("notiMessage", R.string.starting_noti);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // See if user wants color icon or white icon
        int iconDrawable = R.drawable.ic_launcher_simple_sms;

        Notification noti = new Notification.Builder(context)
                .setSmallIcon(iconDrawable)
                .setContentTitle(startingNoti)
                .setContentText("OmNotify")
                .setTicker(startingTickerClearable)
                .setPriority(2147483647)
                .setContentIntent(pIntent).getNotification();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        notificationManager.notify(33333, noti);
    }

}
