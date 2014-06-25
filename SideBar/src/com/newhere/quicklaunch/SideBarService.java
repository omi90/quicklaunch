package com.newhere.quicklaunch;

import java.io.File;
import android.view.View;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import com.newhere.sidebar.R;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.gesture.GestureOverlayView;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
@SuppressLint("NewApi")
public class SideBarService extends Service implements OnTouchListener,OnKeyListener,AnimationListener,OnClickListener{
	private ViewGroup.LayoutParams lp;
	private WindowManager wm;
	private WindowManager.LayoutParams paramSidebarVisible,paramSidebarHidden;
	private ImageView prev_image,setting_image,draggableView;
	private ImageView phone,sms,camera,flash;
	private FrameLayout cont;
	private ListView lv;
	private LinearLayout parent;
	private View myview,sidebarMenu;
	private List<AppInfo> res;
	private Animation openAnim,closeAnim;
	private LinearLayout ll;
	private boolean isSidebarVisible = false;
	private DisplayMetrics dm;
	private ImageView deleteIm,shareIm,uninstallIm;
	private LayoutInflater li;
	private AppAdapter listAdapter;
	private FrameLayout longClickMenu;
	private AppInfo longClickMenuData;
	private TextView drawCharMsg;
	private int pos = 0;
	private boolean longClickMenuVisible=false;
	private int currentapiVersion = android.os.Build.VERSION.SDK_INT;
	private ImageView searchButton;
	private EditText searchText;
	private GestureOverlayView gOV;
	private CharacterSearchListener csl;
	private FlashHandler fh;
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		wm.getDefaultDisplay().getMetrics(dm);
		gOV.getLayoutParams().width = dm.widthPixels-100;
		super.onConfigurationChanged(newConfig);
	}
	public void onCreate(){
		super.onCreate();
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		int minHeight = Math.max(dm.heightPixels, dm.widthPixels);
		int minWidth = minHeight==dm.heightPixels?dm.heightPixels:dm.widthPixels;
		li = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		cont = (FrameLayout)li.inflate(R.layout.layout_list_sidebar, null);
		parent = (LinearLayout) cont.findViewById(R.id.layout);
		sidebarMenu = li.inflate(R.layout.listview_menu_layout, null);
		sidebarMenu.setMinimumHeight(minHeight);
		sidebarMenu.setMinimumWidth(minWidth);
		setting_image = (ImageView)parent.findViewById(R.id.imageView1);
		ll = (LinearLayout)parent.findViewById(R.id.imagelayout);
		ll.getLayoutParams().width = 0;
		myview = cont.findViewById(R.id.drag_layer);
		myview.setMinimumHeight(minHeight);
		lv = (ListView)myview.findViewById(R.id.listView1);
		setting_image.setOnTouchListener(this);
		openAnim = new ResizeAnimation(ll, 
				(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics()));
		openAnim.setDuration(300);
		openAnim.setAnimationListener(this);
		openAnim.setFillAfter(true);
		closeAnim = new ResizeAnimation(ll, 0);
		closeAnim.setDuration(300);
		closeAnim.setAnimationListener(this);
		closeAnim.setFillAfter(true);
		res = LauncherActivity.getPrefInstalledApps(false, getPackageManager(), getApplicationContext());
		listAdapter = new AppAdapter(this, R.layout.listview, res);
		lv.setAdapter(listAdapter);
		lv.setTextFilterEnabled(true);
		lv.setClickable(true);
		lv.setOnItemClickListener(new SidebarListItemClick());
		lv.setOnItemLongClickListener(new SidebarListItemLongClick());
		prev_image = (ImageView)myview.findViewById(R.id.prev_item);
		prev_image.setOnTouchListener(this);
		gOV = (GestureOverlayView) cont.findViewById(R.id.gestureView);//new GestureOverlayView(getApplicationContext());
		gOV.getLayoutParams().width = dm.widthPixels-100;
		gOV.setGestureColor(Color.CYAN);
		gOV.setUncertainGestureColor(Color.RED);
		csl = new CharacterSearchListener(SideBarService.this);
		gOV.addOnGesturePerformedListener(csl);
		myview.setOnTouchListener(this);
		paramSidebarVisible = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_DIM_BEHIND,
				PixelFormat.TRANSLUCENT);
		paramSidebarVisible.gravity = Gravity.TOP|Gravity.LEFT;
		paramSidebarVisible.x = 0;
		paramSidebarVisible.y = 0;
		paramSidebarVisible.dimAmount = (float) 0.75;
		paramSidebarHidden = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				prev_image.getLayoutParams().height,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		paramSidebarHidden.gravity = Gravity.CENTER|Gravity.LEFT;
		paramSidebarHidden.dimAmount = (float) 0.0;
		wm.addView(myview, paramSidebarHidden);
		NotificationHandler.setHidden(false);
		NotificationHandler.createNotification(getApplicationContext(),SideBarService.this);
		setUpMenuImages();
		phone = (ImageView)cont.findViewById(R.id.phone);
		phone.setOnClickListener(this);
		sms = (ImageView)cont.findViewById(R.id.messaging);
		sms.setOnClickListener(this);
		camera = (ImageView)cont.findViewById(R.id.camera);
		camera.setOnClickListener(this);
		flash = (ImageView)cont.findViewById(R.id.flash);
		flash.setOnClickListener(this);
		FrameLayout.LayoutParams flp =new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT,Gravity.RIGHT|Gravity.BOTTOM);
	    drawCharMsg = (TextView) cont.findViewById(R.id.drawcharmsg);
		searchButton = (ImageView) cont.findViewById(R.id.searchButton);
		searchButton.setOnTouchListener(this);
		searchText = (EditText)cont.findViewById(R.id.searchText);
		searchText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				csl.setSearchString(s.toString());
				listAdapter.getFilter().filter(s.toString().toLowerCase());
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}
		});
	}
	private void setUpMenuImages(){
		if (currentapiVersion >= 11){
			deleteIm = (ImageView)sidebarMenu.findViewById(R.id.delete);
			shareIm = (ImageView)sidebarMenu.findViewById(R.id.share);
			uninstallIm = (ImageView)sidebarMenu.findViewById(R.id.uninstall);
			MyDragListener mdl = new MyDragListener();
			deleteIm.setOnDragListener(mdl);
			shareIm.setOnDragListener(mdl);
			uninstallIm.setOnDragListener(mdl);
		}
		else{
			longClickMenu =  (FrameLayout) li.inflate(R.layout.menu_layout_sidebar, null);
			deleteIm = (ImageView)longClickMenu.findViewById(R.id.delete);
			shareIm = (ImageView)longClickMenu.findViewById(R.id.share);
			uninstallIm = (ImageView)longClickMenu.findViewById(R.id.uninstall);
			deleteIm.setOnClickListener(this);
			uninstallIm.setOnClickListener(this);
			shareIm.setOnClickListener(this);
		}
	}
	private void removeMenuImages(){
		synchronized (sidebarMenu) {
			parent.removeView(sidebarMenu);
		}
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		wm.removeView(myview);
		stopForeground(true);
	}
	public void closeSidebar(){
		SideBarService.this.onTouch(prev_image,null);
		if(fh!=null){
			fh.turnOffFlash();
			fh.releaseCamera();
		}
		flash.setImageResource(R.drawable.tourch);
		fh = null;
	}
	@Override
	public boolean onTouch(View v,MotionEvent me) {
		// TODO Auto-generated method stub
		if(me == null && v.equals(prev_image)){
			ll.startAnimation(closeAnim);
			return true;
		}
		else if(v.equals(prev_image) && me.getAction()==MotionEvent.ACTION_DOWN){
			if(!isSidebarVisible){
				ll.startAnimation(openAnim);
				parent.invalidate();
				isSidebarVisible = true;
			}
			else{
				ll.startAnimation(closeAnim);
				isSidebarVisible = false;
			}
			return true;
		}
		else if(v.equals(setting_image)){
			ll.startAnimation(closeAnim);
			final Intent intent = new Intent(getApplicationContext(), LauncherActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			return true;
		}
		else if(v.equals(searchButton)){
			searchText.setText("");
			csl.clear();
		}
		else if(v.equals(myview) && isSidebarVisible){
			if(longClickMenuVisible){
				removeLongClickMenu();
				return true;
			}
			ll.startAnimation(closeAnim);
			isSidebarVisible = false;
			return true;
		} 
		return false;
	}
	private void removeLongClickMenu() {
		// TODO Auto-generated method stub
		cont.removeView(longClickMenu);
		cont.invalidate();
		longClickMenuVisible = false;
	}
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			closeSidebar();
	    }
		return false;
	}
	@Override
	public void onAnimationEnd(Animation animation) {
		if(animation.equals(openAnim)){
			cont.requestLayout();
		}
		else{
			searchText.setText("");
			wm.updateViewLayout(myview, paramSidebarHidden);
			gOV.setVisibility(View.GONE);
			ll.setVisibility(View.INVISIBLE);
			myview.getLayoutParams().width = LayoutParams.WRAP_CONTENT;
			myview.setOnTouchListener(null);
			myview.requestLayout();
			parent.invalidate();
			Calendar cur_cal = Calendar.getInstance();
            cur_cal.setTimeInMillis(System.currentTimeMillis());
            cur_cal.add(Calendar.SECOND, 40);
            Intent i =  new Intent(getApplicationContext(),AdLauncherService.class);
            PendingIntent pi = PendingIntent.getService(getApplicationContext(),
					0,i,
					PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
			am.set(AlarmManager.RTC, cur_cal.getTimeInMillis(), pi);
		}
	}
	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
	}
	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		if(animation == closeAnim){
			ObjectAnimator anim1 = ObjectAnimator.ofFloat(gOV, "alpha", 1.0f,0.0f);
			ValueAnimator anim2 = ValueAnimator.ofInt(dm.widthPixels-120,0);
			anim2.addUpdateListener(new AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					// TODO Auto-generated method stub
					gOV.getLayoutParams().width = (Integer)animation.getAnimatedValue();
				}
			});
			AnimatorSet animSet = new AnimatorSet();
			animSet.playTogether(anim1, anim2);
			animSet.setDuration(250);
			animSet.start();
			cont.setBackgroundColor(Color.TRANSPARENT);
		}
		else if(animation == openAnim){
			gOV.setVisibility(View.VISIBLE);
			wm.updateViewLayout(myview, paramSidebarVisible);
			int bgColor = Preferences.getSidebarColor(getApplicationContext());
			ll.setBackgroundColor(bgColor);
			int colorLowOpac = getContrastColor(bgColor,true);
			ll.setVisibility(View.VISIBLE);
			myview.getLayoutParams().width =LayoutParams.MATCH_PARENT;
			myview.setOnTouchListener(this);
			myview.requestLayout();
			ObjectAnimator anim1 = ObjectAnimator.ofFloat(gOV, "alpha", 0.2f,1.0f);
			ValueAnimator anim2 = ValueAnimator.ofInt(10,dm.widthPixels-120);
			anim2.addUpdateListener(new AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					// TODO Auto-generated method stub
					gOV.getLayoutParams().width = (Integer)animation.getAnimatedValue();
				}
			});
			anim2.addListener(new AnimatorListenerAdapter(){
			    @Override
			    public void onAnimationEnd(Animator animation){
			       gOV.getLayoutParams().width = dm.widthPixels-120;
			       gOV.invalidate();
			       gOV.requestLayout();
			    }
			});
			AnimatorSet animSet = new AnimatorSet();
			animSet.playTogether(anim1, anim2);
			animSet.setDuration(250);
			animSet.start();
		}
	}
	@SuppressLint("NewApi")
	class SidebarListItemLongClick implements OnItemLongClickListener{
		@SuppressLint("NewApi")
		@Override
		public boolean onItemLongClick(AdapterView<?> av, View v,
				int pos, long id) {
			Vibrator vibr = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			vibr.vibrate(50);
			AppInfo ai = (AppInfo)av.getItemAtPosition(pos);
			if (currentapiVersion >= 11){
				ClipData.Item i = new ClipData.Item(ai.sourceDir);
				ClipData.Item i2 = new ClipData.Item(ai.appname);
				ClipData.Item i3 = new ClipData.Item(""+pos);
				ClipData.Item i4 = new ClipData.Item(ai.pname);
				ClipData data = ClipData.newPlainText("package", ai.pname);
				data.addItem(i);
				data.addItem(i2);
				data.addItem(i3);
				data.addItem(i4);
				DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
				v.startDrag(data, shadowBuilder, v, 0);
				ValueAnimator anim = ValueAnimator.ofFloat(0f,50f);
				anim.addUpdateListener(new AnimatorUpdateListener() {
					@Override
					public void onAnimationUpdate(ValueAnimator animation) {
						// TODO Auto-generated method stub
						Float val = (Float) animation.getAnimatedValue();
						int ival = val.intValue();
						sidebarMenu.setPadding(ival, ival, ival, ival);
					}
				});
				parent.addView(sidebarMenu);
				anim.setDuration(200);
				anim.start();
				cont.requestLayout();
	      		cont.invalidate();
				return true;
			} 
			else{
				longClickMenuData = ai;
				SideBarService.this.pos = pos;
				int[] loc = new int[2];
				v.getLocationOnScreen(loc);
				int vWidth = v.getWidth();
				int vHeight = v.getHeight();
				DisplayMetrics dm = new DisplayMetrics();
				wm.getDefaultDisplay().getMetrics(dm);
				int minHeight = dm.heightPixels;
				int h = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
				int w = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
				FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(w,h,Gravity.TOP);
				int posLeft = v.getWidth()+5;
				int posTop = loc[1] - (h/2)+ v.getHeight()/2;
				if((posTop+h)> minHeight){
					posTop = posTop - ((posTop+h)- minHeight);
					flp.bottomMargin = 5;
				}
				flp.leftMargin = posLeft;
				flp.topMargin = posTop;
				//flp.setMargins(posLeft, posTop, 0, 0);
				if(longClickMenuVisible)
					cont.removeView(longClickMenu);
				cont.addView(longClickMenu, flp);
				longClickMenu.setLayoutParams(flp);
				longClickMenu.requestLayout();
				longClickMenuVisible = true;
				return true;
			}
		}
	}
	private void shareIntent(final String sourceDir){
		closeSidebar();
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		sharingIntent.setType("application/vnd.android.package-archive");
		sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(sourceDir)));
		Intent i = Intent.createChooser(sharingIntent, "Share via");
		i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		i.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);        
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getApplication().startActivity(i);
	}
	private void uninstallIntent(String packageName){
		Uri packageUri =Uri.parse("package:"+packageName);
		Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
		uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);                     
		uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try{
			startActivity(uninstallIntent);
		}
		catch(Exception ex){
			Toast.makeText(getApplicationContext(), "Can not uninstall application.", Toast.LENGTH_LONG);
		}
		closeSidebar();
	}
	class SidebarListItemClick implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int pos,
				long id) {
			// TODO Auto-generated method stub
			AppInfo ai = (AppInfo)parent.getItemAtPosition(pos);
			Intent LaunchApp = getPackageManager().getLaunchIntentForPackage(ai.pname);
			if(LaunchApp!=null){
				startActivity( LaunchApp );
				closeSidebar();
			}
		}
	}
	private void removeAppFromList(int pos){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor edit = pref.edit();
		AppInfo ai = listAdapter.getItem(pos);
		edit.putBoolean(ai.appname, false);
		edit.commit();
		listAdapter.delete(ai);
		listAdapter.getFilter().filter(searchText.getText().toString());
	}
	class MyDragListener implements OnDragListener {
		@Override
	    public boolean onDrag(View v, DragEvent event) {
	    	int action = event.getAction();
	    	switch (event.getAction()) {
	    		case DragEvent.ACTION_DRAG_STARTED:
	    			gOV.setVisibility(View.GONE);
	    			break;
	    		case DragEvent.ACTION_DRAG_ENTERED:
	    			lp = v.getLayoutParams();
	    			lp.width = convertFromPxtoDP(100);
	    			lp.height = convertFromPxtoDP(100);
	    			v.requestLayout();
	    			break;
	    		case DragEvent.ACTION_DRAG_EXITED:
	    			//gOV.setVisibility(View.VISIBLE);
	    			lp = v.getLayoutParams();
	    			new Handler().post(new Runnable(){
	    				@Override
	    				public void run() {
	    					lp.width = convertFromPxtoDP(80);
	    					lp.height = convertFromPxtoDP(80);
	    				}
	    			});
	    			v.requestLayout();
	    			break;
	    		case DragEvent.ACTION_DROP:
	    			// Dropped, reassign View to ViewGroup
	    			View view = (View) event.getLocalState();
	    			ClipData cd = event.getClipData();
	    			int cnt = cd.getItemCount();
	    			if(v==deleteIm){
	    				ClipData.Item i = cd.getItemAt(3);
	    				removeAppFromList(Integer.parseInt(i.getText().toString()));
	    			}
	    			else if(v==uninstallIm){
	    				uninstallIntent(cd.getItemAt(4).getText().toString());
	    				ClipData.Item i = cd.getItemAt(3);
	    				removeAppFromList(Integer.parseInt(i.getText().toString()));	
	    			}		
	    			else if(v==shareIm){
	    				ClipData.Item i = cd.getItemAt(1);
	    				shareIntent(i.getText().toString());
	    			}
	    			break;
	    		case DragEvent.ACTION_DRAG_ENDED:
	    			lp = v.getLayoutParams();
	    			v.requestLayout();
	    			new Handler().post(new Runnable(){
	    				@Override
	    				public void run() {
	    					lp.width = convertFromPxtoDP(80);
	    					lp.height = convertFromPxtoDP(80);
	    					removeMenuImages();
	    					gOV.setVisibility(View.VISIBLE);
	    	    		}
	    			});
	    			break;
	    		default:
	    			break;
	    	}
	    	return true;
	    }
	}
	public int convertFromPxtoDP(int px){
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, dm);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int flags = Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP |
			    Intent.FLAG_ACTIVITY_CLEAR_TOP;
		removeLongClickMenu();
		if(v==deleteIm){
			removeAppFromList(pos);
		}
		else if(v==uninstallIm){
			uninstallIntent(longClickMenuData.pname);
			removeAppFromList(pos);	
		}		
		else if(v==shareIm){
			shareIntent(longClickMenuData.sourceDir);
		}
		else if(v==phone){
			Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
			phoneIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			phoneIntent.setFlags(flags);
			if(isCallable(phoneIntent)){
				startActivity( phoneIntent );
				closeSidebar();
			}
		}
		else if(v==sms){
			Intent smsIntent = new Intent(Intent.ACTION_VIEW);
			smsIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			smsIntent.setFlags(flags);
			smsIntent.setData(Uri.parse("sms:"));
			if(isCallable(smsIntent)){
				startActivity( smsIntent );
				closeSidebar();
			}
		}
		else if(v==camera){
			Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			cameraIntent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			cameraIntent.setFlags(flags);
			if(isCallable(cameraIntent)){
				startActivity( cameraIntent );
				closeSidebar();
			}
		}
		else if(v==flash){
			if(fh==null){
				fh = new FlashHandler(getApplicationContext(), flash);
			}
			if(fh.toggleFlash()){
				flash.setImageResource(R.drawable.tourch_on);
			}
			else{
				flash.setImageResource(R.drawable.tourch);
			}
		}
	}
	private boolean isCallable(Intent intent){
		List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent,PackageManager.MATCH_DEFAULT_ONLY);
		return list.size()>0; 
	}
	public void callSearch(String searchString) {
		searchText.setText(searchString);
	}
	public static int getContrastColor(int color,boolean alpha){
		int colorN = 0;
		if(alpha){
			colorN = Color.argb(140,255-Color.red(color),
		        255-Color.green(color),
                255-Color.blue(color));
		}
		else{
			colorN = Color.rgb(255-Color.red(color),
			        255-Color.green(color),
	                255-Color.blue(color));
		}
		return colorN; 
	}
}
class AppInfoComparator implements Comparator<AppInfo>{
	@Override
	public int compare(AppInfo lhs, AppInfo rhs) {
		return lhs.appname.compareTo(rhs.appname);
	}
}