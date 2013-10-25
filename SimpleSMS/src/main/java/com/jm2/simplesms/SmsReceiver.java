package com.jm2.simplesms;

/**
 * Created by matt on 6/24/13.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.util.Calendar;


/**
 * Created by arcticmatt on 5/23/13.
 */
/*
There are two ways to make a broadcast receiver known to the system.
One is to declare it in the manifest file with the <receiver> element,
which is how this SmsReceiver was registered. The other is to create the
receiver dynamically in code and register it with the
Context.registerReceiver() method.

This receiver reacts on the Intent "android.provider.Telephony.SMS_RECEIVED."
This is shown in the <intent-filter> element in the <receiver> element.
The "android.provider.Telephony.SMS_RECEIVED" Intent is fired by the system
when there is an Incoming SMS. Permission must be granted to receive SMSs,
which was done in the manifest file.
 */
public class SmsReceiver extends BroadcastReceiver {
    public static String KEY_ROWID;
    public static String KEY_MESSAGE;
    public static String KEY_SNOOZE_UNIQUE_ID;
    public static String KEY_IS_PINNED;

    public static String KEY_VIBRATION_LENGTH;
    public static String KEY_VIBRATION_REPEAT;
    public static String KEY_VIBRATE_OPTIONS_ENTERED;
    public static String KEY_VIBRATE_NONE;

    public static String KEY_NOTI_HAS_ALARM;
    public static String KEY_ALARM_TIME;
    public static String KEY_ALARM_NONE;

    public static String KEY_QUEUE_BUNDLE;

    String toastMessage;
    Context myContext;
    int tagStart;
    String receivedMessage;
    String notiMessage;
    int numVibrateRepeatMillis;
    int numVibrateLengthMillis;
    long alarmTimeMillis;
    String tagMessage;
    int tagMessageLength;
    String name;
    int uniqueId;
    int snoozeUniqueId;

    char sendNotiTag;

    SharedPreferences sharedPrefs;

    final static String KEY_RECEIVE_SMS_TOAST = "pref_key_receive_sms_toast_on_off";
    final static String KEY_BLOCK_RECEIVED_ALARMS = "pref_key_block_received_alarms";
    final static String KEY_BLOCK_RECEIVED_VIBRATIONS = "pref_key_block_received_vibrations";


    /*
    This is the method that is called when the SMS Receiver is
    "invoked" by the System. This happens because we exposed this class
    in the AndroidManifest.xml.

    The SMS message is contained and attached to the Intent object (the
    second parameter in the onReceive() method) via a Bundle object.
    The messages are stored in an Object array in the PDU format (see
    the method "convertPDUToString" below
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Resources res = context.getResources();

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        KEY_ROWID = res.getString(R.string.KEY_ROWID);
        KEY_MESSAGE = res.getString(R.string.KEY_MESSAGE);
        KEY_SNOOZE_UNIQUE_ID = res.getString(R.string.KEY_SNOOZE_UNIQUE_ID);
        KEY_IS_PINNED = res.getString(R.string.KEY_IS_PINNED);

        KEY_VIBRATION_LENGTH = res.getString(R.string.KEY_VIBRATION_LENGTH);
        KEY_VIBRATION_REPEAT = res.getString(R.string.KEY_VIBRATION_REPEAT);
        KEY_VIBRATE_OPTIONS_ENTERED = res.getString(R.string.KEY_VIBRATE_OPTIONS_ENTERED);
        KEY_VIBRATE_NONE = res.getString(R.string.KEY_VIBRATE_NONE);

        KEY_NOTI_HAS_ALARM = res.getString(R.string.KEY_NOTI_HAS_ALARM);
        KEY_ALARM_TIME = res.getString(R.string.KEY_ALARM_TIME);
        KEY_ALARM_NONE = res.getString(R.string.KEY_ALARM_NONE);

        KEY_QUEUE_BUNDLE = res.getString(R.string.KEY_QUEUE_BUNDLE);

        //---get the SMS message passed in---
        /*Since the SMS message is contained in a Bundle object,
        we must get that Bundle from the intent. The method
        getExtras() returns a Bundle.

        A Bundle is generally used for passing data between
        various Activities of android. It can hold different types
        of values and pass to the new activity.
        */
        myContext = context;
        sendNotiTag = res.getString(R.string.sendNotiTag).charAt(0);
        Bundle bundle = intent.getExtras();

