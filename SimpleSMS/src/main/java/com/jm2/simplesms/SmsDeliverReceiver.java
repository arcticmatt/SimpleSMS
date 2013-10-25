package com.jm2.simplesms;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

// BroadcastReceiver that makes toasts about SMS delivery status
public class SmsDeliverReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context myContext, Intent arg1) {
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                Toast.makeText(myContext, "SMS delivered",
                        Toast.LENGTH_SHORT).show();
                break;
            case Activity.RESULT_CANCELED:
                Toast.makeText(myContext, "SMS not delivered",
                        Toast.LENGTH_SHORT).show();
                break;

        }
    }

}
