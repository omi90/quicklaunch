package com.newhere.quicklaunch;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.graphics.drawable.DrawableCompat;

public class AppInfo implements Parcelable{
    String appname = "";
    String pname = "";
    String versionName = "";
    int versionCode = 0;
    String iconLoc="";
    Drawable icon;
    String sourceDir = "";
    public AppInfo(){
    	icon = new BitmapDrawable();
    }
    public AppInfo(Parcel source){
    	appname = source.readString();
		pname = source.readString();
		versionName = source.readString();
		versionCode = source.readInt();
		iconLoc = source.readString();
		sourceDir = source.readString();
    }
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
	public void readFromParcel(Parcel in) {
		appname = in.readString();
		pname = in.readString();
		versionName = in.readString();
		versionCode = in.readInt();
		iconLoc = in.readString();
		sourceDir = in.readString();
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(appname);
		dest.writeString(pname);
		dest.writeString(versionName);
		dest.writeInt(versionCode);
		dest.writeString(iconLoc);
		dest.writeString(sourceDir);
	}
	public static final Parcelable.Creator<AppInfo> CREATOR = new Creator<AppInfo>() {
	    public AppInfo createFromParcel(Parcel source) {
	        return new AppInfo(source);
	    }
	    public AppInfo[] newArray(int size) {
	        return new AppInfo[size];
	    }
	};
}
