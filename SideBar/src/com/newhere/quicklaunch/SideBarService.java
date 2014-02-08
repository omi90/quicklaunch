package com.newhere.quicklaunch;

import java.io.File;
import android.view.View;
import java.util.Comparator;
import java.util.List;
import com.newhere.sidebar.R;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.DragShadowBuilder;
import android.view.View.OnClickListener;
import android.view.View.OnDragListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class SideBarService extends Service implements OnTouchListener,OnKeyListener,AnimationListener,OnClickListener{
	private WindowManager wm;
	private WindowManager.LayoutParams paramSidebarVisible,paramSidebarHidden;
	private ImageView prev_image,setting_image,draggableView;
	private FrameLayout cont;
	private ListView lv;
	private LinearLayout parent;
	private View myview,sidebarMenu;
	private List<AppInfo> res;
	private List<AppInfo> resSearch;
	private Animation openAnim,closeAnim;
	private LinearLayout ll;
	private boolean isSidebarVisible = false;
	private DisplayMetrics dm;
	private ImageView deleteIm,shareIm,uninstallIm;
	private LayoutInflater li;
	private AppAdapter listAdapter;
	private FrameLayout longClickMenu;
	private AppInfo longClickMenuData;
	private int pos = 0;
	private boolean longClickMenuVisible=false;
	private int currentapiVersion = android.os.Build.VERSION.SDK_INT;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	public void onCreate(){
		super.onCreate();
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		int minHeight = Math.max(dm.heightPixels, dm.widthPixels);
		int minWidth = minHeight==dm.heightPixels?dm.heightPixels:dm.widthPixels;
		paramSidebarVisible = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_DIM_BEHIND,
				PixelFormat.TRANSLUCENT);
		paramSidebarVisible.gravity = Gravity.TOP|Gravity.LEFT;
		paramSidebarVisible.x = 0;
		paramSidebarVisible.y = 0;
		paramSidebarVisible.dimAmount = (float) 0.08;
		paramSidebarHidden = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		paramSidebarHidden.gravity = Gravity.CENTER|Gravity.LEFT;
		li = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		cont = (FrameLayout)li.inflate(R.layout.layout_list_sidebar, null);
		parent = (LinearLayout) cont.findViewById(R.id.layout);
		sidebarMenu = li.inflate(R.layout.listview_menu_layout, null);
		sidebarMenu.setMinimumHeight(minHeight);
		sidebarMenu.setMinimumWidth(minWidth);
		setting_image = (ImageView)parent.findViewById(R.id.imageView1);
		ll = (LinearLayout)parent.findViewById(R.id.imagelayout);
		ll.getLayoutParams().width = 0;
		//ll.requestLayout();
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
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		res = LauncherActivity.getPrefInstalledApps(false, getPackageManager(), getApplicationContext());
		listAdapter = new AppAdapter(this, R.layout.listview, res);
		lv.setAdapter(listAdapter);
		lv.setClickable(true);
		lv.setOnItemClickListener(new SidebarListItemClick());
		lv.setOnItemLongClickListener(new SidebarListItemLongClick());
		prev_image = (ImageView)myview.findViewById(R.id.prev_item);
		prev_image.setOnTouchListener(this);
		myview.setOnTouchListener(this);
		wm.addView(myview, paramSidebarHidden);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setContentTitle("Message").setContentText("Click to close");
		startForeground(1337, mBuilder.build());
		NotificationHandler.setHidden(false);
		NotificationHandler.createNotification(getApplicationContext());
		setUpMenuImages();
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
		parent.removeView(sidebarMenu);
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
		System.out.println("inside on touch event");
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
			SideBarService.this.onTouch(prev_image,null);
	    }
	    return false;
	}
	@Override
	public void onAnimationEnd(Animation animation) {
		System.out.println("Animation ended");
		if(animation.equals(openAnim)){
			wm.updateViewLayout(myview, paramSidebarVisible);
			ll.setVisibility(View.VISIBLE);
			myview.getLayoutParams().width =LayoutParams.MATCH_PARENT;
			myview.setOnTouchListener(this);
			myview.requestLayout();
		}
		else{
			wm.updateViewLayout(myview, paramSidebarHidden);
			ll.setVisibility(View.INVISIBLE);
			myview.getLayoutParams().width = LayoutParams.WRAP_CONTENT;
			myview.setOnTouchListener(null);
			myview.requestLayout();
			parent.invalidate();
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
	@SuppressLint("NewApi")
	class SidebarListItemLongClick implements OnItemLongClickListener{
		@SuppressLint("NewApi")
		@Override
		public boolean onItemLongClick(AdapterView<?> av, View v,
				int pos, long id) {
			Vibrator vibr = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			vibr.vibrate(50);
			//int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			AppInfo ai = (AppInfo)lv.getAdapter().getItem(pos);
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
	      		parent.addView(sidebarMenu);
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
				cont.addView(longClickMenu, flp);
				longClickMenu.setLayoutParams(flp);
				longClickMenu.requestLayout();
				System.out.println(longClickMenu.getLayoutParams());
				longClickMenuVisible = true;
				return true;
			}
		}
	}
	private void shareIntent(String sourceDir){
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		sharingIntent.setType("application/vnd.android.package-archive");
		sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(sourceDir)));
		Intent i = Intent.createChooser(sharingIntent, "Share via");
		i.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);                     
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getApplication().startActivity(i);
		SideBarService.this.onTouch(prev_image,null);
	}
	private void uninstallIntent(String packageName){
		Uri packageUri =Uri.parse("package:"+packageName);
		Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
		uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);                     
		uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(uninstallIntent);
		SideBarService.this.onTouch(prev_image,null);
	}
	class SidebarListItemClick implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int pos,
				long id) {
			// TODO Auto-generated method stub
			AppInfo ai = res.get(pos);
			Intent LaunchApp = getPackageManager().getLaunchIntentForPackage(ai.pname);
			if(LaunchApp!=null){
				startActivity( LaunchApp );
				SideBarService.this.onTouch(prev_image,null);
			}
		}
	}
	private void removeAppFromList(int pos){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		SharedPreferences.Editor edit = pref.edit();
		edit.putBoolean(res.get(pos).appname, false);
		edit.commit();
		res.remove(pos);
		listAdapter.notifyDataSetChanged();
	}
	class MyDragListener implements OnDragListener {
		@Override
	    public boolean onDrag(View v, DragEvent event) {
	    	int action = event.getAction();
	    	switch (event.getAction()) {
	    		case DragEvent.ACTION_DRAG_STARTED:
	    			break;
	    		case DragEvent.ACTION_DRAG_ENTERED:
	    			if(v==uninstallIm){
	    				ImageView im = (ImageView)v;
	    				im.setImageResource(R.drawable.trash_full);
	    			}
	    			v.setAlpha((float) 0.8);
	    			v.invalidate();
	    			//v.setBackgroundDrawable(enterShape);
	    			break;
	    		case DragEvent.ACTION_DRAG_EXITED:
	    			if(v==uninstallIm){
	    				ImageView im = (ImageView)v;
	    				im.setImageResource(R.drawable.trash_empty);
	    			}
	    			v.setAlpha((float) 1.0);
	    			v.invalidate();
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
	    				ClipData.Item i = cd.getItemAt(0);
	    				shareIntent(i.getText().toString());
	    			}
	    			break;
	    		case DragEvent.ACTION_DRAG_ENDED:
	    			if(v==uninstallIm){
	    				ImageView im = (ImageView)v;
	    				im.setImageResource(R.drawable.trash_empty);
	    			}
	    			v.setAlpha((float) 1.0);
	    			v.invalidate();
	    			new Handler().post(new Runnable(){
	    				@Override
	    				public void run() {
	    					removeMenuImages();
	    				}
	    			});
	    			break;
	    		default:
	    			break;
	    	}
	    	return true;
	    }
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
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
	}
}
class AppInfoComparator implements Comparator<AppInfo>{
	@Override
	public int compare(AppInfo lhs, AppInfo rhs) {
		return lhs.appname.compareTo(rhs.appname);
	}
}