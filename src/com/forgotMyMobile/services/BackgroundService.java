package com.forgotMyMobile.services;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import com.forgotMyMobile.helpers.PreferenceHelper;
import com.forgotMyMobile.listeners.SmsReceiver;

public class BackgroundService extends IntentService {
    private static final String TIMESTAMP_FORMAT = "dd/MM/yy hh:mm:ss";
	public static final String RESPOND_TO = "RESPOND_TO";
	private static final String TAG = "BackgroundService";
	private static final String NEW_CALL = "1";
	public static final String COMMAND = "command";
	private static final String DEFAULT = "";
	private static final String HELP = "help";
	private static final String AUTO_ON = "auto on";
	private static final String AUTO_OFF = "auto off";

	public BackgroundService() {
		super("Background service");
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i("Background Service","Intent received");
		String respondTo = intent.getStringExtra(RESPOND_TO);
		String command = intent.getStringExtra(COMMAND);
		
		if(command != null && command.trim().equalsIgnoreCase(SmsReceiver.CTRL_MSG)) {
			handleLoop(respondTo);
			return;
		}
		
		if(command == null || command.trim().equalsIgnoreCase(DEFAULT)) {
			respondDefault(getApplicationContext(),respondTo);
		} else if(command.trim().equalsIgnoreCase(HELP)) {
			respondHelp(getApplicationContext(),respondTo);
		} else if(command.trim().equalsIgnoreCase(AUTO_ON)) {
			respondAutoOn(respondTo);
		} else if(command.trim().equalsIgnoreCase(AUTO_OFF)) {
			respondAutoOff(respondTo);
		} 
	}

	private void handleLoop(String respondTo) {
		PreferenceHelper.setAutoFwd(getApplicationContext(), false);
		sendSMS(respondTo, "CANNOT SET AUTO FORWARD TO SAME NUMBER\nAUTO FORWARD STOPPED \n\n\n send '<passcode> HELP' for additional commands\n", getApplicationContext());		
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
        Cursor cursor = contentResolver.query( Uri.parse("content://sms/inbox"), null, "read = 0", null, null);

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



	private void respondAutoOff(String respondTo) {
		PreferenceHelper.setAutoFwd(getApplicationContext(), false);
		sendSMS(respondTo, "AUTO FORWARD STOPPED \n\n\n send '<passcode> HELP' for additional commands", getApplicationContext());
	}


	private void respondAutoOn(String respondTo) {
		if(!sameAsThisPhoneNumber(respondTo)) {
			PreferenceHelper.saveAutoFwdNumberIfRequired(getApplicationContext(), respondTo);
			PreferenceHelper.setAutoFwd(getApplicationContext(), true);
			sendSMS(respondTo, "AUTO FORWARD SET \n\n\n send '<passcode> HELP' for additional commands\n" +
				SmsReceiver.CTRL_MSG, getApplicationContext());
		} else {
			sendSMS(respondTo, "CANNOT SET AUTO FORWARD TO SAME NUMBER! \n\n\n send '<passcode> HELP' for additional commands", getApplicationContext());
		}
	}
	
	private boolean sameAsThisPhoneNumber(String respondTo) {
		TelephonyManager phoneManager = (TelephonyManager) 
			    getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
			String phoneNumber = phoneManager.getLine1Number();
			Log.e("BackgroundService", "Are same numbers:"+respondTo.equals(phoneNumber));
			return respondTo.equals(phoneNumber);
	}

	private void respondHelp(Context context, String respondTo) {
		String message = "Please send the message in following format:\n" +
				"<passcode> command \n\n" +
				"following commands are permitted\n" +
				"<passcode> HELP - for this help message\n" +
				"<passcode> - for list of missed calls and new unread sms messages\n" +
				"<passcode> AUTO ON - set current number as auto forward number for future messages\n" +
				"<passcode> AUTO OFF - set auto forwarding off, new messages will no longer be sent automatically\n";
		sendSMS(respondTo,message,context);		
	}
	
	public void respondDefault(Context context, String replyToAddress){
		sendMessages(context, replyToAddress);    	
    	sendMissedCalls(context, replyToAddress);
    }



	private void sendMissedCalls(Context context, String replyToAddress) {
		String messages = getMissedCallDetails(context);
		messages += "\n\n\n send '<passcode> HELP' for additional commands";
    	sendSMS(replyToAddress,messages,context);
    	Log.i("calls:",messages);
	}

	private void sendMessages(Context context, String replyToAddress) {
		String messages = getUnreadSMSDetails(context, replyToAddress);
		messages += "\n\n\n send '<passcode> HELP' for additional commands";
    	sendSMS(replyToAddress,messages,context);
    	Log.i("Messages:",messages);
	}
}
