package com.newhere.sidebar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.hardware.Camera.PreviewCallback;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

public class SideBarService extends Service implements OnClickListener,OnKeyListener,AnimationListener{
	private WindowManager wm;
	private WindowManager.LayoutParams param,param2;
	private ImageView next_image,prev_image,setting_image;
	private ListView lv;
	private View myview,parent;
	private List<AppInfo> res;
	private Animation openAnim,closeAnim;
	private TranslateAnimation to_left,to_right;
	private LinearLayout ll;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	public void onCreate(){
		super.onCreate();
		System.out.println("service created");
		openAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.animator.open_anim);
		closeAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.animator.close_anim);
		closeAnim.setAnimationListener(this);
		to_left = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -3, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
		to_right = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -3, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
		to_left.setDuration(500);
		to_left.setFillAfter(true);
		to_right.setDuration(500);
		to_right.setFillAfter(true);
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		int minHeight = Math.max(dm.heightPixels, dm.widthPixels);
		int minWidth = minHeight==dm.heightPixels?dm.heightPixels:dm.widthPixels;
		param = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_DIM_BEHIND|WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
				PixelFormat.TRANSLUCENT);
		param.gravity = Gravity.TOP|Gravity.LEFT;
		param.x = 0;
		param.y = 0;
		param.dimAmount = (float) 0.2;
		next_image = new ImageView(this);
		next_image.setImageResource(R.drawable.drag);
		param2 = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		param2.gravity = Gravity.CENTER|Gravity.LEFT;
		//param2.x = 0;
		//param2.y = minHeight/2 - next_image.getHeight()/2;
		LayoutInflater li = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		parent = li.inflate(R.layout.layout_list_sidebar, null);
		setting_image = (ImageView)parent.findViewById(R.id.imageView1);
		ll = (LinearLayout)parent.findViewById(R.id.imagelayout);
		myview = parent.findViewById(R.id.layout);
		parent.setMinimumWidth(minWidth);
		myview.setMinimumHeight(minHeight);
		lv = (ListView)myview.findViewById(R.id.listView1);
		setting_image.setOnClickListener(this);
		final PackageManager pm = getPackageManager();
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		res = LauncherActivity.getPrefInstalledApps(false, getPackageManager(), getApplicationContext());
		lv.setAdapter(new AppAdapter(this, R.layout.listview, res));
		lv.setClickable(true);
		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				// TODO Auto-generated method stub
				AppInfo ai = res.get(pos);
				Intent LaunchApp = getPackageManager().getLaunchIntentForPackage(ai.pname);
				if(LaunchApp!=null)
					startActivity( LaunchApp );
			}
		});
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> av, View v,
					int pos, long id) {
				AppInfo ai = res.get(pos);
				Intent sharingIntent = new Intent(Intent.ACTION_SEND);
				sharingIntent.setType("application/vnd.android.package-archive");
				sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(ai.sourceDir)));
				Intent i = Intent.createChooser(sharingIntent, "Share via");
				i.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);                     
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    getApplication().startActivity(i);
			    SideBarService.this.onClick(prev_image);
				return true;
			}
		});
		prev_image = (ImageView)myview.findViewById(R.id.prev_item);
		parent.setOnClickListener(this);
		parent.setOnKeyListener(this);
		next_image.setOnClickListener(this);
		prev_image.setOnClickListener(this);
		wm.addView(next_image, param2);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setContentTitle("Message").setContentText("Click to close");
		//Intent i=new Intent(this, FakePlayer.class);
		//i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		//PendingIntent pi=PendingIntent.getActivity(this, 0, i, 0);
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		startForeground(1337, mBuilder.build());
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		System.out.println("removing view");
		super.onDestroy();
		if (next_image != null)
			wm.removeView(next_image);
		stopForeground(true);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.equals(setting_image)){
			final Intent intent = new Intent(getApplicationContext(), LauncherActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			return;
		}
		else if(v.equals(parent)){
			ll.startAnimation(closeAnim);
			prev_image.startAnimation(to_left);
			return;
		}
		else if(v.equals(next_image)){
			wm.removeView(next_image);
			wm.addView(parent, param);
			prev_image.startAnimation(to_right);
			ll.startAnimation(openAnim);
		}
		else if(v.equals(prev_image)){
			ll.startAnimation(closeAnim);
			prev_image.startAnimation(to_left);
		}
	}
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		System.out.println("key pressed");
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			SideBarService.this.onClick(prev_image);
	    }
	    return false;
	}
	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		if(animation.equals(closeAnim)){
			Handler h = new Handler();
			h.post(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					wm.removeView(parent);
					wm.addView(next_image, param2);
				}
			});
		}
	}
	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}
}
class AppInfo {
    String appname = "";
    String pname = "";
    String versionName = "";
    int versionCode = 0;
    String iconLoc;
    Drawable icon;
    String sourceDir = "";
}
