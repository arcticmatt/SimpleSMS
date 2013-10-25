package com.jm2.simplesms;

import android.app.IntentService;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

public class QueueSmsIntentService extends IntentService {
    public static String KEY_SMS_QUEUE_REPEATS;
    public static String KEY_SMS_QUEUE_TIME_TO_END;
    public static String KEY_SEND_NOTI;

    public static String KEY_QUEUE_BUNDLE;

    Bundle queueBundle;

    /**
     * A constructor is required, and must call the super IntentService(String)
     * constructor with a name for the worker thread.
     */
    public QueueSmsIntentService() {
        super("QueueSmsIntentService");
    }

    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        Resources res = this.getResources();

        KEY_SMS_QUEUE_REPEATS = res.getString(R.string.KEY_SMS_QUEUE_REPEATS);
        KEY_SMS_QUEUE_TIME_TO_END = res.getString(R.string.KEY_SMS_QUEUE_TIME_TO_END);
        KEY_SEND_NOTI = res.getString(R.string.KEY_SEND_NOTI);

        KEY_QUEUE_BUNDLE = res.getString(R.string.KEY_QUEUE_BUNDLE);

        // Get the bundle from the intent
        queueBundle = intent.getBundleExtra(KEY_QUEUE_BUNDLE);

        if (queueBundle != null) {
            boolean sendNoti = queueBundle.getBoolean(KEY_SEND_NOTI, false);
            boolean repeat = queueBundle.getBoolean(KEY_SMS_QUEUE_REPEATS);
            long timeToEnd = queueBundle.getLong(KEY_SMS_QUEUE_TIME_TO_END);

            // If not a send noti, use sendSms() method
            if (!sendNoti) {
                /*** If a repeat SMS and the current time is before
                the timeToEnd, send the message ***/
                if (repeat && System.currentTimeMillis() < timeToEnd) {
                    MySmsManager mySmsManager = new MySmsManager(this, queueBundle);
                    mySmsManager.sendSms();
                }
                // Else if nto a repeat SMS, send the message and cancel
                // the alarm
                else if (!repeat) {
                    MySmsManager mySmsManager = new MySmsManager(this, queueBundle);
                    mySmsManager.sendSms();

                    mySmsManager.deleteQueueSms();
                }
                // Else (if repeat SMS and current time is after
                // the timeToEnd, cancel the alarm
                else {
                    MySmsManager mySmsManager = new MySmsManager(this, queueBundle);
                    mySmsManager.deleteQueueSms();
                }
            }
            /*** Else (if a send noti), use sendNotiWithSms() method ***/
            else {
                // If noti has repeat queues and the current time
                // is before the timeToEnd, send the noti
                if (repeat && System.currentTimeMillis() < timeToEnd) {
                    MySmsManager mySmsManager = new MySmsManager(this, queueBundle);
                    mySmsManager.sendNotiWithSms();
                }
                // Else if it is not repeat, send the noti and cancel the
                // alarm
                else if (!repeat) {
                    MySmsManager mySmsManager = new MySmsManager(this, queueBundle);
                    mySmsManager.sendNotiWithSms();

                    mySmsManager.deleteQueueSms();
                }
                // Else (if it is repeat and past timeToEnd) cancel
                // the alarm
                else {
                    MySmsManager mySmsManager = new MySmsManager(this, queueBundle);
                    mySmsManager.deleteQueueSms();
                }
            }
        }
    }
}
