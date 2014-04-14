package com.forgotMyMobile;

import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;

public class BackgroundService {
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
    	
    	Log.i("SendSMS","Sending"+message+" to : "+phoneNumber);
        PendingIntent pi = PendingIntent.getService(context, 0,
                new Intent("SMS_SENT"), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, pi, null);
    }


}
