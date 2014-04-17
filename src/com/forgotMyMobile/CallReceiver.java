package com.forgotMyMobile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.sax.StartElementListener;
import android.telephony.PhoneStateListener;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

public class CallReceiver extends BroadcastReceiver {
	private static final String AUTO_FWD_TO = "AUTO_FWD_TO";
    private static final String TIMESTAMP_FORMAT = "dd/MM/yy hh:mm:ss";

    private static boolean noCallListenerYet = true;
	@Override
	public void onReceive(final Context context, Intent intent) {
		
		
		if (noCallListenerYet) {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(
                    Context.TELEPHONY_SERVICE);
            tm.listen(new PhoneStateListener() {
                @Override
                public void onCallStateChanged(int state, String incomingNumber) {
                    switch (state) {
                        case TelephonyManager.CALL_STATE_RINGING:
                            Log.e("outgoing state", "RINGING");
                            break;
                        case TelephonyManager.CALL_STATE_OFFHOOK:
                        	 Log.e("outgoing state", "OFFHOOK");
                            break;
                        case TelephonyManager.CALL_STATE_IDLE:
                        	 Log.e("outgoing state", "IDLE");
                            break;
                        default:
                        	Log.e("outgoing state","Default: " + state);
                            break;
                    }
                }
            }, PhoneStateListener.LISTEN_CALL_STATE);
            noCallListenerYet = false;
        } 

        Bundle extras = intent.getExtras();
        Log.e("Call Receiver","On receive called"+ intent.getStringExtra(android.telephony.TelephonyManager.EXTRA_STATE) );

        if ( extras != null && intent!= null && (intent.getStringExtra(android.telephony.TelephonyManager.EXTRA_STATE) != null)
        		&& (intent.getStringExtra(android.telephony.TelephonyManager.EXTRA_STATE) == android.telephony.TelephonyManager.EXTRA_STATE_RINGING 
        		|| intent.getStringExtra(android.telephony.TelephonyManager.EXTRA_STATE).equalsIgnoreCase("RINGING")))
        {
        	Toast.makeText(context, "Phone Ringing", Toast.LENGTH_LONG).show();
        	
        	Log.e("IS","starting call..");
        	context.startService(new Intent(context, CallIntentService.class));
        	
        	
        	
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


