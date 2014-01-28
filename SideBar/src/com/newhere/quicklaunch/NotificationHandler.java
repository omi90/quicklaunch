package com.newhere.quicklaunch;

import com.newhere.sidebar.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class NotificationHandler extends Service{
	private static boolean isHidden = false;
	public static final int requestCode = 10001; 
	private static String hidden = "Click here to Show QuickLaunch";
	private static String shown =  "Click here to hide QuickLaunch";
	
	public static boolean isHidden() {
		return isHidden;
	}
	public static void setHidden(boolean isHidden) {
		NotificationHandler.isHidden = isHidden;
	}
	public static String getString() {
		return isHidden?hidden:shown;
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		System.out.println("starting service");
		if(isHidden){
			NotificationHandler.setHidden(false);
			createNotification(getApplicationContext());
			startService(new Intent(getApplicationContext(),SideBarService.class));
		}
		else{
			NotificationHandler.setHidden(true);
			createNotification(getApplicationContext());
			stopService(new Intent(getApplicationContext(),SideBarService.class));
		}
		return super.onStartCommand(intent, flags, startId);
	}
	public static void createNotification(Context ctx){
		PendingIntent pi = PendingIntent.getService(ctx, 
				0, 
				new Intent(ctx,NotificationHandler.class), 
				PendingIntent.FLAG_UPDATE_CURRENT);
		NotificationManager nm = (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
		NotificationCompat.Builder mBuilder =
			    new NotificationCompat.Builder(ctx)
			    .setSmallIcon(R.drawable.qlicon)
			    .setContentTitle("Quick Launch")
			    .setContentText(NotificationHandler.getString())
				.setContentIntent(pi)
				.setOngoing(true);
		nm.notify(NotificationHandler.requestCode, mBuilder.build());
	}
}