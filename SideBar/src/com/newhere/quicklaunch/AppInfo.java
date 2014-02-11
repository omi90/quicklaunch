package com.newhere.quicklaunch;

import android.graphics.drawable.Drawable;

public class AppInfo {
    String appname = "";
    String pname = "";
    String versionName = "";
    int versionCode = 0;
    String iconLoc;
    Drawable icon;
    String sourceDir = "";
    
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((appname == null) ? 0 : appname.hashCode());
		result = prime * result + ((pname == null) ? 0 : pname.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AppInfo other = (AppInfo) obj;
		if (appname == null) {
			if (other.appname != null)
				return false;
		} else if (!appname.equals(other.appname))
			return false;
		if (pname == null) {
			if (other.pname != null)
				return false;
		} else if (!pname.equals(other.pname))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return appname.toLowerCase().trim();
	}
}
