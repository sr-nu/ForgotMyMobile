package com.forgotMyMobile.listeners;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootupListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
		
        if(action.equals(Intent.ACTION_BOOT_COMPLETED) || action.equals(Intent.ACTION_USER_PRESENT)){		
            Intent i = new Intent("com.forgotMyMobile.BootupListener");
            i.setClass(context, SmsReceiver.class);
            context.startService(i);            
        }
        
    }
}
