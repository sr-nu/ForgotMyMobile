package com.forgotMyMobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver{
    private static final String PASSCODE = "PASSCODE";
	public static final String SMS_EXTRA_NAME = "pdus";
	private static final String TAG = "SmsReceiver";

    public void onReceive( Context context, Intent intent )
    {
        
    	Toast.makeText(context,"SMSReceiver on receive",Toast.LENGTH_LONG).show();

        Bundle extras = intent.getExtras();

        if ( extras != null )
        {
            Object[] smsExtra = (Object[]) extras.get( SMS_EXTRA_NAME );

            for (Object aSmsExtra : smsExtra) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) aSmsExtra);

                String body = sms.getMessageBody();
                String fromNumber = sms.getOriginatingAddress();

                if (isControlMessage(body,context)) {
                    Log.i("SMSReceiver", "sms received");
                    
                    Intent i = new Intent(context,BackgroundService.class);
                    i.putExtra(BackgroundService.RESPOND_TO, fromNumber);
                    context.startService(i);
                    
                    Toast.makeText(context, "Control Msg from:"+fromNumber, Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG, "Normal message received from:"+fromNumber);
                }                	
            }
        }
        
    }

	private boolean isControlMessage(String body,Context context) {
		long passcode = PreferenceManager.getDefaultSharedPreferences(context).getLong(PASSCODE, 0);                
		return body != null && body.trim().equals(String.valueOf(passcode));
	}
}
