package com.forgotMyMobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity{
	public static final String PASSCODE = "PASSCODE";
	private static final String TAG = "MainActivity";

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		Log.d(TAG,"On create start");
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		long passcode = preferences.getLong(PASSCODE, 0);
		EditText passCodeView = (EditText) findViewById(R.id.passcode);
		passCodeView.setText(String.valueOf(passcode));
		
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
			
				if( passcode != null && !passcode.trim().isEmpty()) {
					
					Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this.getApplicationContext()).edit();
					editor.putLong(PASSCODE, Long.parseLong(passcode));
					editor.commit();
					
					showSuccessMessage();
					Log.i("MainActivity", "Preferences saved successfully!");
				} else {
					Toast.makeText(MainActivity.this, "Please set a valid Passcode.", Toast.LENGTH_LONG).show();
				}
			}
			
		};
	}

	protected void showSuccessMessage() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this.getApplicationContext());
		final long passcode = preferences.getLong(PASSCODE, 0);
		
		new AlertDialog.Builder(this)
	    .setTitle("All set! you can relax now..")
	    .setMessage("Your passcode has been saved.\n" +
	    		"To retrieve unread sms' and missed calls, " +
	    		"just sms: " + Html.fromHtml("<b>"+passcode+"</b>") + " from any mobile.")
	    .setPositiveButton(R.string.no, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            dialog.cancel();
	        }
	     })	    
	     .setNegativeButton(R.string.yes, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            MainActivity.this.finish();
		        }
		     })
	    .setIcon(R.drawable.relax_chair)
	     .show();
	}

	@Override
	public void onStart(){
		super.onStart();
		startService(new Intent(this,SmsReceiver.class));
	}

}
