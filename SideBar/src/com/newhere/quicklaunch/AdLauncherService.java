package com.newhere.quicklaunch;

import java.util.Calendar;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.newhere.sidebar.R;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

public class AdLauncherService extends Service {
	private AdView adView;
	private AdRequest adRequest;
	private boolean isAdLoaded = false;
	private WindowManager wm;
	private LayoutInflater li;
	private LinearLayout ll;
	private Button b ;
	private WindowManager.LayoutParams paramv;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		li = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		ll = (LinearLayout)li.inflate(R.layout.adlauncher_service_layout, null);
		b = (Button) ll.findViewById(R.id.skip);
		adView = new AdView(getApplicationContext());
		adView.setAdUnitId(getResources().getString(R.string.admob_pub_id));
	    adView.setAdSize(AdSize.BANNER);
	    adRequest = new AdRequest.Builder().build();
	    adView.loadAd(adRequest);
	    adView.setAdListener(new AdListener(){
	    	@Override
	    	public void onAdOpened() {
	    		super.onAdOpened();
	    		stopSelfActivity();
	    	}
			@Override
			public void onAdClosed() {
				// TODO Auto-generated method stub
				super.onAdClosed();
				stopSelfActivity();
		    }
			@Override
			public void onAdFailedToLoad(int errorCode) {
				// TODO Auto-generated method stub
				super.onAdFailedToLoad(errorCode);
				stopSelfActivity();
		    }
			@Override
			public void onAdLoaded() {
				// TODO Auto-generated method stub
				b.setText("Skip Ad");
				isAdLoaded = true;
				wm.addView(ll, paramv);
				super.onAdLoaded();
			}
	    });
	    b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isAdLoaded){
					stopSelfActivity();
				}
			}
		});
		ll.addView(adView, 1);
		paramv = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_DIM_BEHIND,
				PixelFormat.TRANSLUCENT);
		paramv.gravity = Gravity.TOP|Gravity.LEFT;
		paramv.x = 0;
		paramv.y = 0;
		paramv.dimAmount = (float) 0.75;
	}
	private void stopSelfActivity() {
		Calendar cur_cal = Calendar.getInstance();
        cur_cal.setTimeInMillis(System.currentTimeMillis());
        cur_cal.add(Calendar.MINUTE, 50);
        Intent i =  new Intent(getApplicationContext(),AdLauncherService.class);
        PendingIntent pi = PendingIntent.getService(getApplicationContext(),
				0,i,
				PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
		am.set(AlarmManager.RTC, cur_cal.getTimeInMillis(), pi);
		stopSelf();
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		adView.setAdListener(null);
		ll.removeView(adView);
		try{
			wm.removeView(ll);
		}
		catch(IllegalArgumentException iex){
		}
	}
}