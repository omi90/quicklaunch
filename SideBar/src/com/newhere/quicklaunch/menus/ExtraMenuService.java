package com.newhere.quicklaunch.menus;

import com.newhere.sidebar.R;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ExtraMenuService extends Service implements OnTouchListener{
	private LinearLayout parent,menuContainer;
	private ImageView phone,messaging,notes,drawNotes;
	private WindowManager.LayoutParams paramHidden,paramShown;
	private Animation openAnim,closeAnim;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
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
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_DIM_BEHIND,
				PixelFormat.TRANSLUCENT);
		paramShown.gravity = Gravity.BOTTOM;
		paramShown.dimAmount = (float)0.1;
		parent = (LinearLayout)li.inflate(R.layout.extra_menu, null);
		menuContainer = (LinearLayout)parent.findViewById(R.id.menuContainer);
		phone = (ImageView)parent.findViewById(R.id.phone);
		messaging = (ImageView)parent.findViewById(R.id.messaging);
		notes = (ImageView)parent.findViewById(R.id.notes);
		drawNotes = (ImageView)parent.findViewById(R.id.drawNotes);
		parent.setOnTouchListener(this);
		wm.addView(parent, paramHidden);
		openAnim = new ResizeAnimation(menuContainer, 
				(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics()));
		openAnim.setDuration(300);
		//openAnim.setAnimationListener(this);
		openAnim.setFillAfter(true);
		closeAnim = new ResizeAnimation(menuContainer, 0);
		closeAnim.setDuration(300);
		//closeAnim.setAnimationListener(this);
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
		if(view==parent){
			
		}
		return true;
	}

}
