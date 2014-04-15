package com.forgotMyMobile;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

public class BackgroundService extends IntentService {
    public static final String RESPOND_TO = "RESPOND_TO";

	public BackgroundService() {
		super("Background service");
	}

	public void respond(Context context, String replyToAddress){
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query( Uri.parse("content://sms/inbox"), null, "ADDRESS <> '"+replyToAddress+"' AND read = 0", null, null);

        int indexBody = cursor.getColumnIndex("BODY");
        int indexAddr = cursor.getColumnIndex("ADDRESS");

        if ( indexBody < 0 || !cursor.moveToFirst() ) {
        	Log.i("Background Service","No unread messages");
        	return;
        }

        String messages= "";

//    	sendSMS(replyToAddress,cursor.getCount()+"",context);

    	do
        {
            String str = cursor.getString( indexAddr )+" : "
            		+ cursor.getString( indexBody )+ "\n" ;
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
//        sms.sendTextMessage(phoneNumber, null, message, pi, null);
        sms.sendMultipartTextMessage(phoneNumber, null, sms.divideMessage(message), null, null);
    }

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.e("Background Service","Intent received");
		String respondTo = intent.getStringExtra(RESPOND_TO);
		respond(this.getApplicationContext(),respondTo);
	}
}
