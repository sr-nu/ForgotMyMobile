package com.forgotMyMobile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity{
	public static final String PASSCODE = "PASSCODE";
	private static final String AUTO_FWD = "AUTO_FWD";
	private static final String TAG = "MainActivity";

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		Log.d(TAG,"On create start");
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		int passcode = preferences.getInt(PASSCODE, 0);
		EditText passCodeView = (EditText) findViewById(R.id.passcode);
		passCodeView.setText(String.valueOf(passcode));
		
		boolean autoForward = preferences.getBoolean(AUTO_FWD, false);
		CheckBox autoFwdCheckBox = (CheckBox) findViewById(R.id.autoForward);
		autoFwdCheckBox.setChecked(autoForward);
		
		Button saveButton = (Button) findViewById(R.id.savePreferences);
		saveButton.setOnClickListener(setSaveButtonListener());
		Log.d(TAG,"On create completed");
	}

	private OnClickListener setSaveButtonListener() {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				
			EditText passCodeView = (EditText) MainActivity.this.findViewById(R.id.passcode);
			String passcode = passCodeView.getText().toString();
			
			CheckBox autoFwdCheckBox = (CheckBox) findViewById(R.id.autoForward);
			boolean needAutoFwd = autoFwdCheckBox.isChecked();
			
			
				if( passcode != null && !passcode.trim().isEmpty()) {
					
					Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this.getApplicationContext()).edit();
					editor.putInt(PASSCODE, Integer.parseInt(passcode));
					editor.putBoolean(AUTO_FWD, needAutoFwd);
					editor.commit();
					
					Toast.makeText(MainActivity.this, "Preferences saved successfully!", Toast.LENGTH_LONG).show();
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
