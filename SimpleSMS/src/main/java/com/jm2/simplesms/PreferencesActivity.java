package com.jm2.simplesms;

import android.app.ActionBar;
import android.os.Bundle;

public class PreferencesActivity extends NavigateHomeActivity{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable the user to navigate back using the action bar
        ActionBar actionBar = getActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //PreferenceManager.setDefaultValues(PreferencesActivity.this, R.xml.preferences, false);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new UserPreferencesFragment()).commit();
    }



}
