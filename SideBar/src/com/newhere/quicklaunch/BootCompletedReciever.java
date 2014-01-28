package com.newhere.quicklaunch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootCompletedReciever extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent arg1) {
		// TODO Auto-generated method stub
		final Intent intent = new Intent(context, SideBarService.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startService(intent);
	}
}