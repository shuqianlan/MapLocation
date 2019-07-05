package com.ilifesmart.models;

import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class MyLocationListener extends BDAbstractLocationListener {
	public static final String TAG = "MyLocationListener";

	private BaiduMap mMap;
	private boolean isFirstLoc = true;
	public MyLocationListener(BaiduMap map) {
		mMap = map;
	}

	public void setMap(BaiduMap map) {
		mMap = map;
	}

	@Override
	public void onReceiveLocation(BDLocation bdLocation) {
		Log.d(TAG, "onReceiveLocation: bdLocation " + bdLocation);
		if (bdLocation == null || mMap == null) {
			return;
		}

		Log.d(TAG, "onReceiveLocation: direction " + bdLocation.getCity() + bdLocation.getRoadLocString() + bdLocation.getLatitude() + bdLocation.getLongitude());

		MyLocationData locData = new MyLocationData.Builder()
						.accuracy(bdLocation.getRadius())
						.direction(bdLocation.getDirection())
						.latitude(bdLocation.getLatitude())
						.longitude(bdLocation.getLongitude()).build();
		Log.d(TAG, "onReceiveLocation: locData " + locData);
		mMap.setMyLocationData(locData);

		if (isFirstLoc) {
			isFirstLoc = false;
			LatLng ll = new LatLng(bdLocation.getLatitude(), bdLocation.getLongitude());
			MapStatus.Builder builder = new MapStatus.Builder();
			//设置缩放中心点；缩放比例；
			builder.target(ll).zoom(18.0f);
			//给地图设置状态
			mMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
		}
	}
}
