package com.newhere.quicklaunch;

import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

class ResizeAnimation extends Animation {
	private float startWidth;
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
	}
 	@Override
	public boolean willChangeBounds() {
		return true;
	}
}