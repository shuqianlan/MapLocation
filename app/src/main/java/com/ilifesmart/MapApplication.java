package com.ilifesmart;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.SDKInitializer;

public class MapApplication extends Application {

	private static Context sContext;
	@Override
	public void onCreate() {
		super.onCreate();

		sContext = getApplicationContext();

		SDKInitializer.initialize(this);
		SDKInitializer.setHttpsEnable(true);
	}

	public static Context getContext() {
		return sContext;
	}

}
