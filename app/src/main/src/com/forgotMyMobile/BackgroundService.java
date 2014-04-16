package com.forgotMyMobile;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.telephony.SmsManager;
import android.util.Log;

public class BackgroundService extends IntentService {
    private static final String TIMESTAMP_FORMAT = "dd/MM/yy hh:mm:ss";
	public static final String RESPOND_TO = "RESPOND_TO";
	private static final String TAG = "BackgroundService";
	private static final String NEW_CALL = "1";

	public BackgroundService() {
		super("Background service");
	}

	public void respond(Context context, String replyToAddress){
		String messages = getUnreadSMSDetails(context, replyToAddress);        
    	sendSMS(replyToAddress,messages,context);
    	Log.i("Messages:",messages);
    	
    	messages = getMissedCallDetails(context);
    	sendSMS(replyToAddress,messages,context);
    	Log.i("calls:",messages);
    }

	private String getMissedCallDetails(Context context) {
		String messages="List of Missed Calls:\n";
		String[] projection = { CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DATE };
         String where = CallLog.Calls.TYPE+"="+CallLog.Calls.MISSED_TYPE+" AND "+ CallLog.Calls.NEW + "=" + NEW_CALL ;          
         Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, projection ,where, null, null);
         
         if (!cursor.moveToFirst() ) {
         	Log.i("Background Service","No missed calls");
         	return "No Missed Calls!";
         }
         
         int indexName = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
         int indexNumber = cursor.getColumnIndex( CallLog.Calls.NUMBER);
         int indexDate = cursor.getColumnIndex(CallLog.Calls.DATE);
         
     	do
        {
     		String dateString ="";
     		if(indexDate >=0 && cursor.getLong(indexDate) > 0 ) {
     			Date date = new Date(cursor.getLong(indexDate));
     			dateString = new SimpleDateFormat(TIMESTAMP_FORMAT, Locale.US).format(date);
     		}
            String str = "Name: " + cursor.getString( indexName )+", Phone No: "+ cursor.getString( indexNumber )+ ", Time: "+ dateString + "\n" ;
            messages += str;
        }
        while( cursor.moveToNext() );
    	
    	cursor.close();
		return messages;
	}

	private String getUnreadSMSDetails(Context context, String replyToAddress) {
		String messages = "List of Unread Messages:\n";
		
		ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query( Uri.parse("content://sms/inbox"), null, "ADDRESS <> '" + replyToAddress + "' AND read = 0", null, null);

        int indexBody = cursor.getColumnIndex("BODY");
        int indexAddr = cursor.getColumnIndex("ADDRESS");

        if ( indexBody < 0 || !cursor.moveToFirst() ) {
        	Log.i(TAG,"No unread messages");
        	return "No Unread Messages!";
        }

        Log.i(TAG,"Unread SMS count:"+cursor.getCount());
    	do
        {
            String str = "Phone No: "+ cursor.getString( indexAddr )+", Message: "
            		+ cursor.getString( indexBody )+ "\n" ;
            messages += str;
        }
        while( cursor.moveToNext() );
    	
    	cursor.close();
		return messages;
	}
	
    private void sendSMS(String phoneNumber, String message, Context context)
    {
    	if(phoneNumber !=null ) {
	    	Log.i("SendSMS","Sending"+message+" to : "+phoneNumber);
	        SmsManager sms = SmsManager.getDefault();
	        sms.sendMultipartTextMessage(phoneNumber, null, sms.divideMessage(message), null, null);
    	}
    }

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i("Background Service","Intent received");
		String respondTo = intent.getStringExtra(RESPOND_TO);
		respond(this.getApplicationContext(),respondTo);
	}
}
