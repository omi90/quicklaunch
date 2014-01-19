package com.newhere.quicklaunch;

import java.io.File;
import java.util.List;
import com.newhere.sidebar.R;
import android.view.animation.Transformation;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

public class SideBarService extends Service implements OnTouchListener,OnKeyListener,AnimationListener{
	private WindowManager wm;
	private WindowManager.LayoutParams paramSidebarVisible,paramSidebarHidden;
	private ImageView prev_image,setting_image;
	private ListView lv;
	private View myview,parent;
	private List<AppInfo> res;
	private Animation openAnim,closeAnim;
	private LinearLayout ll;
	private boolean isSidebarVisible = false;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	public void onCreate(){
		super.onCreate();
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		int minHeight = Math.max(dm.heightPixels, dm.widthPixels);
		int minWidth = minHeight==dm.heightPixels?dm.heightPixels:dm.widthPixels;
		paramSidebarVisible = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				//WindowManager.LayoutParams.FLAG_DIM_BEHIND|
				WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|
				WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
				PixelFormat.TRANSLUCENT);
		paramSidebarVisible.gravity = Gravity.TOP|Gravity.LEFT;
		paramSidebarVisible.x = 0;
		paramSidebarVisible.y = 0;
		paramSidebarVisible.dimAmount = (float) 0.08;
		paramSidebarHidden = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		paramSidebarHidden.gravity = Gravity.CENTER|Gravity.LEFT;
		LayoutInflater li = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		parent = li.inflate(R.layout.layout_list_sidebar, null);
		setting_image = (ImageView)parent.findViewById(R.id.imageView1);
		ll = (LinearLayout)parent.findViewById(R.id.imagelayout);
		ll.getLayoutParams().width = 0;
		//ll.requestLayout();
		myview = parent.findViewById(R.id.layout);
		myview.setMinimumHeight(minHeight);
		lv = (ListView)myview.findViewById(R.id.listView1);
		setting_image.setOnTouchListener(this);
		openAnim = new ResizeAnimation(ll, 75);
		openAnim.setDuration(300);
		openAnim.setAnimationListener(this);
		openAnim.setFillAfter(true);
		closeAnim = new ResizeAnimation(ll, 0);
		closeAnim.setDuration(300);
		closeAnim.setAnimationListener(this);
		closeAnim.setFillAfter(true);
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
				System.out.println("clciked on items");
				AppInfo ai = res.get(pos);
				Intent LaunchApp = getPackageManager().getLaunchIntentForPackage(ai.pname);
				if(LaunchApp!=null){
					startActivity( LaunchApp );
					SideBarService.this.onTouch(prev_image,null);
				}
			}
		});
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> av, View v,
					int pos, long id) {
				System.out.println("long cliked on items");
				AppInfo ai = res.get(pos);
				Intent sharingIntent = new Intent(Intent.ACTION_SEND);
				sharingIntent.setType("application/vnd.android.package-archive");
				sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(ai.sourceDir)));
				Intent i = Intent.createChooser(sharingIntent, "Share via");
				i.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);                     
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			    getApplication().startActivity(i);
			    SideBarService.this.onTouch(prev_image,null);
				return true;
			}
		});
		prev_image = (ImageView)myview.findViewById(R.id.prev_item);
		prev_image.setOnTouchListener(this);
		wm.addView(myview, paramSidebarHidden);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setContentTitle("Message").setContentText("Click to close");
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		startForeground(1337, mBuilder.build());
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		wm.removeView(myview);
		stopForeground(true);
	}
	@Override
	public boolean onTouch(View v,MotionEvent me) {
		// TODO Auto-generated method stub
		System.out.println("inside onTouch");
		if(me == null){
			System.out.println("Any shortcut clicked");
			wm.updateViewLayout(myview, paramSidebarHidden);
			ll.startAnimation(closeAnim);
			final Intent intent = new Intent(getApplicationContext(), LauncherActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			return true;
		}
		else if(me.getAction() == MotionEvent.ACTION_OUTSIDE){
			wm.updateViewLayout(myview, paramSidebarHidden);
			ll.startAnimation(closeAnim);
			isSidebarVisible = false;
			System.out.println("Touched outside");
			return true;
		} 
		else if(v.equals(prev_image) && me.getAction()==MotionEvent.ACTION_DOWN){
			if(!isSidebarVisible){
				wm.updateViewLayout(myview, paramSidebarVisible);
				System.out.println(ll.getVisibility());
				ll.startAnimation(openAnim);
				parent.invalidate();
				isSidebarVisible = true;
				System.out.println("View was not visible");
			}
			else{
				wm.updateViewLayout(myview, paramSidebarHidden);
				ll.startAnimation(closeAnim);
				isSidebarVisible = false;
				System.out.println("View was visible");
			}
			return true;
		}
		return false;
	}
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			SideBarService.this.onTouch(prev_image,null);
	    }
	    return false;
	}
	@Override
	public void onAnimationEnd(Animation animation) {
		System.out.println("Animation ended");
	}
	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		System.out.println("Animation started");
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
class ResizeAnimation extends Animation {
	private int startWidth;
	final int targetWidth;
	View view;
 
	public ResizeAnimation(View view, int targetWidth) {
		this.view = view;
		this.targetWidth = targetWidth;
	}
 
	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		int newWidth = (int) (startWidth + (targetWidth - startWidth) * interpolatedTime);
		view.getLayoutParams().width = newWidth;
		view.requestLayout();
	}
 
	@Override
	public void initialize(int width, int height, int parentWidth, int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		startWidth = view.getWidth();
		System.out.println("start width >> "+startWidth);
	}
 
	@Override
	public boolean willChangeBounds() {
		return true;
	}
}