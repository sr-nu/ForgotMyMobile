package com.forgotMyMobile;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.KeyEvent;

public class CallIntentService extends IntentService {
	
	public CallIntentService(){
		super("CallIntentservice");
	}
		
	@Override
	protected void onHandleIntent(Intent intent) {
    	Log.e("IS","Intent received call..");
    	
    	Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
    	buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
    	sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");
    	
    	 Intent headSetUnPluggedintent = new Intent(Intent.ACTION_HEADSET_PLUG);
    	    headSetUnPluggedintent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
    	    headSetUnPluggedintent.putExtra("state", 0);
    	    headSetUnPluggedintent.putExtra("name", "Headset");
    	    try {
    	        sendOrderedBroadcast(headSetUnPluggedintent, null);
    	    } catch (Exception e) {
    	        // TODO Auto-generated catch block
    	        e.printStackTrace();
    	    }
    	
    	
    	Intent callIntent = new Intent(Intent.ACTION_CALL);
//        callIntent.setData(Uri.parse("tel:+6590694196"));
        callIntent.setData(Uri.parse("tel:+6596278948"));
        callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntent.addFlags(Intent.FLAG_FROM_BACKGROUND);
        super.getApplicationContext().startActivity(callIntent);
	}	
}