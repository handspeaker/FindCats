package com.handspeaker.findcats;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

public class FindCatsApplication extends Application{
	
	@Override
	public void onCreate() {
		super.onCreate();
		SDKInitializer.initialize(this);
	}
}
