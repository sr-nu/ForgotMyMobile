package com.forgotMyMobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootupListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||
            intent.getAction().equals(Intent.ACTION_USER_PRESENT)){
            Intent i = new Intent("com.example.ServiceStarter");
            i.setClass(context, SmsReceiver.class);
            context.startService(i);
        }
    }


}
