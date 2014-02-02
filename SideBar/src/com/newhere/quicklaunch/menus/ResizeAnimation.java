package com.newhere.quicklaunch.menus;

import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

class ResizeAnimation extends Animation {
	private float startHeight;
	final int targetHeight;
	View view;
 	public ResizeAnimation(View view, int targetHeight) {
		this.view = view;
		this.targetHeight = targetHeight;
	}
 	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		int newHeight = (int) (startHeight + (targetHeight - startHeight) * interpolatedTime);
		view.getLayoutParams().height = newHeight;
		view.requestLayout();
	}
 	@Override
	public void initialize(int width, int height, int parentWidth, int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		startHeight = view.getHeight();
	}
 	@Override
	public boolean willChangeBounds() {
		return true;
	}
}