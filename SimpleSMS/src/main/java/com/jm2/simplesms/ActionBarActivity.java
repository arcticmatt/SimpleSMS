package com.jm2.simplesms;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class ActionBarActivity extends Activity {
    public static String KEY_ROWID;
    public static String KEY_SNOOZE_UNIQUE_ID;
    public static String KEY_QUEUE_NONE;
    // not in DBAdapter
    public static String KEY_NO_DELETE_TOAST;

    public static String KEY_QUEUE_TIME;
    public static String KEY_QUEUE_REPEATS;
    public static String KEY_QUEUE_REPEAT_MILLIS;
    public static String KEY_QUEUE_TIME_TO_END;
    public static String KEY_QUEUE_INFINITE_REPEATS;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    // Go to preferences activity
    public void toSettings(MenuItem menuItem) {
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
    }



    // Delete all active (remake) queue sms messages, including sent notis that are queued
    public void deleteAllQueuedSms(MenuItem menuItem) {
        Resources res = this.getResources();
        KEY_ROWID = res.getString(R.string.KEY_ROWID);
        DBAdapter myDBAdapter = new DBAdapter(this);
        myDBAdapter.open();
        Cursor myCursor = myDBAdapter.getAllActiveQueueSms();
        myCursor.moveToFirst();
        // for loop that runs through every row of cursor
        for(int i = 0; i < myCursor.getCount(); i++) {
            String rowIdString = myCursor.getString(myCursor.getColumnIndex(KEY_ROWID));
            int rowId = Integer.parseInt(rowIdString);

            // The only variable necessary to delete a queued sms is its rowid
            Bundle smsBundle = new Bundle();
            smsBundle.putInt(KEY_ROWID, rowId);
            MySmsManager mySmsManager = new MySmsManager(this, smsBundle);
            mySmsManager.deleteQueueSms();

            myCursor.moveToNext();
        }

        myDBAdapter.close();

        Toast.makeText(this,
                "Queued SMS messages deleted",
                Toast.LENGTH_SHORT).show();
    }

/*    // Goes to help activity
    public void toHelp(MenuItem menuItem) {
        Intent helpIntent = new Intent(this, HelpActivity.class);
        startActivity(helpIntent);
    }*/

}
