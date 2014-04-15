package com.forgotMyMobile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		Button smsListButton = (Button) findViewById(R.id.UpdateList);
		smsListButton.setOnClickListener(fetchSMSListListener());
		
		Button passCodeButton = (Button) findViewById(R.id.setPasscode);
		passCodeButton.setOnClickListener(setPassCodeListener());
		
		int passcode = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("PASSCODE", 0);
		EditText passCodeView = (EditText) MainActivity.this.findViewById(R.id.passcode);
		passCodeView.setText(String.valueOf(passcode));
	}

	private OnClickListener setPassCodeListener() {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
			EditText passCodeView = (EditText) MainActivity.this.findViewById(R.id.passcode);
			String passcode = passCodeView.getText().toString();
			
				if( passcode != null && !passcode.trim().isEmpty()) {
					
					Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this.getApplicationContext()).edit();
					editor.putInt("PASSCODE", Integer.parseInt(passcode));
					editor.commit();
				}
			}
			
		};
	}

	private OnClickListener fetchSMSListListener() {
		return 	new OnClickListener() {

			@Override
			public void onClick(View v) {
				final Uri SMS_INBOX = Uri.parse("content://sms/inbox");

				Cursor c = getContentResolver().query(SMS_INBOX, null, "read = 0", null, null);
				int unreadMessagesCount = c.getCount();
				c.close();
				Toast.makeText(getApplicationContext(), "Unread SMS count:"+unreadMessagesCount, Toast.LENGTH_SHORT).show();
				
				 String[] projection = { CallLog.Calls.CACHED_NAME, CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DATE };
		         String where = CallLog.Calls.TYPE+"="+CallLog.Calls.MISSED_TYPE+" AND "+ CallLog.Calls.NEW + "=1" ;          
		         Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, projection ,where, null, null);
		         Toast.makeText(getApplicationContext(), "Missed call count:"+cursor.getCount(), Toast.LENGTH_LONG).show();
		         cursor.close();
			}

		};
	}


	@Override
	public void onStart(){
		super.onStart();
		startService(new Intent(this,SmsReceiver.class));
	}

}
