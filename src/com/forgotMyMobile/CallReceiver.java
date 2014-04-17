package com.forgotMyMobile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class CallReceiver extends BroadcastReceiver {
	private static final String AUTO_FWD_TO = "AUTO_FWD_TO";
    private static final String TIMESTAMP_FORMAT = "dd/MM/yy hh:mm:ss";

	@Override
	public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();
        Log.e("Call Receiver","On receive called");

        if ( extras != null 
        		&& intent.getStringExtra(android.telephony.TelephonyManager.EXTRA_STATE) == android.telephony.TelephonyManager.EXTRA_STATE_RINGING 
        		|| intent.getStringExtra(android.telephony.TelephonyManager.EXTRA_STATE).equalsIgnoreCase("RINGING"))
        {
        	Toast.makeText(context, "Phone Ringing", Toast.LENGTH_LONG).show();
            String phoneNumber = extras.getString("incoming_number");
            Log.e("Receiving call from",phoneNumber);
            
        	if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(MainActivity.AUTO_FWD, false)) {
        		Log.e("Auto forward enabled","true");
        		String autoFwdTo = PreferenceManager.getDefaultSharedPreferences(context).getString(AUTO_FWD_TO,"");
        		if(autoFwdTo != null && !autoFwdTo.trim().isEmpty()) {
            		Log.i("Call Receiver","AutoForwarding message to:"+autoFwdTo);
        		    SmsManager smsManager = SmsManager.getDefault();
        		    smsManager.sendMultipartTextMessage(autoFwdTo, null, smsManager.divideMessage("Receiving Call from: "+ phoneNumber+" at "+new SimpleDateFormat(TIMESTAMP_FORMAT, Locale.US).format(new Date())), null, null);
        		}
        	}

        }


	}

}
