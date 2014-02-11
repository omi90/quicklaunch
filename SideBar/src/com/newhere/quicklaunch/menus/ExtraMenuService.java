package com.newhere.quicklaunch.menus;

import com.newhere.sidebar.R;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;

public class ExtraMenuService extends Service implements OnTouchListener{
	private LinearLayout menuContainer;
	private FrameLayout parent;
	private ImageView phone,messaging,notes,drawNotes,viewOpener;
	private WindowManager.LayoutParams paramHidden,paramShown;
	private Animation openAnim,closeAnim;
	private WindowManager wm;
	private boolean isMenuVisible = false;
	private boolean isWindowVisible = false;
	private LayoutParams param;
	private DisplayMetrics dm;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		dm = new DisplayMetrics();
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		LayoutInflater li = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
		paramHidden = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		paramHidden.gravity = Gravity.BOTTOM;
		paramShown = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_DIM_BEHIND,
				PixelFormat.TRANSLUCENT);
		paramShown.gravity = Gravity.BOTTOM;
		paramShown.dimAmount = (float)0.3;
		parent = (FrameLayout)li.inflate(R.layout.extra_menu, null);
		viewOpener = (ImageView)parent.findViewById(R.id.viewOpener);
		menuContainer = (LinearLayout)parent.findViewById(R.id.menuContainer);
		phone = (ImageView)parent.findViewById(R.id.phone);
		messaging = (ImageView)parent.findViewById(R.id.messaging);
		notes = (ImageView)parent.findViewById(R.id.notes);
		drawNotes = (ImageView)parent.findViewById(R.id.drawNotes);
		viewOpener.setOnTouchListener(this);
		phone.setOnTouchListener(this);
		messaging.setOnTouchListener(this);
		notes.setOnTouchListener(this);
		drawNotes.setOnTouchListener(this);
		parent.setOnTouchListener(this);
		wm.addView(parent, paramHidden);
		MyAnimationListener al = new MyAnimationListener();
		openAnim = new ResizeAnimation(menuContainer, 
				(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics()));
		openAnim.setDuration(300);
		openAnim.setAnimationListener(al);
		openAnim.setFillAfter(true);
		closeAnim = new ResizeAnimation(menuContainer, 1);
		closeAnim.setDuration(300);
		closeAnim.setAnimationListener(al);
		closeAnim.setFillAfter(true);
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public boolean onTouch(View view, MotionEvent me) {
		// TODO Auto-generated method stub
		if(view == phone && me.getAction() == MotionEvent.ACTION_DOWN){
			
		}
		else if(view == messaging && me.getAction() == MotionEvent.ACTION_DOWN){
			
		}
		else if(view == notes && me.getAction() == MotionEvent.ACTION_DOWN){
	
		}
		else if(view == drawNotes && me.getAction() == MotionEvent.ACTION_DOWN){
			/*if(isWindowVisible){
				parent.removeViewAt(0);
			}
			wm.getDefaultDisplay().getMetrics(dm);
			BrushView bv = new BrushView(getApplicationContext());
			bv.setBackgroundColor(Color.CYAN);
			param = new LayoutParams(dm.widthPixels, 
									dm.heightPixels-menuContainer.getHeight(), 
									Gravity.TOP|Gravity.LEFT);
			parent.addView(bv, 0, param);
			parent.invalidate();
			isWindowVisible = true;*/
		}
		else if((view == parent || view == viewOpener) && me.getAction() == MotionEvent.ACTION_DOWN){
			if(!isMenuVisible) {
				menuContainer.requestLayout();
				menuContainer.startAnimation(openAnim);
				isMenuVisible = true;
		        return true;
			} 
			else{
				menuContainer.requestLayout();
				menuContainer.startAnimation(closeAnim);
				isMenuVisible = false;
		        return true;
			}
	    }
		return false;
	}
	class MyAnimationListener implements AnimationListener{
		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			if(animation == openAnim){
				wm.updateViewLayout(parent, paramShown);
			}
			else if(animation == closeAnim){
				wm.updateViewLayout(parent, paramHidden);
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
}