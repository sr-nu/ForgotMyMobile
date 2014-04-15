package com.forgotMyMobile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity{
	public static final String PASSCODE = "PASSCODE";

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		
		Button passCodeButton = (Button) findViewById(R.id.setPasscode);
		passCodeButton.setOnClickListener(setPassCodeListener());
		
		int passcode = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(PASSCODE, 0);
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
					editor.putInt(PASSCODE, Integer.parseInt(passcode));
					editor.commit();
					
					Toast.makeText(MainActivity.this, "Passcode saved successfully!", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(MainActivity.this, "Please set a valid Passcode.", Toast.LENGTH_LONG).show();
				}
			}
			
		};
	}

	@Override
	public void onStart(){
		super.onStart();
		startService(new Intent(this,SmsReceiver.class));
	}

}
