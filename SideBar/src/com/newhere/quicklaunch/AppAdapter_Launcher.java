package com.newhere.quicklaunch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.newhere.sidebar.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AppAdapter_Launcher extends ArrayAdapter<AppInfo> {
	private final Context context;
	private final List<AppInfo> values;
	private AppInfo ri;
	private Map<String,?> appsPref;
	public AppAdapter_Launcher(Context context, int textViewResourceId, List<AppInfo> objects,Map<String,?> appsPref) {
		super(context, textViewResourceId, objects);
		this.context = context;
		values = objects;
		this.appsPref = appsPref;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		int list_layout = R.layout.listview_launch;
		View rowView = inflater.inflate(list_layout, parent, false);
		CheckBox cb = (CheckBox)rowView.findViewById(R.id.checkBox1);
		ImageView im = (ImageView)rowView.findViewById(R.id.imageView1);
		TextView tv1 = (TextView)rowView.findViewById(R.id.textView1);
		//TextView tv2 = (TextView)rowView.findViewById(R.id.textView2);
		ri = values.get(position);
		if(appsPref.containsKey(ri.appname)){
			cb.setChecked((Boolean)appsPref.get(ri.appname));
		}
		else{
			cb.setChecked(false);
		}
		im.setImageDrawable(ri.icon);
		tv1.setText(ri.appname);
		//tv2.setText(ri.pname);
		return rowView;
	}
}