package com.forgotMyMobile.helpers;

import android.content.Context;
import android.content.Intent;

import com.forgotMyMobile.services.BackgroundService;

public class CommandHandler {

	public static void handleCommand(Context context, String fromNumber, String command) {
		PreferenceHelper.saveAutoFwdNumberIfRequired(context, fromNumber);
		delegateToService(context, fromNumber,command);
	}

	private static void delegateToService(Context context, String fromNumber, String command) {
		Intent i = new Intent(context,BackgroundService.class);
		i.putExtra(BackgroundService.RESPOND_TO, fromNumber);
		i.putExtra(BackgroundService.COMMAND, command);
		context.startService(i);
	}

}
