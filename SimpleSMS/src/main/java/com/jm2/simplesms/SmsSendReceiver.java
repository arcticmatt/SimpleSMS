package com.jm2.simplesms;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

// BroadcastReceiver that makes toasts about SMS send status
public class SmsSendReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context myContext, Intent arg1) {
        switch (getResultCode()) {
            case Activity.RESULT_OK:
                Toast.makeText(myContext, "SMS sent",
                        Toast.LENGTH_SHORT).show();
                break;
            case android.telephony.SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                Toast.makeText(myContext, "Generic failure",
                        Toast.LENGTH_SHORT).show();
                break;
            case android.telephony.SmsManager.RESULT_ERROR_NO_SERVICE:
                Toast.makeText(myContext, "No service",
                        Toast.LENGTH_SHORT).show();
                break;
            case android.telephony.SmsManager.RESULT_ERROR_NULL_PDU:
                Toast.makeText(myContext, "Null PDU",
                        Toast.LENGTH_SHORT).show();
                break;
            case android.telephony.SmsManager.RESULT_ERROR_RADIO_OFF:
                Toast.makeText(myContext, "Radio off",
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
