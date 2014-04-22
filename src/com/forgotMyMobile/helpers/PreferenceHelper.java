package com.forgotMyMobile.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferenceHelper {

    public static final String AUTO_FWD = "AUTO_FWD";
    public static final String PASS_CODE = "PASS_CODE";
    public static final String SMS_EXTRA_NAME = "pdus";
    public static final String AUTO_FWD_TO = "AUTO_FWD_TO";

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
}
