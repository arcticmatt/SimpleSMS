package com.jm2.simplesms;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

public class DBAdapter {
    /*---- General Noti ----*/

    public static String KEY_ROWID;
    public static String KEY_MESSAGE;
    public static String KEY_TIME;
    public static String KEY_SNOOZE_UNIQUE_ID;
    public static String KEY_IS_PINNED;
    public static String KEY_REMAKE;

    /*---- Vibration Noti ----*/

    public static String KEY_VIBRATION_LENGTH;
    public static String KEY_VIBRATION_REPEAT;
    public static String KEY_VIBRATE_OPTIONS_ENTERED;
    public static String KEY_VIBRATE_NONE;

    /*---- Queue Noti ----*/

    public static String KEY_IS_QUEUED_NOTI;
    public static String KEY_QUEUE_TIME;
    public static String KEY_QUEUE_REPEATS;
    public static String KEY_QUEUE_REPEAT_MILLIS;
    public static String KEY_QUEUE_TIME_TO_END;
    public static String KEY_LAST_QUEUE_TIME;
    public static String KEY_QUEUE_INFINITE_REPEATS;
    public static String KEY_QUEUE_NONE;
    public static String KEY_QUEUE_REMINDER;
    public static String KEY_IS_QUEUED_NOTI_CLICK;

    /*---- SMS and Queue SMS ----*/
    public static String KEY_PHONE_NUMBER;
    public static String KEY_SEND_NOTI;

    public static String KEY_IS_SMS;
    public static String KEY_SMS_QUEUE_TIME_TO_END;
    public static String KEY_SMS_QUEUE_TIME;
    public static String KEY_SMS_QUEUE_REPEAT_MILLIS;
    public static String KEY_SMS_QUEUE_REPEATS;
    public static String KEY_SMS_QUEUE_MESSAGE;
    public static String KEY_SMS_QUEUE_PHONE_NUMBER;
    public static String KEY_SMS_QUEUE_UNIQUE_ID;

    /*---- Alarm ----*/

    public static String KEY_NOTI_HAS_ALARM;
    public static String KEY_ALARM_TIME;
    public static String KEY_ALARM_REPEATS;
    public static String KEY_ALARM_REPEAT_MILLIS;
    public static String KEY_ALARM_IS_SILENT;
    public static String KEY_ALARM_IS_VIBRATE;
    public static String KEY_ALARM_MAX_VOLUME;
    public static String KEY_ALARM_IS_VOLUME_CRESCENDO;
    public static String KEY_ALARM_SOUND_URI;
    public static String KEY_ALARM_SNOOZE_LENGTH;
    public static String KEY_ALARM_NONE;

    /* not used in DB */
    public static String KEY_INSERT;
    public static String KEY_REMAKE_QUEUE;

    private static final String TAG = "DBAdapter";

    private static final String DATABASE_NAME = "notifications";
    private static final String DATABASE_TABLE = "notis";
    private static final int DATABASE_VERSION = 1;

    private static String DATABASE_CREATE;

