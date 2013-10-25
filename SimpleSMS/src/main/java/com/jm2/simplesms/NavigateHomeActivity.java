package com.jm2.simplesms;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

/**
 * Created by matt on 9/9/13.
 */
// An activity that sets the icon in the action bar to navigate
// back to MainActivity
public class NavigateHomeActivity extends Activity {
    // Set the icon in the action bar to navigate back
    // to MainActivity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                /*The regular way where you define the parent
                activity in AndroidManifest did not work
                for the Nexus 7, so this is used instead*/
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
