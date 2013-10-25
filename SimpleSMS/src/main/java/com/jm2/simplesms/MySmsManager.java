package com.jm2.simplesms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class MySmsManager {
    public static String KEY_ROWID;
    public static String KEY_MESSAGE;
    public static String KEY_SEND_NOTI;
    public static String KEY_PHONE_NUMBER;
    public static String KEY_IS_PINNED;
    public static String KEY_REMAKE;

    public static String KEY_VIBRATION_LENGTH;
    public static String KEY_VIBRATION_REPEAT;

    public static String KEY_IS_SMS;
    public static String KEY_SMS_QUEUE_TIME_TO_END;
    public static String KEY_SMS_QUEUE_TIME;
    public static String KEY_SMS_QUEUE_REPEAT_MILLIS;
    public static String KEY_SMS_QUEUE_REPEATS;
    public static String KEY_SMS_QUEUE_MESSAGE;
    public static String KEY_SMS_QUEUE_PHONE_NUMBER;
    public static String KEY_SMS_QUEUE_UNIQUE_ID;

    public static String KEY_ALARM_TIME;

    Context myContext;
    String tag;
    String sendNotiTag;
    String message;
    String phoneNo;
    int numVibrateLengthMillis;
    int numVibrateRepeatMillis;
    boolean pinned;
    long alarmTimeMillis;
    PendingIntent sentPI;
    PendingIntent deliveredPI;

    Bundle smsBundle;
    DBAdapter myDBAdapter;

    public MySmsManager(Context context, Bundle bundle) {
        myContext = context;
        Resources res = myContext.getResources();
        sendNotiTag = res.getString(R.string.sendNotiTag);

        KEY_ROWID = res.getString(R.string.KEY_ROWID);
        KEY_MESSAGE = res.getString(R.string.KEY_MESSAGE);
        KEY_SEND_NOTI = res.getString(R.string.KEY_SEND_NOTI);
        KEY_PHONE_NUMBER = res.getString(R.string.KEY_PHONE_NUMBER);
        KEY_IS_PINNED = res.getString(R.string.KEY_IS_PINNED);
        KEY_REMAKE = res.getString(R.string.KEY_REMAKE);

        KEY_VIBRATION_LENGTH = res.getString(R.string.KEY_VIBRATION_LENGTH);
        KEY_VIBRATION_REPEAT = res.getString(R.string.KEY_VIBRATION_REPEAT);

        KEY_IS_SMS = res.getString(R.string.KEY_IS_SMS);
        KEY_SMS_QUEUE_TIME_TO_END = res.getString(R.string.KEY_SMS_QUEUE_TIME_TO_END);
        KEY_SMS_QUEUE_TIME = res.getString(R.string.KEY_SMS_QUEUE_TIME);
        KEY_SMS_QUEUE_REPEAT_MILLIS = res.getString(R.string.KEY_SMS_QUEUE_REPEAT_MILLIS);
        KEY_SMS_QUEUE_REPEATS = res.getString(R.string.KEY_SMS_QUEUE_REPEATS);
        KEY_SMS_QUEUE_MESSAGE = res.getString(R.string.KEY_SMS_QUEUE_MESSAGE);
        KEY_SMS_QUEUE_PHONE_NUMBER = res.getString(R.string.KEY_SMS_QUEUE_PHONE_NUMBER);
        KEY_SMS_QUEUE_UNIQUE_ID = res.getString(R.string.KEY_SMS_QUEUE_UNIQUE_ID);

        KEY_ALARM_TIME = res.getString(R.string.KEY_ALARM_TIME);

        // Initialize neccessary instance variables
        smsBundle = bundle;
        message = smsBundle.getString(KEY_MESSAGE);
        phoneNo = smsBundle.getString(KEY_PHONE_NUMBER);
        numVibrateLengthMillis = smsBundle.getInt(KEY_VIBRATION_LENGTH, 0);
        numVibrateRepeatMillis = smsBundle.getInt(KEY_VIBRATION_REPEAT, 0);
        pinned = smsBundle.getBoolean(KEY_IS_PINNED);
        alarmTimeMillis = smsBundle.getLong(KEY_ALARM_TIME, 0);

        myDBAdapter = new DBAdapter(context);
    }

    // Sends an sms message
    public void sendSms() {
        phoneNo = filterPhoneNumber();

        boolean validMessage = checkSmsTextFields();

        /*
        Makes the PendingIntent to set off the first onReceive method.
        getBroadcast is used in place of a constructor,
        and retrieves a PI that will perform a broadcast,
        like calling Context.sendBroadcast.
        */
        Intent sentIntent = new Intent();
        sentIntent.setAction("com.jm2.simplesms.sms_sent");
        sentPI = PendingIntent.getBroadcast(myContext, 0,
                sentIntent, 0);

        //---makes the PendingIntent to set off the second onReceive method.
        Intent deliveredIntent = new Intent();
        deliveredIntent.setAction("com.jm2.simplesms.sms_delivered");
        deliveredPI = PendingIntent.getBroadcast(myContext, 0,
                deliveredIntent, 0);

        /*
        sentPI is broadcasted when the message is sucessfully sent/failed.
        B/c the BroadcastReceiver was registered (in AndroidManifest), and b/c the broadcasted
        Intent in sentPI matches the filter (the action), the BroadcastReceiver receives
        the broadcasted intent from sentPI, and thus its onReceive method is called.

        deliveredPi is is broadcast when the message is delivered to the recipient.
        The raw pdu of the status report is in the extended data ("pdu"). - taken
        from the android developers website
         */

        // If only one message, simply send it
        if (validMessage && message.length() <= 160) {
            sendAndInsertSms();
        }
        // Else if multiple messages, send with different method
        else if (validMessage && message.length() > 160) {
            sendAndInsertMultiPartSms();
        }
    }

    // Send a single SMS message and insert it into the content provider
    protected void sendAndInsertSms() {
        android.telephony.SmsManager sms = android.telephony.SmsManager.getDefault();
        sms.sendTextMessage(phoneNo, null, message, sentPI, deliveredPI);
        ContentValues values = new ContentValues();
        values.put("address", phoneNo);
        values.put("body", message);
        myContext.getContentResolver().insert(Uri.parse("content://sms/sent"), values);
        //unregisterSmsReceivers();
    }

    // Send a multi-part SMS message and insert it into the content provider
    protected void sendAndInsertMultiPartSms() {
        android.telephony.SmsManager sms = android.telephony.SmsManager.getDefault();
        ArrayList<String> messageArrayList = sms.divideMessage(message);
        int numberOfMessages = messageArrayList.size();
        ArrayList<PendingIntent> sentPIArrayList = new ArrayList<PendingIntent>();
        ArrayList<PendingIntent> deliveredPIArrayList = new ArrayList<PendingIntent>();
        for (int i = 0; i < numberOfMessages; i++) {
            sentPIArrayList.add(sentPI);
            deliveredPIArrayList.add(deliveredPI);
        }
        sms.sendMultipartTextMessage(phoneNo, null, messageArrayList,
                sentPIArrayList, deliveredPIArrayList);
        ContentValues values = new ContentValues();
        values.put("address", phoneNo);
        values.put("body", message);
        myContext.getContentResolver().insert(Uri.parse("content://sms/sent"), values);
    }

    // Add the correct tag to the SMS message and send it
    public void sendNotiWithSms() {
        tag = sendNotiTag;

        // Add pinned status to tag
        if (pinned) {
            tag += "p";
        }
        else {
            tag += "c";
        }


        // Enter alarm options. Takes priority
        // over vibrate options.
        if (alarmTimeMillis != 0) {
            Calendar smsCalendar = Calendar.getInstance();
            smsCalendar.setTimeInMillis(alarmTimeMillis);
            int month = smsCalendar.get(Calendar.MONTH) + 1;
            int day = smsCalendar.get(Calendar.DAY_OF_MONTH);
            int hour = smsCalendar.get(Calendar.HOUR_OF_DAY);
            int minute = smsCalendar.get(Calendar.MINUTE);
            tag += "a" + month + "." + day + "." +
                    hour + "." + minute;
        }
        // Add vibrate options to tag (only
        // if no alarm options are entered)
        else if (numVibrateLengthMillis != 0 &&
                numVibrateRepeatMillis != 0) {
            numVibrateRepeatMillis += numVibrateLengthMillis;
            numVibrateRepeatMillis /= 60000;
            tag += "v" + numVibrateRepeatMillis + "." +
                    numVibrateLengthMillis;
        }

        tag += sendNotiTag;
        message += tag;

        sendSms();
    }

    // Checks to make sure there is a phone number and
    // message entered
    public boolean checkSmsTextFields() {
        // If phone number edit text is blank,
        // but user has entered a message, prompt
        // user to enter a recipient
        if (phoneNo.length() == 0 && message.length() > 0) {
            Toast.makeText(myContext,
                    "Please add a recipient",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        // Else if message edit text is blank,
        // but user has entered a phone number,
        // prompt user to enter a message
        else if (phoneNo.length() > 0 && message.length() == 0) {
            Toast.makeText(myContext,
                    "Please add a message",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        // Else if both are blank, prompt user to enter
        // both recipient and message
        else if (phoneNo.length() == 0 && message.length() == 0) {
            Toast.makeText(myContext,
                    "Please enter both phone number and message.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // Filters out all extra symbols from the phone
    // number
    public String filterPhoneNumber() {
        for (int i = 0; i < phoneNo.length(); i++) {
            if (phoneNo.charAt(i) != '+' && phoneNo.charAt(i) != '1' && phoneNo.charAt(i) != '2'
                    && phoneNo.charAt(i) != '3' && phoneNo.charAt(i) != '4'
                    && phoneNo.charAt(i) != '5' && phoneNo.charAt(i) != '6'
                    && phoneNo.charAt(i) != '7' && phoneNo.charAt(i) != '8'
                    && phoneNo.charAt(i) != '9' && phoneNo.charAt(i) != '0') {
                phoneNo = phoneNo.substring(0, i) + phoneNo.substring(i + 1);
                i--;
            }
        }
        return phoneNo;
    }

    // Insert created noti into database
    public void insertSmsIntoDatabase() {

        String[] keys = {
                KEY_ROWID, KEY_MESSAGE, KEY_PHONE_NUMBER, KEY_SEND_NOTI, KEY_REMAKE, KEY_IS_SMS,

                KEY_VIBRATION_LENGTH, KEY_VIBRATION_REPEAT, KEY_ALARM_TIME,

                KEY_SMS_QUEUE_TIME_TO_END, KEY_SMS_QUEUE_TIME, KEY_SMS_QUEUE_REPEAT_MILLIS, KEY_SMS_QUEUE_REPEATS,
                KEY_SMS_QUEUE_MESSAGE, KEY_SMS_QUEUE_PHONE_NUMBER, KEY_SMS_QUEUE_UNIQUE_ID,
        };

        // Tells content provider that this row is an sms
        smsBundle.putBoolean(KEY_IS_SMS, true);
        // This boolean is by default true, and will
        // only be updated to false when all queued sms are deleted
        smsBundle.putBoolean(KEY_REMAKE, true);
        if (smsBundle != null) {
            String[] columns = new String[keys.length];
            String[] values = new String[keys.length];
            for(int i = 0; i < keys.length; i++) {
                columns[i] = keys[i];
                Object tempValue = smsBundle.get(keys[i]);
                if (tempValue != null) {
                    String tempValueString = tempValue.toString();
                    values[i] = tempValueString;
                }
                else {
                    values[i] = "";
                }
            }
            myDBAdapter.open();
            myDBAdapter.insertNoti(columns, values);
            myDBAdapter.close();
        }
    }

    // Cancels the queue sms alarm manager and updates
    // KEY_REMAKE to false
    public void deleteQueueSms() {
        int uniqueId = smsBundle.getInt(KEY_ROWID);
        Intent intent = new Intent(myContext, QueueSmsIntentService.class);
        PendingIntent cancelingPendingIntent =
                PendingIntent.getService(myContext, uniqueId, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmMgr = (AlarmManager) myContext.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.cancel(cancelingPendingIntent);

        String[] columns = {KEY_REMAKE};
        String[] new_values = {"false"};
        updateSmsOneRow(uniqueId, columns, new_values);
    }

    // Updates one row of DBAdater
    public void updateSmsOneRow(int rowId, String[] columns, String[] new_values) {
        myDBAdapter.open();
        myDBAdapter.updateNoti(rowId, columns, new_values);
        myDBAdapter.close();
    }
}
