package com.jm2.simplesms;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class QueueSmsDialogActivity extends Activity {
    public static String KEY_SMS_QUEUE_TIME;
    public static String KEY_SMS_QUEUE_REPEATS;
    public static String KEY_SMS_QUEUE_REPEAT_MILLIS;
    public static String KEY_SMS_QUEUE_TIME_TO_END;

    public static String KEY_QUEUE_BUNDLE;

    private static final String KEY_THEMES_DIALOGS = "pref_key_themes_dialogs";
    private static final String KEY_QUEUE_SMS_REPEAT = "pref_key_queue_sms_repeat";

    NumberPicker dayPicker;
    NumberPicker hourPicker;
    NumberPicker minutePicker;
    TimePicker myTimePicker;
    DatePicker myDatePicker;

    int dateMonth;
    int dateDay;
    int dateYear;

    int timeHour;
    int timeMinute;

    int repeatDay;
    int repeatHour;
    int repeatMinute;
    boolean repeat;


    SharedPreferences sharedPrefs;
    Bundle queueBundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Theme
        if (sharedPrefs.getString(KEY_THEMES_DIALOGS, "1").equals("1")) {
            setTheme(android.R.style.Theme_Holo_Light_Dialog);
        }

        Resources res = this.getResources();

        KEY_SMS_QUEUE_TIME = res.getString(R.string.KEY_SMS_QUEUE_TIME);
        KEY_SMS_QUEUE_REPEATS = res.getString(R.string.KEY_SMS_QUEUE_REPEATS);
        KEY_SMS_QUEUE_REPEAT_MILLIS = res.getString(R.string.KEY_SMS_QUEUE_REPEAT_MILLIS);
        KEY_SMS_QUEUE_TIME_TO_END = res.getString(R.string.KEY_SMS_QUEUE_TIME_TO_END);

        KEY_QUEUE_BUNDLE = res.getString(R.string.KEY_QUEUE_BUNDLE);

        // Gets the intent this dialog was started with
        // and get the extras from it
        Intent smsInfoIntent = getIntent();
        queueBundle = smsInfoIntent.getBundleExtra(KEY_QUEUE_BUNDLE);

        // Request no dialog title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_queue_sms);

        initializePickers();
    }

    // Cancel button method (finishes dialog)
    public void finishDialog(View v) {
        finish();
    }

    // Gets queue Sms settings and starts QueueSmsAlarm
    public void queueSms(View v) {
        // Check message and phoneNo fields and make a toast
        // if user forgot to enter something
        MySmsManager mySmsManager = new MySmsManager(this, queueBundle);
        boolean validMessage = mySmsManager.checkSmsTextFields();

        // Only queue the Sms if the user entered values for the
        // message and phoneNo
        if (validMessage) {
            getPickerValues();

            // If all the values in the repeat pickers are 0,
            // repeat is false. Otherwise, repeat is true
            if (repeatDay != 0 || repeatHour != 0 || repeatMinute != 0) {
                repeat = true;
            } else {
                repeat = false;
            }

            // Calculate the repeat period in milliseconds
            long repeatMillis = (repeatDay * 24 * 60 * 60 * 1000)
                    + (repeatHour * 60 * 60 * 1000)
                    + (repeatMinute * 60 * 1000);

            // Make a calendar and set it to the user entered
            // time and date. Also set the seconds and milliseconds
            // to zero (this is necessary - otherwise they will be
            // set to the seconds and milliseconds at which the sms is queued
            Calendar queueCalendar = Calendar.getInstance();
            queueCalendar.set(dateYear, dateMonth, dateDay, timeHour, timeMinute);
            queueCalendar.set(Calendar.SECOND, 0);
            queueCalendar.set(Calendar.MILLISECOND, 0);
            long queueTime = queueCalendar.getTimeInMillis();
            long timeToEnd = 0;
            // If a repeat queued SMS, calculate the timeToEnd
            if (repeat) {
                timeToEnd = getSmsTimeToEnd(queueTime, repeatMillis);
            }

            // This bundle contains all the extras needed to implement the SmsAlarm class
            queueBundle.putLong(KEY_SMS_QUEUE_TIME_TO_END, timeToEnd);
            queueBundle.putLong(KEY_SMS_QUEUE_TIME, queueTime);
            queueBundle.putLong(KEY_SMS_QUEUE_REPEAT_MILLIS, repeatMillis);
            queueBundle.putBoolean(KEY_SMS_QUEUE_REPEATS, repeat);

            // Create/start the alarm
            QueueSmsAlarm myQueueSmsAlarm = (QueueSmsAlarm) new QueueSmsAlarm(this, queueBundle);

            // Insert the queued SMS into the database. This is the ONLY
            // place where SMS messages are inserted into DBAdapter
            MySmsManager updatedMySmsManager = new MySmsManager(this, queueBundle);
            updatedMySmsManager.insertSmsIntoDatabase();

            // Create a toast that tells user when the
            // queue time is
            SimpleDateFormat calendarFormat = new SimpleDateFormat("EEEE M/d, KK:mm a");
            String queueToastText = "SMS queued for " + calendarFormat.format(queueCalendar.getTime());

            Toast toast = Toast.makeText(this, queueToastText, Toast.LENGTH_LONG);
            toast.show();

            Intent queueDialogIntent = new Intent(this, MainActivity.class);
            // Boolean extra that makes the text clear in main activity.
            // This happens in the onResume method
            queueDialogIntent.putExtra("clear_text", true);
            queueDialogIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(queueDialogIntent);
        }
    }

    // Initializes all the pickers in the alarm dialog
    protected void initializePickers() {
        // Initialize all pickers
        myTimePicker = (TimePicker) findViewById(R.id.timePicker1);

        myDatePicker = (DatePicker) findViewById(R.id.datePicker1);

        // For the number pickers it is necessary to set min and
        // max values
        dayPicker = (NumberPicker) findViewById(R.id.daysPicker);
        dayPicker.setMaxValue(30);
        dayPicker.setMinValue(0);

        hourPicker = (NumberPicker) findViewById(R.id.hoursPicker);
        hourPicker.setMaxValue(23);
        hourPicker.setMinValue(0);

        minutePicker = (NumberPicker) findViewById(R.id.minutesPicker);
        minutePicker.setMaxValue(59);
        minutePicker.setMinValue(0);
    }

    // Gets all picker values and assign them
    // to instance variables
    protected void getPickerValues() {

        // Get all the values that the user
        // entered in the pickers
        dateMonth = myDatePicker.getMonth();
        dateDay = myDatePicker.getDayOfMonth();
        dateYear = myDatePicker.getYear();

        timeHour = myTimePicker.getCurrentHour();
        timeMinute = myTimePicker.getCurrentMinute();

        repeatDay = dayPicker.getValue();
        repeatHour = hourPicker.getValue();
        repeatMinute = minutePicker.getValue();
    }

    // Gets time to end repeat SMS queues
    protected long getSmsTimeToEnd(long queueTime, long repeatMillis) {
        // Get shared preferences in order to determine how many
        // times the SMS should be repeated
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        int queueRepeatNumber = Integer.parseInt(sharedPrefs.getString(KEY_QUEUE_SMS_REPEAT, "5"));
        // If a repeat SMS, set a time for the alarm to end
        long timeDifferenceToEnd = repeatMillis * queueRepeatNumber;
        long timeToEnd = queueTime + timeDifferenceToEnd + 30000;
        return timeToEnd;
    }
}
