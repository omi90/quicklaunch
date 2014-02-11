package com.newhere.quicklaunch;

import java.util.ArrayList;
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
import android.widget.Filter;
import android.widget.ImageView;

public class AppAdapter extends ArrayAdapter<AppInfo> {
	private final Context context;
	private List<AppInfo> values;
	private List<AppInfo> originalValues; 
	private AppInfoFilter aiFilter;
	private AppInfo ri;
	public AppAdapter(Context context, int textViewResourceId, List<AppInfo> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		values = new ArrayList<AppInfo>();
		values.addAll(objects);
		originalValues = new ArrayList<AppInfo>();
		originalValues.addAll(objects);
	}
	public void delete(AppInfo ai){
		originalValues.remove(ai);
	}
	@Override
	public Filter getFilter() {
	   if (aiFilter == null){
		   aiFilter  = new AppInfoFilter();
	   }
	   return aiFilter;
	}
	private class AppInfoFilter extends Filter{
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			// TODO Auto-generated method stub
			constraint = constraint.toString().toLowerCase();
		    FilterResults result = new FilterResults();
		    if(constraint != null && constraint.toString().length() > 0)
		    {
		    	ArrayList<AppInfo> filteredItems = new ArrayList<AppInfo>();
		    	for(int i = 0, l = originalValues.size(); i < l; i++){
		    		AppInfo ai = originalValues.get(i);
		    		if(ai.toString().toLowerCase().startsWith(constraint.toString()))
		    			filteredItems.add(ai);
		    	}
		    	result.count = filteredItems.size();
		    	result.values = filteredItems;
		    }
		    else{
		    	synchronized(this){
		    		result.values = originalValues;
		    		result.count = originalValues.size();
		    	}
		    }
		    return result;
		}
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			values = (ArrayList<AppInfo>)results.values;
		    notifyDataSetChanged();
		    clear();
		    for(int i = 0, l = values.size(); i < l; i++)
		     add(values.get(i));
		    notifyDataSetInvalidated();
		}
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