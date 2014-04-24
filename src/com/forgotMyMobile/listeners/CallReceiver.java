package com.forgotMyMobile.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.forgotMyMobile.helpers.PreferenceHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CallReceiver extends BroadcastReceiver {
    private static final String TIMESTAMP_FORMAT = "dd/MM/yy hh:mm:ss";
	@Override
	public void onReceive(final Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        Log.e("Call Receiver","On receive called"+ intent.getStringExtra(android.telephony.TelephonyManager.EXTRA_STATE) );

        if ( extras != null && intent!= null && (intent.getStringExtra(android.telephony.TelephonyManager.EXTRA_STATE) != null)
        		&& (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_RINGING)
        		|| intent.getStringExtra(android.telephony.TelephonyManager.EXTRA_STATE).equalsIgnoreCase("RINGING")))
        {
        	Toast.makeText(context, "Phone Ringing", Toast.LENGTH_LONG).show();
            String phoneNumber = extras.getString("incoming_number");
            Log.e("Receiving call from",phoneNumber);
            
        	if(PreferenceHelper.isAutoForwardEnabled(context)) {
        		Log.e("Auto forward enabled","true");
        		String autoFwdTo = PreferenceHelper.getAutoFwdNumber(context);
        		if(autoFwdTo != null && !autoFwdTo.trim().isEmpty()) {
            		Log.i("Call Receiver","AutoForwarding message to:"+autoFwdTo);
        		    SmsManager smsManager = SmsManager.getDefault();
        		    smsManager.sendMultipartTextMessage(autoFwdTo, null, smsManager.divideMessage("Receiving Call from: "+ phoneNumber+" at "+new SimpleDateFormat(TIMESTAMP_FORMAT, Locale.US).format(new Date())), null, null);
        		}
        	}
        }

	}
}


