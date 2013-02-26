package com.example;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: srikanthnutigattu
 * Date: 02/08/12
 * Time: 6:59 AM
 * To change this template use File | Settings | File Templates.
 */
public class ServiceStarter extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED) ||
            intent.getAction().equals(Intent.ACTION_USER_PRESENT)){
            Intent i = new Intent("com.example.ServiceStarter");
            i.setClass(context, SmsReceiver.class);
            context.startService(i);
            Toast.makeText(context, "Service Started!!", Toast.LENGTH_SHORT).show();
        }
    }
}
