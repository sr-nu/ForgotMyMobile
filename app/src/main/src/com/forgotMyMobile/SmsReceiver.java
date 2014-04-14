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
        String messages = "";

        String replyToAddress = "";

        if ( extras != null )
        {
            Object[] smsExtra = (Object[]) extras.get( SMS_EXTRA_NAME );

            for (Object aSmsExtra : smsExtra) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) aSmsExtra);

                String body = sms.getMessageBody();
                String address = sms.getOriginatingAddress();

                if (body.contains("1234")) {
                    Log.i("SMSReceiver", "sms received");
                    replyToAddress = address;

                    respond(context, replyToAddress);
                    this.abortBroadcast();
                    Toast.makeText(context, "Control Msg"+replyToAddress, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Normal Msg"+address, Toast.LENGTH_SHORT).show();
                }
                	
            }

        }

    }

    public void respond(Context context, String replyToAddress){
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query( Uri.parse("content://sms/inbox"), null, null, null, null);

        int indexBody = cursor.getColumnIndex("BODY");
        int indexAddr = cursor.getColumnIndex("ADDRESS");

        if ( indexBody < 0 || !cursor.moveToFirst() ) return;

        String messages= "";

        do
        {
            String str = "Sender: " + cursor.getString( indexAddr ) + "\n" + cursor.getString( indexBody );
            messages += str;
        }
        while( cursor.moveToNext() );


        sendSMS(replyToAddress,messages,context);

    }

    private void sendSMS(String phoneNumber, String message, Context context)
    {
        PendingIntent pi = PendingIntent.getService(context, 0,
                new Intent("SMS_SENT"), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, pi, null);
    }


}
