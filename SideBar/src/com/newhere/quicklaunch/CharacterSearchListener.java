package com.newhere.quicklaunch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.newhere.sidebar.R;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.os.Environment;
import android.widget.Toast;

public class CharacterSearchListener implements OnGesturePerformedListener {
	private GestureLibrary gestureLib;
	private SideBarService obj;
	private String searchString = "";
	public String getSearchString() {
		return searchString;
	}
	public void setSearchString(String searchString) {
		this.searchString = searchString;
	}
	public CharacterSearchListener(SideBarService obj) {
		//gestureLib = GestureLibraries.fromFile(new File(Environment.getExternalStorageDirectory()
        //        .getAbsolutePath(), "gestures"));
		gestureLib = GestureLibraries.fromRawResource(obj.getApplicationContext(),R.raw.gestures);
		if (!gestureLib.load()) {
			//finish();
		}
		this.obj = obj;
	}
	@Override
	public void onGesturePerformed(GestureOverlayView gov, Gesture gesture) {
		// TODO Auto-generated method stub
		ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
		double maxScore = 0.0;
		String maxScoreName = "";
		for (Prediction prediction : predictions) {
			if (prediction.score > 1.0) {
				if(maxScore < prediction.score){
					maxScore = prediction.score;
					maxScoreName = prediction.name;
				}
			}
		}
		if(maxScoreName.contains("close")){
			obj.closeSidebar();
			return;
		}
		searchString += maxScoreName;
		obj.callSearch(searchString);
	}
	public void clear(){
		searchString = "";
	}
	public List<AppInfo> getAppsContaining(List<AppInfo> list,String searchStr){
		List<AppInfo> listCopy = new ArrayList<AppInfo>(list);
		List<AppInfo> ailist = new ArrayList<AppInfo>();
		for(AppInfo ai:listCopy){
			if(ai.appname.equalsIgnoreCase(searchStr)){
				ailist.add(ai);
			}
		}
		return ailist;
	}
}