    private final Context context;

    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBAdapter(Context ctx) {
        this.context = ctx;

        Resources res = context.getResources();

        /*---- General Noti ----*/

        KEY_ROWID = res.getString(R.string.KEY_ROWID);
        KEY_MESSAGE = res.getString(R.string.KEY_MESSAGE);
        KEY_TIME = res.getString(R.string.KEY_TIME);
        KEY_SNOOZE_UNIQUE_ID = res.getString(R.string.KEY_SNOOZE_UNIQUE_ID);
        KEY_IS_PINNED = res.getString(R.string.KEY_IS_PINNED);
        KEY_REMAKE = res.getString(R.string.KEY_REMAKE);

        /*---- Vibration Noti ----*/

        KEY_VIBRATION_LENGTH = res.getString(R.string.KEY_VIBRATION_LENGTH);
        KEY_VIBRATION_REPEAT = res.getString(R.string.KEY_VIBRATION_REPEAT);
        KEY_VIBRATE_OPTIONS_ENTERED = res.getString(R.string.KEY_VIBRATE_OPTIONS_ENTERED);
        KEY_VIBRATE_NONE = res.getString(R.string.KEY_VIBRATE_NONE);

        /*---- Queue Noti ----*/

        KEY_IS_QUEUED_NOTI = res.getString(R.string.KEY_IS_QUEUED_NOTI);
        KEY_QUEUE_TIME = res.getString(R.string.KEY_QUEUE_TIME);
        KEY_QUEUE_REPEATS = res.getString(R.string.KEY_QUEUE_REPEATS);
        KEY_QUEUE_REPEAT_MILLIS = res.getString(R.string.KEY_QUEUE_REPEAT_MILLIS);
        KEY_QUEUE_TIME_TO_END = res.getString(R.string.KEY_QUEUE_TIME_TO_END);
        KEY_LAST_QUEUE_TIME = res.getString(R.string.KEY_LAST_QUEUE_TIME);
        KEY_QUEUE_INFINITE_REPEATS = res.getString(R.string.KEY_QUEUE_INFINITE_REPEATS);
        KEY_QUEUE_NONE = res.getString(R.string.KEY_QUEUE_NONE);
        KEY_QUEUE_REMINDER = res.getString(R.string.KEY_QUEUE_REMINDER);
        KEY_IS_QUEUED_NOTI_CLICK = res.getString(R.string.KEY_IS_QUEUED_NOTI_CLICK);

        /*---- SMS and Queue SMS ----*/
        KEY_PHONE_NUMBER = res.getString(R.string.KEY_PHONE_NUMBER);
        KEY_SEND_NOTI = res.getString(R.string.KEY_SEND_NOTI);

        KEY_IS_SMS = res.getString(R.string.KEY_IS_SMS);
        KEY_SMS_QUEUE_TIME_TO_END = res.getString(R.string.KEY_SMS_QUEUE_TIME_TO_END);
        KEY_SMS_QUEUE_TIME = res.getString(R.string.KEY_SMS_QUEUE_TIME);
        KEY_SMS_QUEUE_REPEAT_MILLIS = res.getString(R.string.KEY_SMS_QUEUE_REPEAT_MILLIS);
        KEY_SMS_QUEUE_REPEATS = res.getString(R.string.KEY_SMS_QUEUE_REPEATS);
        KEY_SMS_QUEUE_MESSAGE = res.getString(R.string.KEY_SMS_QUEUE_MESSAGE);
        KEY_SMS_QUEUE_PHONE_NUMBER = res.getString(R.string.KEY_SMS_QUEUE_PHONE_NUMBER);
        KEY_SMS_QUEUE_UNIQUE_ID = res.getString(R.string.KEY_SMS_QUEUE_UNIQUE_ID);

        /*---- Alarm ----*/

        KEY_NOTI_HAS_ALARM = res.getString(R.string.KEY_NOTI_HAS_ALARM);
        KEY_ALARM_TIME = res.getString(R.string.KEY_ALARM_TIME);
        KEY_ALARM_REPEATS = res.getString(R.string.KEY_ALARM_REPEATS);
        KEY_ALARM_REPEAT_MILLIS = res.getString(R.string.KEY_ALARM_REPEAT_MILLIS);
        KEY_ALARM_IS_SILENT = res.getString(R.string.KEY_ALARM_IS_SILENT);
        KEY_ALARM_IS_VIBRATE = res.getString(R.string.KEY_ALARM_IS_VIBRATE);
        KEY_ALARM_MAX_VOLUME = res.getString(R.string.KEY_ALARM_MAX_VOLUME);
        KEY_ALARM_IS_VOLUME_CRESCENDO = res.getString(R.string.KEY_ALARM_IS_VOLUME_CRESCENDO);
        KEY_ALARM_SOUND_URI = res.getString(R.string.KEY_ALARM_SOUND_URI);
        KEY_ALARM_SNOOZE_LENGTH = res.getString(R.string.KEY_ALARM_SNOOZE_LENGTH);
        KEY_ALARM_NONE = res.getString(R.string.KEY_ALARM_NONE);

            /* not used in DB */
        KEY_INSERT = res.getString(R.string.KEY_INSERT);
        KEY_REMAKE_QUEUE = res.getString(R.string.KEY_REMAKE_QUEUE);

        DATABASE_CREATE =
                "create table " + DATABASE_TABLE + " (_id integer primary key autoincrement, "
                        + KEY_MESSAGE + " TEXT, "
                        + KEY_TIME + " TEXT, "
                        + KEY_SNOOZE_UNIQUE_ID + " TEXT, "
                        + KEY_IS_PINNED + " TEXT, "
                        + KEY_REMAKE + " TEXT, "
                        + KEY_VIBRATION_LENGTH + " TEXT, "
                        + KEY_VIBRATION_REPEAT + " TEXT, "
                        + KEY_VIBRATE_OPTIONS_ENTERED + " TEXT, "
                        + KEY_VIBRATE_NONE + " TEXT, "
                        + KEY_IS_QUEUED_NOTI + " TEXT, "
                        + KEY_QUEUE_TIME + " TEXT, "
                        + KEY_QUEUE_REPEATS + " TEXT, "
                        + KEY_QUEUE_REPEAT_MILLIS + " TEXT, "
                        + KEY_QUEUE_TIME_TO_END + " TEXT, "
                        + KEY_LAST_QUEUE_TIME + " TEXT, "
                        + KEY_QUEUE_INFINITE_REPEATS + " TEXT, "
                        + KEY_QUEUE_NONE + " TEXT, "
                        + KEY_QUEUE_REMINDER + " TEXT, "
                        + KEY_IS_QUEUED_NOTI_CLICK + " TEXT, "
                        + KEY_PHONE_NUMBER + " TEXT, "
                        + KEY_SEND_NOTI + " TEXT, "
                        + KEY_IS_SMS + " TEXT, "
                        + KEY_SMS_QUEUE_TIME_TO_END + " TEXT, "
                        + KEY_SMS_QUEUE_TIME + " TEXT, "
                        + KEY_SMS_QUEUE_REPEAT_MILLIS + " TEXT, "
                        + KEY_SMS_QUEUE_REPEATS + " TEXT, "
                        + KEY_SMS_QUEUE_MESSAGE + " TEXT, "
                        + KEY_SMS_QUEUE_PHONE_NUMBER + " TEXT, "
                        + KEY_SMS_QUEUE_UNIQUE_ID + " TEXT, "
                        + KEY_NOTI_HAS_ALARM + " TEXT, "
                        + KEY_ALARM_TIME + " TEXT, "
                        + KEY_ALARM_REPEATS + " TEXT, "
                        + KEY_ALARM_REPEAT_MILLIS + " TEXT, "
                        + KEY_ALARM_IS_SILENT + " TEXT, "
                        + KEY_ALARM_IS_VIBRATE + " TEXT, "
                        + KEY_ALARM_MAX_VOLUME + " TEXT, "
                        + KEY_ALARM_IS_VOLUME_CRESCENDO + " TEXT, "
                        + KEY_ALARM_SOUND_URI + " TEXT, "
                        + KEY_ALARM_SNOOZE_LENGTH + " TEXT, "
                        + KEY_ALARM_NONE + " TEXT);";



        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS titles");
            onCreate(db);
        }
    }

    //---opens the database---
    public DBAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //---closes the database---
    public void close() {
        DBHelper.close();
    }


    //---insert a noti with specific fields into the datbase---

    public long insertNoti(String[] columns, String[] values){

        ContentValues initialValues = new ContentValues();

        if (columns.length != values.length){
            return -1;
        } else{

            for(int i = 0; i<columns.length; i++){

                initialValues.put(columns[i], values[i]);

            }

        }

        // This try/catch block ensures that notis with the same
        // row id are not inserted twice (which will cause an error)
        try {
            return db.insertOrThrow(DATABASE_TABLE, null, initialValues);
        }
        catch(SQLException exception) {
        }
        finally {
        }
        return 1;

    }


    /*---- Update specific fields of noti at position rowID ----*/

    public boolean updateNoti(long rowId, String[] columns, String[] new_values){

        ContentValues args = new ContentValues();

        if (columns.length != new_values.length){
            return false;
        } else{

            for(int i = 0; i<columns.length; i++){

                args.put(columns[i], new_values[i]);

            }

        }

        return db.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }

    //---retrieves all the Notis---
    public Cursor getAllActiveQueueSms() {
        // Only get the SMS rows from DBAdapter
        String select = "((" + KEY_REMAKE + "='true') AND (" + KEY_IS_SMS + "='true'))";
        // Only return necessary rows
        return db.query(DATABASE_TABLE, new String[]{
                KEY_ROWID,
                KEY_REMAKE,
                KEY_MESSAGE,
                KEY_TIME,
                KEY_PHONE_NUMBER,
                KEY_SEND_NOTI,

                KEY_IS_SMS,
                KEY_SMS_QUEUE_TIME_TO_END,
                KEY_SMS_QUEUE_TIME,
                KEY_SMS_QUEUE_REPEAT_MILLIS,
                KEY_SMS_QUEUE_REPEATS,
                KEY_SMS_QUEUE_MESSAGE,
                KEY_SMS_QUEUE_PHONE_NUMBER,
                KEY_SMS_QUEUE_UNIQUE_ID,

                KEY_VIBRATION_LENGTH,
                KEY_VIBRATION_REPEAT,
                KEY_ALARM_TIME,
        },
                select,
                null,
                null,
                null,
                null);
    }

    // Remakes both the notis and the queued/sent SMS
    // that still existed when shutdown occured
    public void remakeOnBoot() {


        // SMS
        Bundle smsBundle = new Bundle();
        Cursor smsCursor = getAllActiveQueueSms();
        smsCursor.moveToFirst();
        // for loop that runs through each row of cursor
        for (int i = 0; i < smsCursor.getCount(); i++) {
            int columnCount = smsCursor.getColumnCount();
            // for loop that runs through each column of current row
            for (int j = 0; j < columnCount; j++) {
                String tempColumnName = smsCursor.getColumnName(j);
                String tempValueString = smsCursor.getString(j);
                if (tempValueString != null && tempValueString.length() > 0) {
                    // Add the INTGER extras to bundle
                    if (tempColumnName.equals(KEY_ROWID) ||
                            tempColumnName.equals(KEY_VIBRATION_LENGTH) ||
                            tempColumnName.equals(KEY_VIBRATION_REPEAT) ||
                            tempColumnName.equals(KEY_SMS_QUEUE_UNIQUE_ID)) {
                        int tempInt = Integer.parseInt(tempValueString);
                        smsBundle.putInt(tempColumnName, tempInt);
                    }
                    // Add the STRING extras to bundle
                    if (tempColumnName.equals(KEY_MESSAGE) ||
                            tempColumnName.equals(KEY_PHONE_NUMBER) ||
                            tempColumnName.equals(KEY_SMS_QUEUE_MESSAGE) ||
                            tempColumnName.equals(KEY_SMS_QUEUE_PHONE_NUMBER)) {
                        String tempString = tempValueString;
                        smsBundle.putString(tempColumnName, tempString);
                    }
                    // Add the LONG fields to bundle
                    if (tempColumnName.equals(KEY_TIME) ||
                            tempColumnName.equals(KEY_SMS_QUEUE_TIME_TO_END) ||
                            tempColumnName.equals(KEY_SMS_QUEUE_TIME) ||
                            tempColumnName.equals(KEY_SMS_QUEUE_REPEAT_MILLIS) ||
                            tempColumnName.equals(KEY_ALARM_TIME)) {
                        Long tempLong = Long.parseLong(tempValueString);
                        smsBundle.putLong(tempColumnName, tempLong);
                    }
                    // Add the BOOLEAN fields to bundle
                    if (tempColumnName.equals(KEY_REMAKE) ||
                            tempColumnName.equals(KEY_SEND_NOTI) ||
                            tempColumnName.equals(KEY_SMS_QUEUE_REPEATS)) {
                        Boolean tempBoolean = Boolean.parseBoolean(tempValueString);
                        smsBundle.putBoolean(tempColumnName, tempBoolean);
                    }
                }
            }

            Resources res = context.getResources();
            String KEY_SMS_QUEUE_REMAKE_FROM_REBOOT = res.getString(R.string.KEY_SMS_QUEUE_REMAKE_FROM_REBOOT);
            // Tell QueueSmsAlarm that this sms is remade from boot, in order to recalculate
            // the queue time for repeat sms messages
            smsBundle.putBoolean(KEY_SMS_QUEUE_REMAKE_FROM_REBOOT, true);

            QueueSmsAlarm myQueueSmsAlarm = new QueueSmsAlarm(context, smsBundle);

            smsCursor.moveToNext();

        }

        close();
    }
}



