package com.ilifesmart.maplocation;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.location.BDLocation;
import com.baidu.location.BDNotifyListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapBaseIndoorMapInfo;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.ilifesmart.models.MyLocationListener;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class MapActivity extends AppCompatActivity {

	public static final String TAG = "MapActivity";

	@BindView(R.id.BaiduMapView)
	MapView mBaiduMapView;

	private BaiduMap mBaiduMap;
	private LocationClient mLocationClient;
	private MyLocationListener locationListener;
	private SuggestionSearch search;
	private RoutePlanSearch busSearch;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		ButterKnife.bind(this);

//		BaiduMapOptions options = new BaiduMapOptions();
//		options.mapType(BaiduMap.MAP_TYPE_SATELLITE);
//		options.compassEnabled(true);

		initialize();
	}

	@OnTextChanged(R.id.search)
	void onTextChanged(final CharSequence s, int a, int b , int c) {
		search.requestSuggestion(new SuggestionSearchOption()
		.city("杭州").keyword(s.toString()));

	}
	
	private void initialize() {

		// 路线搜索
		busSearch = RoutePlanSearch.newInstance();
		OnGetRoutePlanResultListener listener = new OnGetRoutePlanResultListener() {

			// 步行路线
			@Override
			public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

			}

			// 公交路线
			@Override
			public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
				Log.d(TAG, "onGetTransitRouteResult: describeContents " + transitRouteResult.describeContents());
				Log.d(TAG, "onGetTransitRouteResult: getSuggestAddrInfo " + transitRouteResult.getSuggestAddrInfo());
				Log.d(TAG, "onGetTransitRouteResult: getTaxiInfo " + transitRouteResult.getTaxiInfo());
			}

			// 地铁路线
			@Override
			public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

			}

			// 驾驶路线
			@Override
			public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

			}

			// 室内路线
			@Override
			public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

			}

			// 骑行路线
			@Override
			public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

			}
		};

		busSearch.setOnGetRoutePlanResultListener(listener);
		PlanNode stNode = PlanNode.withCityNameAndPlaceName("杭州", "锦绣江南");
		PlanNode enNode = PlanNode.withCityNameAndPlaceName("杭州", "江陵路");

		busSearch.transitSearch(new TransitRoutePlanOption()
		.from(stNode).to(enNode).city("杭州"));

		// 位置检索功能
		search = SuggestionSearch.newInstance();
		OnGetSuggestionResultListener searchListener = new OnGetSuggestionResultListener() {
			@Override
			public void onGetSuggestionResult(SuggestionResult suggestionResult) {
				List<SuggestionResult.SuggestionInfo> infos = suggestionResult.getAllSuggestions();
				Log.d(TAG, "onGetSuggestionResult: describeContents " + suggestionResult.describeContents());
				Log.d(TAG, "onGetSuggestionResult: XXXX " + infos.size() + " contents: " + Arrays.toString(infos.toArray()));
//				Log.d(TAG, "onGetSuggestionResult: SuggestionResult " + suggestionResult);
			}
		};

		search.setOnGetSuggestionResultListener(searchListener);

		mBaiduMap = mBaiduMapView.getMap();
		mBaiduMap.setTrafficEnabled(true); // 开启交通图
		mBaiduMap.setCustomTrafficColor("FFFF0000", "AADD0000", "AA00DD00", "AA00FF00"); // 自定义路况图颜色 严重拥堵，拥堵，缓行，畅通

		MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(13);
		mBaiduMap.animateMapStatus(u); // 地图状态刷新

		// 人群分布密度(热力图)
		mBaiduMap.setBaiduHeatMapEnabled(false);

		// 开启定位图层
		mBaiduMap.setMyLocationEnabled(true);

		// configuration
		// 精度圈大小不可人为设置。定位指针方向依据手机系统陀螺仪，开发者需自行设置.
		MyLocationConfiguration configuration = new MyLocationConfiguration(MyLocationConfiguration.LocationMode.NORMAL, true, null);
		mBaiduMap.setMyLocationConfiguration(configuration);

		mBaiduMap.setOnBaseIndoorMapListener(new BaiduMap.OnBaseIndoorMapListener() {
			@Override
			public void onBaseIndoorMapMode(boolean b, MapBaseIndoorMapInfo mapBaseIndoorMapInfo) {
				if (b) {
					// 室内图
					Log.d(TAG, "onBaseIndoorMapMode: mapBaseIndoorMapInfo " + mapBaseIndoorMapInfo);
					MapBaseIndoorMapInfo.SwitchFloorError err = mBaiduMap.switchBaseIndoorMapFloor(mapBaseIndoorMapInfo.getCurFloor(), mapBaseIndoorMapInfo.getID());
					Log.d(TAG, "onBaseIndoorMapMode: err " + err);
				} else {
					// 移除
					Log.d(TAG, "onBaseIndoorMapMode: 木有--");
				}
			}
		});

		// 开启室内定位.
		mBaiduMap.setIndoorEnable(true);

		mLocationClient = new LocationClient(this);

		LocationClientOption locationClientOption = new LocationClientOption();
		locationClientOption.setEnableSimulateGps(true);
		locationClientOption.setOpenGps(true);
		locationClientOption.setCoorType("bd09ll");
		locationClientOption.setScanSpan(1000);

		mLocationClient.setLocOption(locationClientOption);

		locationListener = new MyLocationListener(mBaiduMap);
		mLocationClient.registerLocationListener(locationListener);
		mLocationClient.start(); // 开始定位
	}

	@Override
	protected void onResume() {
		super.onResume();

		mBaiduMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();

		mBaiduMapView.onPause();
	}

	@Override
	protected void onDestroy() {
		mLocationClient.stop();
		mBaiduMap.setMyLocationEnabled(false);
		mBaiduMapView.onDestroy();
		mBaiduMap = null;
		search.destroy();
		busSearch.destroy();
		locationListener.setMap(mBaiduMap);
		super.onDestroy();
	}

	@OnClick(R.id.normal)
	public void onMapToNormal() {
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
	}

	@OnClick(R.id.satelite)
	public void onMapToSatelite() {
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
	}

	@OnClick(R.id.empty)
	public void onMapToEmpty() {
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NONE);
	}

	@OnClick(R.id.heat)
	public void onMapToHeat() {
		mBaiduMap.setBaiduHeatMapEnabled(!mBaiduMap.isBaiduHeatMapEnabled());
	}
}
