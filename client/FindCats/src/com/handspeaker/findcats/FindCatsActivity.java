package com.handspeaker.findcats;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMyLocationClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.handspeaker.network.OnReceiveDataListener;
import com.handspeaker.network.PostRequest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 用于显示寻猫地图
 * 
 * @author SongQi
 * 
 */
public class FindCatsActivity extends ActionBarActivity implements
		SensorEventListener, OnGetGeoCoderResultListener,OnReceiveDataListener {
	public static final String FIND_CATS = "findcats";

	public static final int FIND_STATE = 0;
	public static final int ADD_STATE = 1;

	public static final int ADD_LOCATION = 0;
	// map related
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private GeoCoder mGeoCoder;
	// show markers on the map
	private List<Marker> mMakers;
	private InfoWindow mInfoWindow;
	private BitmapDescriptor markerImage;
	private MarkerOptions options;
	// location related
	private LocationClient mLocationClient;
	// public MyLocationListenner myListener;
	private boolean isFirstLoc;
	// orientation sensor
	private SensorManager mSensorManager;
	private Sensor mOrientation;
	private BDLocation lastLocation;
	private BDLocationListener mBDLocationListener;
	// popup related
	private TextView textViewHint;
	private int current_state;
	private boolean showInfoWindow;
	private boolean isLoading;
	private Marker lastMark;

	private ProgressDialog progressDialog;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.arg1) {
			case 0:
				progressDialog.dismiss();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_findcats);
		ActionBar actionBar = this.getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		initUserInterface();
		// loadData();
	}

	/**
	 * initialize user interface initialize all controllers and some variables
	 */
	public void initUserInterface() {
		// init variables
		isLoading = true;
		isFirstLoc = true;
		lastLocation = null;
		lastMark = null;
		mMakers = new ArrayList<Marker>();
		options = new MarkerOptions();
		current_state = FIND_STATE;
		showInfoWindow = false;
		textViewHint = (TextView) findViewById(R.id.textview_hint);
		// init map controllers
		// 初始化搜索模块，注册事件监听
		mGeoCoder = GeoCoder.newInstance();
		mGeoCoder.setOnGetGeoCodeResultListener(this);
		mMapView = (MapView) findViewById(R.id.bmapView);
		mMapView.setClickable(true);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public boolean onMapPoiClick(MapPoi arg0) {
				return false;
			}

			@Override
			public void onMapClick(LatLng arg0) {
				if (current_state == FIND_STATE) {
					showInfoWindow = false;
					lastMark = null;
					mBaiduMap.hideInfoWindow();
				} else if (current_state == ADD_STATE) {

				}

			}
		});
		// add response function for marker click
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			public boolean onMarkerClick(Marker marker) {
				if (current_state == FIND_STATE) {
					return displayData(marker);
				} else if (current_state == ADD_STATE) {
					mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption()
							.location(marker.getPosition()));
				}
				return true;
			}
		});
		mBaiduMap.setOnMyLocationClickListener(new OnMyLocationClickListener() {

			@Override
			public boolean onMyLocationClick() {
				if (current_state == ADD_STATE) {
					LatLng latlng = new LatLng(lastLocation.getLatitude(),
							lastLocation.getLongitude());
					mGeoCoder.reverseGeoCode(new ReverseGeoCodeOption()
							.location(latlng));
					// // add-location activity
					// Intent intent = new Intent();
					// Bundle bundle = new Bundle();
					// // put some data
					// intent.putExtras(bundle);
					// intent.setClass(FindCatsActivity.this,
					// AddLocationActivity.class);
					// startActivityForResult(intent, ADD_LOCATION);
					// return true;
				}
				return true;
			}
		});
		mLocationClient = new LocationClient(this);
		mBDLocationListener = new BDLocationListener() {

			@Override
			public void onReceivePoi(BDLocation poi) {

			}

			@Override
			public void onReceiveLocation(BDLocation location) {
				if (location == null || mMapView == null) {
					return;
				}
				lastLocation = location;
				if (isFirstLoc) {
					isFirstLoc = false;
					int locType = location.getLocType();
					Log.v(MainApplication.tag, "locType" + locType);
					// 如果定位成功，利用当前定位坐标载入周围的喂食点
					// 如果定位失败，则载入之前的保存的坐标，并利用其载入周围的喂食点
					if (locType != BDLocation.TypeGpsLocation
							|| locType != BDLocation.TypeNetWorkLocation
							|| locType != BDLocation.TypeOffLineLocation) {
						MapStatusUpdate u = MapStatusUpdateFactory
								.newLatLng(new LatLng(location.getLatitude(),
										location.getLongitude()));
						mBaiduMap.animateMapStatus(u);
					} else {
						SharedPreferences sharedPreferences = getPreferences(Activity.MODE_PRIVATE);
						double latitude = Double.parseDouble(sharedPreferences
								.getString("latitude",
										EntranceActivity.DEFALUT_LATITUDE));
						double longitude = Double.parseDouble(sharedPreferences
								.getString("longitude",
										EntranceActivity.DEFALUT_LONGITUDE));
						MapStatusUpdate u = MapStatusUpdateFactory
								.newLatLng(new LatLng(latitude, longitude));
						mBaiduMap.animateMapStatus(u);
						
					}
					loadData();
				}
			}
		};
		mLocationClient.registerLocationListener(mBDLocationListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);
		option.setCoorType("bd09ll");
		option.setScanSpan(1000);
		mLocationClient.setLocOption(option);
		// init orientation sensor
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		markerImage = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_gcoding);
	}

	private void showDialog() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setTitle("正在加载");
		progressDialog.setMessage("请稍等");
		progressDialog.show();
	}
	
	/**
	 * load data from server. currently the server has not been established, so
	 * this function will generate fake data
	 * @throws JSONException 
	 */
	public void loadData(){
		Log.v("FIND_CATS", "load data!");
		isLoading=true;
		showDialog();
		//发送当前地图的中心点给服务器，目前一次性返回所有结果，以后会根据中心点坐标返回一定范围内的结果
		JSONObject jsonObject = new JSONObject();
		MapStatus mapStatus=mBaiduMap.getMapStatus();
		try {
			jsonObject.put("latitude", mapStatus.target.latitude);
			jsonObject.put("longitude", mapStatus.target.longitude);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		PostRequest postRequest=PostRequest.getPostRequest();
		postRequest.iniRequest(PostRequest.QUERY,jsonObject);
		postRequest.setOnReceiveDataListener(this);
		postRequest.execute();
//		options.icon(markerImage);
//		options.zIndex(7);
//		options.draggable(false);
		// add marker overlay
//		LatLng[] locations = new LatLng[4];
//		locations[0] = new LatLng(39.963175, 116.400244);
//		locations[1] = new LatLng(39.942821, 116.369199);
//		locations[2] = new LatLng(39.939723, 116.425541);
//		locations[3] = new LatLng(39.906965, 116.401394);
//		for (int i = 0; i < 4; ++i) {
//			options.position(locations[i]);
//			Marker marker = (Marker) mBaiduMap.addOverlay(options);
//			mMakers.add(marker);
//		}
		isLoading = false;
//		Toast.makeText(this, "load data", Toast.LENGTH_SHORT).show();
	}

	/**
	 * show hint to tell the user how to add new location or update old location
	 */
	public void changeToAddState() {
		if (current_state != ADD_STATE) {
			current_state = ADD_STATE;
			textViewHint.setVisibility(TextView.VISIBLE);
			mBaiduMap.hideInfoWindow();
		}
	}

	public boolean displayData(Marker marker) {

		OnInfoWindowClickListener listener = new OnInfoWindowClickListener() {
			public void onInfoWindowClick() {
				// the app will jump to the activity which will show detail
				// information
				// LatLng ll = marker.getPosition();
				// LatLng llNew = new LatLng(ll.latitude + 0.005,
				// ll.longitude + 0.005);
				// marker.setPosition(llNew);
				Intent intent = new Intent();
				// put some data
				intent.setClass(FindCatsActivity.this,
						DetailLocationActivity.class);
				startActivity(intent);
			}
		};

		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.popup_dialog, null);
		TextView textAddr = (TextView) view.findViewById(R.id.textview_addr);
		textAddr.setText(textAddr.getText() + "小南庄十号楼1单元602");
		LatLng ll = marker.getPosition();
		mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(view),
				ll, -46, listener);
		if (lastMark == null || !lastMark.equals(marker)) {
			lastMark = marker;
			showInfoWindow = true;
			mBaiduMap.hideInfoWindow();
			mBaiduMap.showInfoWindow(mInfoWindow);
		} else {
			if (!showInfoWindow) {
				showInfoWindow = true;
				mBaiduMap.showInfoWindow(mInfoWindow);
			} else {
				showInfoWindow = false;
				mBaiduMap.hideInfoWindow();
			}
		}
		return true;
	}

	@Override
	protected void onPause() {
		mBaiduMap.setMyLocationEnabled(false);
		mLocationClient.stop();
		mSensorManager.unregisterListener(this);
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mBaiduMap.setMyLocationEnabled(true);
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				LocationMode.NORMAL, true, null));
		// MapStatusUpdate msu = MapStatusUpdateFactory.zoomTo(14.0f);
		// mBaiduMap.setMapStatus(msu);
		if (!mLocationClient.isStarted()) {
			mLocationClient.start();
		}
		mLocationClient.start();
		mSensorManager.registerListener(this, mOrientation,
				SensorManager.SENSOR_DELAY_NORMAL);
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		Log.v(MainApplication.tag, "findcats destroy");
		MyLocationData locationData=mBaiduMap.getLocationData();

		Intent intent=new Intent();
		intent.putExtra("latitude", Double.toString(locationData.latitude));
		intent.putExtra("longitude", Double.toString(locationData.longitude));
		setResult(EntranceActivity.RESULT_OK, intent);
		mLocationClient.stop();
		mLocationClient.unRegisterLocationListener(mBDLocationListener);
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.findcats_actionbar, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			this.finish();
			return true;
		case R.id.action_add:
			changeToAddState();
			return true;
		case R.id.action_refresh:
			// call loadData to get data from server
			if (!isLoading) {
				loadData();
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		float azimuth_angle = event.values[0];
		// float pitch_angle = event.values[1];
		// float roll_angle = event.values[2];
		// Log.v("tag", azimuth_angle+","+pitch_angle+","+roll_angle);
		// Math.abs(azimuth_angle - direction) > 1.0 &&
		if (lastLocation != null) {
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(lastLocation.getRadius())
					.latitude(lastLocation.getLatitude())
					.longitude(lastLocation.getLongitude())
					.direction(azimuth_angle).build();
			// Log.v("tag", "direction:" + direction);
			mBaiduMap.setMyLocationData(locData);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && current_state == ADD_STATE) {
			current_state = FIND_STATE;
			textViewHint.setVisibility(TextView.GONE);
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode == ADD_LOCATION) {
			current_state = FIND_STATE;
			textViewHint.setVisibility(TextView.GONE);
		}
		super.onActivityResult(requestCode, resultCode, intent);
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult result) {
	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(this, "抱歉，未能找到结果", Toast.LENGTH_SHORT).show();
		} else {
			// add-location activity
			Intent intent = new Intent();
			intent.putExtra("address", result.getAddress());
			intent.putExtra("latitude", result.getLocation().latitude);
			intent.putExtra("longitude", result.getLocation().longitude);
			intent.setClass(FindCatsActivity.this, NewLocationActivity.class);
			startActivityForResult(intent, ADD_LOCATION);
		}
	}

	@Override
	public void onReceiveData(String strResult, int StatusCode) {
		
	}
}