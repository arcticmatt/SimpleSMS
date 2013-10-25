package com.jm2.simplesms;

/**
 * Created by matt on 10/17/13.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

public class WelcomeDialogActivity extends Activity {
    public static String KEY_QUEUE_BUNDLE;
    public static String KEY_MESSAGE;

    private static final String KEY_THEMES_DIALOGS = "pref_key_themes_dialogs";
    Bundle queueBundle;
    SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_welcome);

        // Set dialog title
        setTitle("Welcome!");
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // Theme
        if (sharedPrefs.getString(KEY_THEMES_DIALOGS, "1").equals("1")) {
            setTheme(android.R.style.Theme_Holo_Light_Dialog);
        }

    }

    // Cancel button method (finishes dialog)
    public void finishDialog(View v) {
        finish();
    }



}
