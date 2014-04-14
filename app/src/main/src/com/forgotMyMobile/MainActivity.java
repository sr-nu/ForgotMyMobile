package com.forgotMyMobile;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		Button smsListButton = (Button) findViewById(R.id.UpdateList);
		smsListButton.setOnClickListener(fetchSMSListListener());
	}

	private OnClickListener fetchSMSListListener() {
		return 	new OnClickListener() {

			@Override
			public void onClick(View v) {
				final Uri SMS_INBOX = Uri.parse("content://sms/inbox");

				Cursor c = getContentResolver().query(SMS_INBOX, null, "read = 0", null, null);
				int unreadMessagesCount = c.getCount();
				c.deactivate();
				Toast.makeText(getApplicationContext(), "Unread SMS count:"+unreadMessagesCount, Toast.LENGTH_LONG).show();

			}

		};
	}


	@Override
	public void onStart(){
		super.onStart();
		startService(new Intent(this,SmsReceiver.class));
	}

}
