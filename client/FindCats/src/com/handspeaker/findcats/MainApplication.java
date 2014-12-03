package com.handspeaker.findcats;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

public class MainApplication extends Application{
	
	public static final String tag="findcats";
	
	@Override
	public void onCreate() {
		super.onCreate();
		SDKInitializer.initialize(this);
	}
}
