package com.forgotMyMobile;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver{
    public static final String SMS_EXTRA_NAME = "pdus";

    final SmsManager sms = SmsManager.getDefault();

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
                String address = sms.getOriginatingAddress();

                if (body.contains("1234")) {
                    Log.i("SMSReceiver", "sms received");
                  
                    //make a call to service to respond to the SMS
                    Intent i = new Intent(context,BackgroundService.class);
                    i.putExtra(BackgroundService.RESPOND_TO, address);
                    context.startService(i);
                    Toast.makeText(context, "Control Msg"+address, Toast.LENGTH_SHORT).show();
                    this.abortBroadcast();
                } else {
                    Toast.makeText(context, "Normal Msg"+address, Toast.LENGTH_SHORT).show();
                }
                	
            }

        }

    }


}
