package com.newhere.quicklaunch;

import java.util.List;

import com.newhere.sidebar.R;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class AppAdapter extends ArrayAdapter<AppInfo> {
	private final Context context;
	private final List<AppInfo> values;
	private AppInfo ri;
	public AppAdapter(Context context, int textViewResourceId, List<AppInfo> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		values = objects;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		int list_layout = R.layout.listview;
		View rowView = inflater.inflate(list_layout, parent, false);
		ImageView im = (ImageView)rowView.findViewById(R.id.app_image);
		ri = values.get(position);
		im.setImageDrawable(ri.icon);
		ActivityManager manager = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> processes = manager.getRunningAppProcesses();
		if(checkRunningStatus(ri.pname, processes)){
			rowView.setBackgroundColor(Color.argb(220, 13, 97, 84));
		}
		return rowView;
	}
	private boolean checkRunningStatus(String packageName, List<RunningAppProcessInfo> processes) {
		for(RunningAppProcessInfo proInfo:processes){
			for(String pkgName:proInfo.pkgList){
				if(pkgName.equalsIgnoreCase(packageName))
					return true;
			}
		}
		return false;
	}
}