package com.forgotMyMobile.helpers;

import com.forgotMyMobile.activities.MainActivity;
import com.mymobile.forgotmymobile.R;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;

public class PreferenceHelper {

    public static final String AUTO_FWD = "AUTO_FWD";
    public static final String PASS_CODE = "PASS_CODE";
    public static final String SMS_EXTRA_NAME = "pdus";
    public static final String AUTO_FWD_TO = "AUTO_FWD_TO";
	private static final int NOTIFICATION_DEFAULT = 1;

    public static String getPassCode(final Context context) {
        return String.valueOf(PreferenceManager.getDefaultSharedPreferences(context).getLong(PASS_CODE, 0));
    }

    public static boolean isAutoForwardEnabled(final Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(AUTO_FWD, false);
    }

    public static void saveSettings(String passcode, boolean needAutoFwd, final Context context) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putLong(PASS_CODE, Long.parseLong(passcode));
        editor.putBoolean(AUTO_FWD, needAutoFwd);
        editor.commit();
    }

    public static void saveAutoFwdNumberIfRequired(Context context, String fromNumber) {
        if(isAutoForwardEnabled(context)) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
            editor.putString(AUTO_FWD_TO, fromNumber);
            editor.commit();
        }
    }

    public static String getAutoFwdNumber(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(AUTO_FWD_TO,"");
    }

	public static void setAutoFwd(Context context, boolean needAutoFwd) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(AUTO_FWD, needAutoFwd);
        editor.commit();
        
        Log.e("PrefHelper","Auto:"+needAutoFwd);
        if(needAutoFwd) {
        	displayNotification(context);
        } else {
        	hideNotification(context);
        }
	}

	private static void hideNotification(Context context) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(NOTIFICATION_DEFAULT);
	}

	private static void displayNotification(Context context) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent i = new Intent(context, MainActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		i.putExtra(MainActivity.STOP_AUTO_FWD, true);
		
		Builder builder = new NotificationCompat.Builder(context)
		.setSmallIcon(R.drawable.alert)
		.setContentTitle("AUTO FORWARDING! [TAP TO STOP]")
		.setContentText("All details forwarding to : " + getAutoFwdNumber(context) + "\n" + "TAP TO STOP!")
		.setAutoCancel(false)
		.setOngoing(true)
		.setTicker("Auto Forward Set")    
		.setContentIntent(PendingIntent.getActivity(context, NOTIFICATION_DEFAULT, i,0));
		
		notificationManager.notify(NOTIFICATION_DEFAULT, builder.build());
		
	}
}
