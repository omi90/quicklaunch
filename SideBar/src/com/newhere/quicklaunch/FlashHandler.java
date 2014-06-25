package com.newhere.quicklaunch;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

public class FlashHandler {
	private Camera camera;
	private boolean isFlashOn = false;
	private boolean hasFlash = false;
	private Parameters params;
	private Context context;
	private ImageView flashIcon;
	public FlashHandler(Context ctx,ImageView im) {
		// TODO Auto-generated constructor stub
		this.context = ctx;
		this.flashIcon = im;
		hasFlash = this.context.getPackageManager()
				.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
		if(hasFlash){
			getCamera();
		}
	}
	private void getCamera() {
		if (camera == null) {
			try {
				camera = Camera.open();
				params = camera.getParameters();
			} catch (RuntimeException e) {
				Log.d("Error", e.getMessage());
				e.printStackTrace();
				Toast.makeText(this.context,"Can not open Flash!", Toast.LENGTH_SHORT).show();
			}
		}
	}
	// Turning On flash
	private void turnOnFlash() {
		if(!hasFlash)
			return;
		if (!isFlashOn) {
			if (camera == null || params == null) {
				return;
			}
			params = camera.getParameters();
			params.setFlashMode(Parameters.FLASH_MODE_TORCH);
			camera.setParameters(params);
			camera.startPreview();
			isFlashOn = true;
			// changing button/switch image
			//toggleButtonImage();
		}
	}
	// Turning Off flash
	public void turnOffFlash() {
		if(!hasFlash)
			return;
		if (isFlashOn) {
			if (camera == null || params == null) {
				return;
			}
			params = camera.getParameters();
			params.setFlashMode(Parameters.FLASH_MODE_OFF);
			camera.setParameters(params);
			camera.stopPreview();
			isFlashOn = false;
			// changing button/switch image
			//toggleButtonImage();
		}
	}
	public boolean toggleFlash(){
		if (!hasFlash) {
			// device doesn't support flash
			// Show alert message and close the application
			Toast.makeText(this.context, "Device does not support flash!", Toast.LENGTH_SHORT).show();
			return false;
		}
		if(isFlashOn){
			turnOffFlash();
			return false;
		}
		else{
			turnOnFlash();
			return true;
		}
	}
	public void releaseCamera(){
		if(camera!=null){
			camera.release();
			camera = null;
		}
	}
}
