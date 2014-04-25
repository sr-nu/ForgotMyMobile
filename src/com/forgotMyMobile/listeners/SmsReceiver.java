package com.forgotMyMobile.listeners;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.forgotMyMobile.helpers.CommandHandler;
import com.forgotMyMobile.helpers.PreferenceHelper;

public class SmsReceiver extends BroadcastReceiver{
    private static final String TAG = "SmsReceiver";


    public void onReceive( Context context, Intent intent )
    {
        
    	Toast.makeText(context,"SMSReceiver on receive",Toast.LENGTH_LONG).show();

        Bundle extras = intent.getExtras();

        if ( extras != null )
        {
            Object[] smsExtra = (Object[]) extras.get( PreferenceHelper.SMS_EXTRA_NAME );

            for (Object aSmsExtra : smsExtra) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) aSmsExtra);

                String body = sms.getMessageBody();
                String fromNumber = sms.getOriginatingAddress();

                if (isControlMessage(body,context)) {
                    Log.i("SMSReceiver", "sms received");

                    CommandHandler.handleCommand(context, fromNumber, getCommand(body,context));
                    
                    Toast.makeText(context, "Control Msg from:"+fromNumber, Toast.LENGTH_SHORT).show();
                } else {
                	if(PreferenceHelper.isAutoForwardEnabled(context)) {
                		Log.d(TAG,"AutoForward is set");
                		String autoFwdTo = PreferenceHelper.getAutoFwdNumber(context);
                		if(autoFwdTo != null && !autoFwdTo.trim().isEmpty() && !autoFwdTo.equals(fromNumber)) {
                    		Log.i(TAG,"AutoForwarding message to:"+autoFwdTo);
                		    SmsManager smsManager = SmsManager.getDefault();
                		    smsManager.sendMultipartTextMessage(autoFwdTo, null, smsManager.divideMessage(fromNumber+":"+body), null, null);
                		}
                	}
                    Log.i(TAG, "Normal message received from:"+fromNumber);
                }                	
            }
        }
        
    }

    private String getCommand(String body, Context context) {
    	String passCode = PreferenceHelper.getPassCode(context);
    	if ( body.contains(passCode) ){
    		return body.substring(passCode.length()).trim();
    	} else {
    		return null;
    	}
    }
    
	private boolean isControlMessage(String body,Context context) {
		String passCode = PreferenceHelper.getPassCode(context);
		return body != null && body.trim().startsWith(passCode);
	}
}
