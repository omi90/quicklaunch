package com.newhere.quicklaunch;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

public class Preferences {
	public final static String fileName = "quicklaunch_settings";
	public final static String sidebarColor = "sidebar_color";
	public static int getSidebarColor(Context ctx){
		SharedPreferences pref = ctx.getSharedPreferences(Preferences.fileName, ctx.MODE_PRIVATE);
		return pref.getInt(Preferences.sidebarColor, Color.BLACK);
 	}
}
