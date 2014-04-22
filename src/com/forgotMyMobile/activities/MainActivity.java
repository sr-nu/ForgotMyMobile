package com.forgotMyMobile.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.forgotMyMobile.helpers.PreferenceHelper;
import com.forgotMyMobile.listeners.SmsReceiver;
import com.mymobile.forgotmymobile.R;

public class MainActivity extends Activity{
    private static final String TAG = "MainActivity";

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		Log.d(TAG,"On create start");
        EditText passCodeView = (EditText) findViewById(R.id.passcode);
		passCodeView.setText(PreferenceHelper.getPassCode(getApplicationContext()));

        CheckBox autoFwdCheckBox = (CheckBox) findViewById(R.id.autoForward);
		autoFwdCheckBox.setChecked(PreferenceHelper.isAutoForwardEnabled(getApplicationContext()));
		
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
                    PreferenceHelper.saveSettings(passcode, needAutoFwd, MainActivity.this);
					showSuccessMessage();
					Log.i("MainActivity", "Preferences saved successfully!");
				} else {
					Toast.makeText(MainActivity.this, "Please set a valid Pass code.", Toast.LENGTH_LONG).show();
				}
			}
			
		};
	}

    protected void showSuccessMessage() {
		final String passCode = PreferenceHelper.getPassCode(MainActivity.this);
		
		new AlertDialog.Builder(this)
	    .setTitle("All set! you can relax now..")
	    .setMessage("Your pass code has been saved.\n" +
	    		"To retrieve unread sms' and missed calls, " +
	    		"just sms: " + Html.fromHtml("<b>"+passCode+"</b>") + " from any mobile.")
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
