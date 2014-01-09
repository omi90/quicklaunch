package com.newhere.sidebar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;

public class LauncherActivity extends Activity {
	//private static final String fileName = "sidebar_preferences";
	private ListView lv;
	private int listSize=0;
	private ArrayList<AppInfo> apps=null;
	private Button save;
	private Map<String,Boolean> data;
	private Handler h;
	private ProgressBar pb;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launcher);
		lv = (ListView)findViewById(R.id.listView1);
		save = (Button)findViewById(R.id.save);
		save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getBaseContext(), SideBarService.class);
				stopService(intent);
				intent = new Intent(getBaseContext(), SideBarService.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startService(intent);
			}
		});
		pb = (ProgressBar)findViewById(R.id.progressbar_loading);
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		h = new Handler();
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				apps = getInstalledApps(false, getPackageManager());
				listSize = apps.size();
				data = (Map<String, Boolean>) getData(getApplicationContext());
				saveData(apps,getApplicationContext());
				if(data.size()<1)
					data = (Map<String, Boolean>) getData(getApplicationContext());
				h.post(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						pb.setVisibility(View.GONE);
						lv.setAdapter(new AppAdapter_Launcher(getApplicationContext(), R.layout.listview_launcher, apps, data));
						lv.setOnItemClickListener(new OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> arg0, View view, int pos, long id) {
								// TODO Auto-generated method stub
								CheckBox cb = (CheckBox)view.findViewById(R.id.checkBox1);
								boolean value = cb.isChecked();
								cb.setChecked(!value);
								SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
								SharedPreferences.Editor edit = pref.edit();
								edit.putBoolean(apps.get(pos).appname, !value);
								edit.commit();
								data.put(apps.get(pos).appname, !value);
								System.out.println(apps.get(pos).appname+" <<"+pref.getBoolean(apps.get(pos).appname, false));
							}
						});
						final Intent intent = new Intent(getBaseContext(), SideBarService.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startService(intent);
					}
				});
			}
		});
		t.start();
	}

	public static Map<String,?> getData(Context context){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		return pref.getAll();
	}
	public void saveData(ArrayList<AppInfo> appsPref,Context context){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor edit = pref.edit();
		for(AppInfo ai:appsPref){
			if(!pref.contains(ai.appname)){
				edit.putBoolean(ai.appname, true);
				System.out.println(ai.appname+" >>"+true);				
			}
			else
				System.out.println(ai.appname+" >>"+false);
		}
		edit.commit();
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.launcher, menu);
        return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId()==R.id.customize_menu){
			final Intent i = new Intent(getApplicationContext(),CustomizeActivity.class);
			startActivity(i);
		}
		return super.onOptionsItemSelected(item);
	}
	public static ArrayList<AppInfo> getInstalledApps(boolean systemApps,PackageManager pm){
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<PackageInfo> apps = pm.getInstalledPackages(PackageManager.GET_META_DATA);
		ArrayList<AppInfo> res = new ArrayList<AppInfo>();
		for(int i=0;i<apps.size();i++) {
			PackageInfo p = apps.get(i);
			if(!systemApps){
				if ((p.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
					continue ;
				}
			}
			AppInfo newInfo = new AppInfo();
		    newInfo.appname = p.applicationInfo.loadLabel(pm).toString();
		    newInfo.pname = p.packageName;
		    newInfo.versionName = p.versionName;
		    newInfo.versionCode = p.versionCode;
		    newInfo.icon = p.applicationInfo.loadIcon(pm);
		    newInfo.sourceDir = p.applicationInfo.sourceDir;
		    //System.out.println(newInfo.sourceDir);
		    res.add(newInfo);
		}
		return res;
	}
	public static ArrayList<AppInfo> getPrefInstalledApps(boolean systemApps,PackageManager pm,Context context){
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		Map<String,?> data = pref.getAll();
		Intent intent = new Intent(Intent.ACTION_MAIN, null);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		List<PackageInfo> apps = pm.getInstalledPackages(PackageManager.GET_META_DATA);
		ArrayList<AppInfo> res = new ArrayList<AppInfo>();
		for(int i=0;i<apps.size();i++) {
			PackageInfo p = apps.get(i);
			if(!systemApps){
				if ((p.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
					continue ;
				}
			}
			if(data.containsKey(p.applicationInfo.loadLabel(pm).toString()) && (Boolean)data.get(p.applicationInfo.loadLabel(pm).toString())){
				AppInfo newInfo = new AppInfo();
				newInfo.appname = p.applicationInfo.loadLabel(pm).toString();
				newInfo.pname = p.packageName;
				newInfo.versionName = p.versionName;
				newInfo.versionCode = p.versionCode;
				newInfo.icon = p.applicationInfo.loadIcon(pm);
				newInfo.sourceDir = p.applicationInfo.sourceDir;
				//System.out.println(newInfo.sourceDir);
				res.add(newInfo);
			}
		}
		return res;
	}
}
