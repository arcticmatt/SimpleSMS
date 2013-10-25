package com.jm2.simplesms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

public class QueueSmsAlarm {
    public static String KEY_ROWID;

    public static String KEY_SMS_QUEUE_TIME;
    public static String KEY_SMS_QUEUE_REPEATS;
    public static String KEY_SMS_QUEUE_REPEAT_MILLIS;
    public static String KEY_SMS_QUEUE_TIME_TO_END;
    public static String KEY_SMS_QUEUE_REMAKE_FROM_REBOOT;

    public static String KEY_QUEUE_BUNDLE;

    // This constructor creates the alarm with all the extras
    // contained in the bundle
    public QueueSmsAlarm(Context context, Bundle queueBundle){
        Resources res = context.getResources();

        KEY_ROWID = res.getString(R.string.KEY_ROWID);

        KEY_SMS_QUEUE_TIME = res.getString(R.string.KEY_SMS_QUEUE_TIME);
        KEY_SMS_QUEUE_REPEATS = res.getString(R.string.KEY_SMS_QUEUE_REPEATS);
        KEY_SMS_QUEUE_REPEAT_MILLIS = res.getString(R.string.KEY_SMS_QUEUE_REPEAT_MILLIS);
        KEY_SMS_QUEUE_TIME_TO_END = res.getString(R.string.KEY_SMS_QUEUE_TIME_TO_END);
        KEY_SMS_QUEUE_REMAKE_FROM_REBOOT = res.getString(R.string.KEY_SMS_QUEUE_REMAKE_FROM_REBOOT);

        KEY_QUEUE_BUNDLE = res.getString(R.string.KEY_QUEUE_BUNDLE);

        // Get all the extras from the bundle
        int uniqueId = queueBundle.getInt(KEY_ROWID, (int) System.currentTimeMillis());
        long repeatMillis = queueBundle.getLong(KEY_SMS_QUEUE_REPEAT_MILLIS, 0);
        boolean repeat = queueBundle.getBoolean(KEY_SMS_QUEUE_REPEATS, false);
        long queueTime = queueBundle.getLong(KEY_SMS_QUEUE_TIME);
        long timeToEnd = queueBundle.getLong(KEY_SMS_QUEUE_TIME_TO_END, 0);
        boolean remakeFromReboot = queueBundle.getBoolean(KEY_SMS_QUEUE_REMAKE_FROM_REBOOT, false);

        // If remaking (from boot) a repeat sms, set the queue time
        // to the next time, so that an sms is not immediately sent on boot
        if(repeat && remakeFromReboot) {
            while (queueTime < System.currentTimeMillis() && queueTime+repeatMillis < timeToEnd) {
                queueTime += repeatMillis;
            }
            queueBundle.putBoolean(KEY_SMS_QUEUE_REMAKE_FROM_REBOOT, false);
        }

        // Get an alarm manager that will start a service. An alarm manager
        // is used because it provides a CPU wake lock, enabling the alarm
        // to go off and vibrations to go off even when phone is asleep
        AlarmManager alarmMgr =
                (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, QueueSmsIntentService.class);
        intent.putExtra(KEY_QUEUE_BUNDLE, queueBundle);
        // FLAG_UPDATE_CURRENT - used in conjunction with ClickNoti.java, b/c
        // when a noti is saved in ClickNoti.java with reset vibration options,
        // a PI with the same uniqueId as the one before is created. However, unless
        // this flag is set, the extras will not be updated
        PendingIntent pendingIntent =
                PendingIntent.getService(context, uniqueId, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        // RTC_WAKEUP means this alarm runs on System.currentTimeMillis.
        // The second parameter is when to first run the alarm
        if (!repeat) {
            alarmMgr.set(AlarmManager.RTC_WAKEUP, queueTime, pendingIntent);
        } else {
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, queueTime, repeatMillis,
                    pendingIntent);
        }
    }
}