        //---if the Bundle object is not empty, then we will
        //---convert it to a string
        if (bundle != null) {
            receivedMessage = convertBundleToString(bundle, context);
            //---display the new SMS message---
            if (sharedPrefs.getBoolean(KEY_RECEIVE_SMS_TOAST, true)) {
                Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show();
            }

            // Get the noti tag
            tagMessage = getSmsTag();
            tagMessageLength = tagMessage.length();

            /*Only create the noti stuff if the tag is correct (if
            the tag is not correct, tagStart will stay at its initialized
            value of -1)*/
            if (tagStart != -1 && tagMessage.length() > 2 &&
                    (tagMessage.charAt(1) == 'c' ||
                            tagMessage.charAt(1) == 'p')) {

                createNotiFromSms();
            }
        }
    }

    protected void createNotiFromSms() {
        // Set up unique ids for the notis to use (usually
        // set up in MainActivity
        uniqueId = (int) System.currentTimeMillis();
        snoozeUniqueId = uniqueId + 1;

        notiMessage = receivedMessage.substring(0, tagStart) + " - " + name;


        // Initialize variables
        boolean pinned = tagMessage.charAt(1) == 'p';
        // These booleans determine whether or not an alarm or vibrate alarm managers are made
        boolean blockReceivedAlarms = sharedPrefs.getBoolean(KEY_BLOCK_RECEIVED_ALARMS, false);
        boolean blockReceivedVibrations = sharedPrefs.getBoolean(KEY_BLOCK_RECEIVED_VIBRATIONS, false);
        numVibrateRepeatMillis = 0;
        numVibrateLengthMillis = 0;
        alarmTimeMillis = 0;

        /*If tag message has 'v' as the 2nd char (i.e. ~cv)
        and it is more than just ~cv~/~pv~ (more than 4 chars),
        set up the vibrate options*/
        if (tagMessage.charAt(2) == 'v' &&
                tagMessageLength > 4 &&
                !blockReceivedVibrations) {

            setVibrateVariables();
        }
        /*If tag message has 'a' as the 2nd char (i.e. ~ca)
        and it is more than just ~ca~/~pa~ (more than 4 chars), and
        if the user does not want received alarms blocked
        set up the vibrate options*/
        else if (tagMessage.charAt(2) == 'a' &&
                tagMessageLength > 4 &&
                !blockReceivedAlarms) {

            setAlarmVariables();
        }

        // Put extras into the notiBundle
        Bundle notiBundle = new Bundle();
        notiBundle.putString(KEY_MESSAGE, notiMessage);
        notiBundle.putInt(KEY_ROWID, uniqueId);
        notiBundle.putInt(KEY_SNOOZE_UNIQUE_ID, snoozeUniqueId);
        notiBundle.putBoolean(KEY_IS_PINNED, pinned);
        if (numVibrateLengthMillis != 0 && numVibrateRepeatMillis != 0 && !blockReceivedVibrations) {
            notiBundle.putInt(KEY_VIBRATION_LENGTH, numVibrateLengthMillis);
            notiBundle.putInt(KEY_VIBRATION_REPEAT, numVibrateRepeatMillis);
            notiBundle.putBoolean(KEY_VIBRATE_OPTIONS_ENTERED, true);
            notiBundle.putBoolean(KEY_VIBRATE_NONE, false);
        }
        if (alarmTimeMillis != 0 && !blockReceivedAlarms) {
            notiBundle.putLong(KEY_ALARM_TIME, alarmTimeMillis);
            notiBundle.putBoolean(KEY_NOTI_HAS_ALARM, true);
            notiBundle.putBoolean(KEY_ALARM_NONE, false);
        }


    }

    // Gets the tag from the SMS, if it exists
    protected String getSmsTag() {
        int receivedMessageLength = receivedMessage.length();
        String tagMessage = "";
        // This will be the index where the tag starts
        tagStart = -1;
        int tagCounter = 0;
        // Starting at the end of the received message,
        // search for the char '~'. Once two of them are found,
        // set tagMessage to the string between the two, inclusive
        for (int i = receivedMessageLength - 1; i > -1; i--) {
            if (receivedMessage.charAt(i) == sendNotiTag) {
                tagCounter++;
            }
            if (tagCounter == 2) {
                tagMessage += receivedMessage.substring(i);
                tagStart = i;
                break;
            }
        }
        return tagMessage;
    }

    // Sets the vibrate length and repeat times
    protected void setVibrateVariables() {
        int periodAt = 0;
        String numVibrateRepeatMillisString = "";
        /*Starting at the fourth character (think ~cv#),
        run through tagMessage until a period is reached.
        When it is reached, set numVibrateRepeatMillisString to
        the substring between where we started and the period*/
        for (int i = 3; i < tagMessageLength; i++) {
            char numberChar = tagMessage.charAt(i);
            if (numberChar == '.') {
                numVibrateRepeatMillisString += tagMessage.substring(3, i);
                // Set periodAt to the current spot
                // in the string (a period) for the next for loop to use
                periodAt = i;
                // When a period is reached, break out of the for loop
                break;
            }
        }

        String numVibrateLengthMillisString = "";
        /*Starting at one spot past periodAt (gotten from the last for loop),
        run through tagMessage until '~' is reached. When it is reached,
        set numVibrateLengthMillisString to the substring between where
        we started and the '~'*/
        for (int i = periodAt + 1; i < tagMessageLength; i++) {
            char numberChar = tagMessage.charAt(i);
            if (numberChar == sendNotiTag) {
                numVibrateLengthMillisString += tagMessage.substring(periodAt + 1, i);
                break;
            }
        }

        // Turn the vibrate strings into ints
        if (numVibrateLengthMillisString.length() > 0) {
            numVibrateLengthMillis = Integer.parseInt(numVibrateLengthMillisString);
        }
        if (numVibrateRepeatMillisString.length() > 0) {
            /*** Testing purporses ***/
            // numVibrateRepeatMillis = Integer.parseInt(numVibrateRepeatMillisString);
            numVibrateRepeatMillis = Integer.parseInt(numVibrateRepeatMillisString) * 60000 - numVibrateLengthMillis;
        }
    }

    protected void setAlarmVariables() {
        int year;
        int month = 0;
        int day = 0;
        int hour = 0;
        int minute = 0;
        int periodAt = 0;

        String monthString = "";
        /*Starting at the fourth character (think ~ca#),
        run through tagMessage until a period is reached.
        When it is reached, set monthString to
        the substring between where we started and the period*/
        for (int i = 3; i < tagMessageLength; i++) {
            char numberChar = tagMessage.charAt(i);
            if (numberChar == '.') {
                monthString += tagMessage.substring(3, i);
                // Set periodAt to the current spot
                // in the string (a period) for the next for loop to use
                periodAt = i;
                // When a period is reached, break out of the for loop
                break;
            }
        }

        String dayString = "";
        /*Starting at one spot past periodAt (gotten from the last for loop),
        run through tagMessage until a period is reached.
        When it is reached, set dayString to
        the substring between where we started and the period*/
        for (int i = periodAt + 1; i < tagMessageLength; i++) {
            char numberChar = tagMessage.charAt(i);
            if (numberChar == '.') {
                dayString += tagMessage.substring(periodAt + 1, i);
                periodAt = i;
                break;
            }
        }

        String hourString = "";
        /*Starting at one spot past periodAt (gotten from the last for loop),
        run through tagMessage until a period is reached.
        When it is reached, set hourString to
        the substring between where we started and the period*/
        for (int i = periodAt + 1; i < tagMessageLength; i++) {
            char numberChar = tagMessage.charAt(i);
            if (numberChar == '.') {
                hourString += tagMessage.substring(periodAt + 1, i);
                periodAt = i;
                break;
            }
        }

        /*Starting at one spot past periodAt (gotten from the last for loop),
        run through tagMessage until '~' is reached. When it is reached,
        set minuteString to the substring between where
        we started and the '~'*/
        String minuteString = "";
        for (int i = periodAt + 1; i < tagMessageLength; i++) {
            char numberChar = tagMessage.charAt(i);
            if (numberChar == sendNotiTag) {
                minuteString += tagMessage.substring(periodAt + 1, i);
                break;
            }
        }

        // Turn the calendar strings into ints
        if (monthString.length() > 0) {
            month = Integer.parseInt(monthString);
            // Months start at 0
            month--;
        }
        if (dayString.length() > 0) {
            day = Integer.parseInt(dayString);
        }
        if (hourString.length() > 0) {
            hour = Integer.parseInt(hourString);
        }
        if (minuteString.length() > 0) {
            minute = Integer.parseInt(minuteString);
        }

        /*Create a calendar with the given month, day, hour, and
        minute. Set the year to the current year, and be sure
        to set the seconds and milliseconds to 0 so that the alarm
        goes off at exactly the minute*/
        Calendar smsCalendar = Calendar.getInstance();
        year = smsCalendar.get(Calendar.YEAR);
        smsCalendar.set(year, month, day, hour, minute);
        smsCalendar.set(Calendar.SECOND, 0);
        smsCalendar.set(Calendar.MILLISECOND, 0);
        alarmTimeMillis = smsCalendar.getTimeInMillis();
    }

    //---This method takes in a Bundle and converts it to a String
    protected String convertBundleToString(Bundle bundleFromIntent, Context context) {
        toastMessage = "";
        String message = "";
        /*
        The get(String key) method of Bundle class returns the entry
        with the given key as an object, in this case "pdus."
        A PDU is a "protocol description unit" which is the industry
        format for an SMS message. So basically, the get("pdus")
        gets all the PDU objects from the Bundle. In a large Bundle,
        there might be multiple PDU objects, which is why an Object
        Array is made.
         */
        Object[] pdus = (Object[]) bundleFromIntent.get("pdus");
        //---Makes a new Array that holds as many SmsMessage objects
        //---as there are PDUs in the Bundle.
        SmsMessage[] msgs = new SmsMessage[pdus.length];
        for (int i = 0; i < msgs.length; i++) {
            /*
            Takes the first element of Array msgs and sets it to
            equal an Sms object. The Sms object is created by the
            static method createFromPdu, which creates an SmsMessage
            from a raw PDU.

            THIS METHOD WLL SOON BE DEPRECATED, IN FAVOR OF THE METHOD
            CREATEFROMPDU(BYTE[], STRING), WHICH TAKES AN EXTRA FORMAT
            PARAMETER
             */
            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            name = findNameByAddress(context, msgs[i].getOriginatingAddress());
            message += msgs[i].getMessageBody().toString();
        }

        toastMessage += "SMS from " + name;
        toastMessage += ": ";
        toastMessage += message;

        return message;
    }


    // Find contact name by using his/her address
    public String findNameByAddress(Context ct, String addr) {
        Uri myPerson = Uri.withAppendedPath(ContactsContract.CommonDataKinds.Phone.CONTENT_FILTER_URI,
                Uri.encode(addr));

        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

        Cursor cursor = null;
        if (myPerson != null) {
            cursor = ct.getContentResolver().query(myPerson,
                    projection, null, null, null);
        }

        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            cursor.close();
            return name;
        }
        if (cursor != null) {
            cursor.close();
        }
        return addr;
    }
}
