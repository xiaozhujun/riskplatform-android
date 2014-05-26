package org.whut.platform;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.whut.application.MyApplication;
import org.whut.client.CasClient;
import org.whut.strings.ToastStrings;
import org.whut.strings.UrlStrings;
import org.whut.utils.BMapUtils;
import org.whut.utils.JsonUtils;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapStatus;
import com.baidu.mapapi.map.MKMapStatusChangeListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.MyLocationOverlay.LocationMode;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class MapActivity extends Activity {
	private BMapManager mapManager;
	private MapView mMapView;
	private MapController mapController;
	private TextView et;
	private LinearLayout sp_layout;
	private Spinner sp_province;
	private Spinner sp_city;
	private Spinner sp_area;
	private String responseMsg;
	private static String[] provinces;
	private static String[] cities;
	private static String[] areas;
	private static String[] CompanyPositions;
	private static String[] unitAddresses;
	private MyHandler handler;
	private Intent it;

	private ProgressDialog dialog;


	private PopupOverlay pop = null;

	private MyOverlay mOverlay = null;
	private TextView popupText = null;
	private View popupInfo = null;
	private View viewCache = null;

	// private static final float ZOOM_LEVEL_ALL = (float) 4.6;
	private static final int ZOOM_LEVEL_PROVINCE = 8;
	private static final int ZOOM_LEVEL_CITY = 10;
	private static final int ZOOM_LEVEL_AREA = 13;

	boolean isRequest = false;
	boolean isFirstLoc = true;
	private LocationClient myLocationClient = null;
	private LocationData locationData = null;
	public MyLocationListenner myListener = new MyLocationListenner();
	MyLocationOverlay myLocationOverlay = null;
	private MKSearch mkSearch;
	private GeoPoint point;
	private static String provinceName;
	private static String cityName;
	private static String areaName;

	private boolean flag = true;//
	private ImageButton zoom_in;
	private ImageButton zoom_out;
	private static final int maxZoom = 18;
	private static final int minZoom = 3;
	private static int currentZoom = 0;
	private static final float ZOOM_LOCATION =  (float) 15;
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			locationData.latitude = location.getLatitude();
			locationData.longitude = location.getLongitude();
			locationData.accuracy = location.getRadius();
			locationData.direction = location.getDerect();
			myLocationOverlay.setData(locationData);
			mMapView.refresh();
			if (isRequest || isFirstLoc) {
				// 移动地图到定位点
				Log.d("LocationOverlay", "receive location, animate to it");
				point = new GeoPoint((int) (locationData.latitude * 1e6),
						(int) (locationData.longitude * 1e6));
				mapController.animateTo(point);
				mapController.setCenter(point);
				isRequest = false;
				myLocationOverlay.setLocationMode(LocationMode.FOLLOWING);
				mkSearch.reverseGeocode(point);
				Log.i("location", Double.toString(locationData.longitude));
				Log.i("location", Double.toString(locationData.latitude));
			}

			isFirstLoc = false;

		}

		@Override
		public void onReceivePoi(BDLocation poiLocation) {
			if (poiLocation == null) {
				return;

			}

		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mapManager = new BMapManager(getApplication());
		mapManager.init(new MKGeneralListener() {

			@Override
			public void onGetPermissionState(int arg0) {

				Toast.makeText(getApplicationContext(),
						ToastStrings.KEY_FAILED, Toast.LENGTH_SHORT).show

				();
			}

			@Override
			public void onGetNetworkState(int arg0) {

				Toast.makeText(getApplicationContext(),

				ToastStrings.NETWORK_FAILED, Toast.LENGTH_SHORT).show();
			}
		});

		setContentView(R.layout.activity_map);
		//线程处理初始化
		handler = new MyHandler(this);
		//地图参数设置
		mMapView = (MapView) findViewById(R.id.mMapView1);
		mapController = mMapView.getController();
		mapController.setZoom(ZOOM_LOCATION);
		currentZoom = (int) ZOOM_LOCATION ;
		mMapView.regMapStatusChangeListener(new MKMapStatusChangeListener() {

			@Override
			public void onMapStatusChange(MKMapStatus arg0) {
				currentZoom = (int) mMapView.getZoomLevel();
				if(currentZoom<maxZoom&&currentZoom>minZoom){
					zoom_in.setEnabled(true);
					zoom_out.setEnabled(true);
				}
			}
		});

		// 定位初始化
		myLocationClient = new LocationClient(getApplicationContext());
		locationData = new LocationData();
		myLocationClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(5000);
		myLocationClient.setLocOption(option);
		myLocationClient.start();

		// 定位图层初始化
		myLocationOverlay = new MyLocationOverlay(mMapView);
		// 设置定位数据
		myLocationOverlay.setData(locationData);
		mMapView.getOverlays().clear();
		// 添加定位图层
		mMapView.getOverlays().add(myLocationOverlay);
		myLocationOverlay.enableCompass();
		// 修改定位数据后刷新图层生效
		mMapView.refresh();
		mkSearch = new MKSearch();
		mkSearch.init(mapManager, new MySearchListener());

		// handler = new MyHandler1(this);

		dialog = new ProgressDialog(this);
		dialog.setTitle("提示");
		dialog.setMessage("正在获取数据，请稍后...");
		dialog.setCancelable(true);
		dialog.setIndeterminate(false);

		et = (TextView) findViewById(R.id.tv_searchbox_home_text);
		zoom_in = (ImageButton) findViewById(R.id.zoom_in);
		zoom_out = (ImageButton) findViewById(R.id.zoom_out);
		
		sp_layout = (LinearLayout) findViewById(R.id.sp_layout);
		sp_province = (Spinner) findViewById(R.id.spinner1);
		sp_city = (Spinner) findViewById(R.id.spinner2);
		sp_area = (Spinner) findViewById(R.id.spinner3);

		et.setEnabled(false);// 定位完成后再使能，不然容易报错！
		et.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				sp_layout.setVisibility(View.VISIBLE);
				flag = false;
			}
		});

		mMapView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				sp_layout.setVisibility(View.GONE);
			}
		});

		
		zoom_in.setOnClickListener(new OnClickListener() {


			@Override
			public void onClick(View v) {
				currentZoom = (int) mMapView.getZoomLevel();
				if(currentZoom<maxZoom){
					mapController.setZoom(currentZoom+1);
					currentZoom = currentZoom + 1 ;
					if(currentZoom>=maxZoom){
						zoom_in.setEnabled(false);
						Toast.makeText(getApplicationContext(), ToastStrings.ZOOM_IN, Toast.LENGTH_SHORT).show();
					}
				}
			}
		});

		zoom_out.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				currentZoom = (int) mMapView.getZoomLevel();
				if(currentZoom>minZoom){
					mapController.setZoom(currentZoom-1);
					currentZoom = currentZoom - 1;
					if(currentZoom<=minZoom){
						zoom_out.setEnabled(false);
						Toast.makeText(getApplicationContext(), ToastStrings.ZOOM_OUT, Toast.LENGTH_SHORT).show();
					}
				}
			}
		});

		setSpinner();

		MyApplication.getInstance().addActivity(this);

	}

	private void initOverlay() {

		mOverlay = new MyOverlay(getResources().getDrawable(
				R.drawable.icon_gcoding), mMapView);
		viewCache = getLayoutInflater().inflate(R.layout.layout_popup, null);
		popupInfo = viewCache.findViewById(R.id.popup);
		popupText = (TextView) viewCache.findViewById(R.id.textcache);

		if (CompanyPositions.length > 0) {
			for (int i = 0; i < CompanyPositions.length; i++) {
				double mLng = Double
						.parseDouble(CompanyPositions[i].split(",")[0]);
				double mLat = Double
						.parseDouble(CompanyPositions[i].split(",")[1]);
				int mRiskValue = Integer.parseInt(CompanyPositions[i]
						.split(",")[2]);
				GeoPoint p = new GeoPoint((int) (mLat * 1E6),
						(int) (mLng * 1E6));
				OverlayItem item = new OverlayItem(p, unitAddresses[i], "");
				switch (mRiskValue) {
				case 1:
					item.setMarker(getResources().getDrawable(R.drawable.blue));
					break;
				case 2:
					item.setMarker(getResources().getDrawable(R.drawable.blue));
					break;
				case 3:
					item.setMarker(getResources().getDrawable(R.drawable.green));
					break;
				case 4:
					item.setMarker(getResources()
							.getDrawable(R.drawable.yellow));
					break;
				case 5:
					item.setMarker(getResources().getDrawable(R.drawable.pink));
					break;
				case 6:
					item.setMarker(getResources().getDrawable(R.drawable.red));
					break;
				}

				mOverlay.addItem(item);
			}
			mMapView.getOverlays().add(mOverlay);
			mMapView.refresh();
			sp_layout.setVisibility(View.GONE);

			PopupClickListener popListener = new PopupClickListener() {

				@Override
				public void onClickedPopup(int arg0) {

					startActivity(it);
				}
			};

			pop = new PopupOverlay(mMapView, popListener);
		}

	}

	private void removeOverlay() {
		mMapView.getOverlays().clear();
		mMapView.refresh();
	}


	private void setSpinner() {

		sp_province
				.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						if (flag) {
							new Thread(new GetCityThreadAfterLocation())
									.start();
						} else {
							// 此线程初始化city数据的同时，获取province的lat和lng
							new Thread(new GetCityThread()).start();
							if (sp_province.getSelectedItem().toString() != provinces[0]) {
								dialog.show();
							}

						}

					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {

					}
				});

		sp_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				//FLAG作用是定位后且设置spinner，才能允许用户点击搜索框，，进行再次spinner搜索。否则定位未完成，点击搜索框，则报错。
				if (flag) {
					new Thread(new GetAreaThreadAfterLocation()).start();
				} else {
					new Thread(new GetAreaThread()).start();
					if (sp_city.getSelectedItem().toString() != cities[0]) {
						dialog.show();
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}

		});

		sp_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				// 改变地图坐标的同时，获取所有区域内公司的经纬度信息并加载标记点
				new Thread(new AreaThread()).start();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

	}

	@Override
	protected void onPause() {

		mMapView.onPause();
		if (mapManager != null) {
			mapManager.stop();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {

		mMapView.onResume();
		if (mapManager != null) {
			mapManager.start();
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {

		mMapView.destroy();
		if (mapManager != null) {
			mapManager.destroy();
			mapManager = null;
		}
		if (myLocationClient != null) {
			myLocationClient.stop();
		}

		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mMapView.onRestoreInstanceState(savedInstanceState);
	}

	/** 
	 * 菜单、返回键响应 
	 */  
	@Override  
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
		if(keyCode == KeyEvent.KEYCODE_BACK)  
		{    
			exitBy2Click();      //调用双击退出函数  
		}  
		return false;  
	}  
	/** 
	 * 双击退出函数 
	 */  
	private static Boolean isExit = false;  

	private void exitBy2Click() {  
		Timer tExit = null;  
		if (isExit == false) {  
			isExit = true; // 准备退出  
			Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();  
			tExit = new Timer();  
			tExit.schedule(new TimerTask() {  
				@Override  
				public void run() {  
					isExit = false; // 取消退出  
				}  
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务  

		} else {  
			MyApplication.getInstance().exit();
		}  
	}
	 


	static class MyHandler extends Handler {
		WeakReference<MapActivity> mActivity;

		MyHandler(MapActivity activity) {
			mActivity = new WeakReference<MapActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			MapActivity theActivity = mActivity.get();
			if (msg.what == 1) {
				theActivity.removeOverlay();
				theActivity.sp_province.setAdapter(new ArrayAdapter<String>(
						theActivity, R.layout.item_sp_types, provinces));
				theActivity.dialog.dismiss();
			} else if (msg.what == 2) {
				theActivity.removeOverlay();
				theActivity.sp_city.setAdapter(new ArrayAdapter<String>(
						theActivity, R.layout.item_sp_types, cities));
				theActivity.dialog.dismiss();
				if (msg.obj != null) {
					Double lat = Double.parseDouble(msg.obj.toString().split(
							",")[0]);
					Double lng = Double.parseDouble(msg.obj.toString().split(
							",")[1]);
					GeoPoint point = new GeoPoint((int) (lng * 1E6),
							(int) (lat * 1E6));
					theActivity.mapController.setZoom(msg.arg1);
					theActivity.mapController.setCenter(point);
				}
			} else if (msg.what == 3) {
				theActivity.removeOverlay();
				theActivity.sp_area.setAdapter(new ArrayAdapter<String>(
						theActivity, R.layout.item_sp_types, areas));
				theActivity.dialog.dismiss();
				if (msg.obj != null) {
					Double lat = Double.parseDouble(msg.obj.toString().split(
							",")[0]);
					Double lng = Double.parseDouble(msg.obj.toString().split(
							",")[1]);
					GeoPoint point = new GeoPoint((int) (lng * 1E6),
							(int) (lat * 1E6));
					theActivity.mapController.setZoom(msg.arg1);
					theActivity.mapController.setCenter(point);
				}
			} else if (msg.what == 4) {
				theActivity.removeOverlay();
				if (msg.obj != null) {
					Double lat = Double.parseDouble(msg.obj.toString().split(
							",")[0]);
					Double lng = Double.parseDouble(msg.obj.toString().split(
							",")[1]);
					GeoPoint point = new GeoPoint((int) (lng * 1E6),
							(int) (lat * 1E6));
					theActivity.mapController.setZoom(msg.arg1);
					theActivity.mapController.setCenter(point);
					theActivity.initOverlay();
				}
			}
			// 通过定位得到覆盖物的数据处理
			else if (msg.what == 5) {
				if (CompanyPositions.length > 0) {
					Toast.makeText(theActivity,
							"你所在位置为\n" + provinceName + cityName + areaName,
							Toast.LENGTH_SHORT).show();
					theActivity.initOverlay();
				} else {
					Toast.makeText(theActivity,
							"你所在位置为" + provinceName + cityName + areaName,
							Toast.LENGTH_SHORT).show();
					Toast.makeText(theActivity, "你所在区域没有设备！",
							Toast.LENGTH_SHORT).show();
				}
			}
			// 通过位置信息设置spinner省默认值
			else if (msg.what == 6) {
				theActivity.sp_province.setAdapter(new ArrayAdapter<String>(
						theActivity, R.layout.item_sp_types, provinces));
				for (int i = 0; i < provinces.length; i++) {
					// theActivity.address.setText(provinceName);
					if (provinces[i].equals(provinceName)) {
						theActivity.sp_province.setSelection(i, true);
					}
				}
			}
			// 通过位置信息设置spinner城市默认值
			else if (msg.what == 7) {
				theActivity.sp_city.setAdapter(new ArrayAdapter<String>(
						theActivity, R.layout.item_sp_types, cities));
				for (int i = 0; i < cities.length; i++) {
					if (cities[i].equals(cityName)) {
						theActivity.sp_city.setSelection(i, true);
					}
				}
			}
			// 通过位置信息设置spinner地区默认值
			else if (msg.what == 8) {
				theActivity.sp_area.setAdapter(new ArrayAdapter<String>(
						theActivity, R.layout.item_sp_types, areas));
				for (int i = 0; i < areas.length; i++) {
					if (areas[i].equals(areaName)) {
						theActivity.sp_area.setSelection(i, true);
						Toast.makeText(theActivity, "3！", Toast.LENGTH_SHORT)
								.show();
					}
				}
				theActivity.et.setEnabled(true);
			}
		}

	}

	class MyOverlay extends ItemizedOverlay<OverlayItem> {

		public MyOverlay(Drawable arg0, MapView arg1) {
			super(arg0, arg1);

		}

		@Override
		public boolean onTap(GeoPoint arg0, MapView arg1) {

			if (pop != null) {
				pop.hidePop();
			}

			return false;
		}

		@Override
		protected boolean onTap(int arg0) {

			OverlayItem item = getItem(arg0);
			popupText.setText(item.getTitle());
			it = new Intent(MapActivity.this, EquipInfoActivity.class);
			it.putExtra("unitAddress", item.getTitle());
			Bitmap[] bitMaps = { BMapUtils.getBitmapFromView(popupInfo) };
			pop.showPopup(bitMaps, item.getPoint(), 32);

			return true;
		}
	}

	class AreaThread implements Runnable {

		@Override
		public void run() {

			List<String> list = null;
			HashMap<String, String> params = new HashMap<String, String>();
			if (sp_area.getSelectedItem() != areas[0]) {
				params.put("province", sp_province.getSelectedItem().toString());
				params.put("city", sp_city.getSelectedItem().toString());
				params.put("area", sp_area.getSelectedItem().toString());
				Message msg = Message.obtain();
				responseMsg = CasClient.getInstance().doPost(
						UrlStrings.GET_LAT_LNG_BY_AREA, params);
				String message = CasClient.getInstance().doPost(
						UrlStrings.GET_AREA_INFO, params);
				try {
					msg.obj = JsonUtils.getLatLng(responseMsg);
					list = JsonUtils.getCompanyPosition(message);
					CompanyPositions = list.toArray(new String[list.size()]);
					list = JsonUtils.getUnitAddress(message);
					unitAddresses = list.toArray(new String[list.size()]);
					msg.what = 4;
					msg.arg1 = ZOOM_LEVEL_AREA;
				} catch (Exception e) {

					e.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}

	}

	class locationAreaThread implements Runnable {

		private String province;
		private String city;
		private String area;

		public locationAreaThread(String province, String city, String area) {
			this.province = province;
			this.city = city;
			this.area = area;
		}

		@Override
		public void run() {

			List<String> list = null;
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("province", province);
			params.put("city", city);
			params.put("area", area);
			Message msg = Message.obtain();
			String message = CasClient.getInstance().doPost(
					UrlStrings.GET_AREA_INFO, params);
			try {
				list = JsonUtils.getCompanyPosition(message);
				CompanyPositions = list.toArray(new String[list.size()]);
				msg.what = 5;

			} catch (Exception e) {
				e.printStackTrace();
			}
			handler.sendMessage(msg);
		}
	}

	class GetProvinceThread implements Runnable {

		@Override
		public void run() {

			List<String> list = null;
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("list", "得到权限内所有省份");
			String message = CasClient.getInstance().doPost(
					UrlStrings.GET_PROVINCE_LIST, params);
			Message msg = Message.obtain();
			try {
				list = JsonUtils.initProvince(message);
				provinces = list.toArray(new String[list.size()]);
				msg.what = 1;
				handler.sendMessage(msg);
			} catch (Exception e) {

				e.printStackTrace();
			}
		}

	}

	class GetProvinceThreadAfterLocation implements Runnable {

		@Override
		public void run() {

			List<String> list = null;
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("list", "得到权限内所有省份");
			String message = CasClient.getInstance().doPost(
					UrlStrings.GET_PROVINCE_LIST, params);
			Message msg = Message.obtain();
			try {
				list = JsonUtils.initProvince(message);
				provinces = list.toArray(new String[list.size()]);
				msg.what = 6;
				handler.sendMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	class GetCityThread implements Runnable {

		@Override
		public void run() {

			List<String> list = null;
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("province", sp_province.getSelectedItem().toString());
			Message msg = Message.obtain();
			String message = CasClient.getInstance().doPost(
					UrlStrings.GET_CITY_BY_PROVINCE, params);
			if (sp_province.getSelectedItem().toString() != provinces[0]) {
				String message2 = CasClient.getInstance().doPost(
						UrlStrings.GET_LAT_LNG_BY_PROVINCE,
						params);
				try {
					String latlng = JsonUtils.getLatLng(message2);
					msg.obj = latlng;
				} catch (Exception e) {

					e.printStackTrace();
				}
			}
			try {
				list = JsonUtils.initCity(message);
				cities = list.toArray(new String[list.size()]);
				msg.what = 2;
				msg.arg1 = ZOOM_LEVEL_PROVINCE;
				handler.sendMessage(msg);
			} catch (Exception e) {

				e.printStackTrace();
			}
		}

	}

	class GetCityThreadAfterLocation implements Runnable {
		@Override
		public void run() {
			List<String> list = null;
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("province", sp_province.getSelectedItem().toString());
			Message msg = Message.obtain();
			String message = CasClient.getInstance().doPost(
					UrlStrings.GET_CITY_BY_PROVINCE, params);
			try {
				list = JsonUtils.initCity(message);
				cities = list.toArray(new String[list.size()]);
				msg.what = 7;
				handler.sendMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	class GetAreaThread implements Runnable {

		@Override
		public void run() {

			List<String> list = null;
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("province", sp_province.getSelectedItem().toString());
			params.put("city", sp_city.getSelectedItem().toString());
			Message msg = Message.obtain();
			String message = CasClient.getInstance().doPost(
					UrlStrings.GET_AREA_BY_CITY, params);
			if (sp_city.getSelectedItem().toString() != cities[0]) {
				String message2 = CasClient.getInstance().doPost(
						UrlStrings.GET_LAT_LNG_BY_CITY, params);
				try {
					String latlng = JsonUtils.getLatLng(message2);
					msg.obj = latlng;
				} catch (Exception e) {

					e.printStackTrace();
				}
			}
			try {
				list = JsonUtils.initArea(message);
				areas = list.toArray(new String[list.size()]);
				msg.what = 3;
				msg.arg1 = ZOOM_LEVEL_CITY;
				handler.sendMessage(msg);
			} catch (Exception e) {

				e.printStackTrace();
			}
		}

	}

	class GetAreaThreadAfterLocation implements Runnable {

		@Override
		public void run() {

			List<String> list = null;
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("province", sp_province.getSelectedItem().toString());
			params.put("city", sp_city.getSelectedItem().toString());
			Message msg = Message.obtain();
			String message = CasClient.getInstance().doPost(
					UrlStrings.GET_AREA_BY_CITY, params);
			try {
				list = JsonUtils.initArea(message);
				areas = list.toArray(new String[list.size()]);
				msg.what = 8;
				handler.sendMessage(msg);
			} catch (Exception e) {

				e.printStackTrace();
			}
		}

	}

	public class MySearchListener implements MKSearchListener {
		public void onGetAddrResult(MKAddrInfo result, int error) {
			if (error != 0 || result == null) {
				Toast.makeText(MapActivity.this, "获取地理信息失败", Toast.LENGTH_LONG)
						.show();
			} else {

				provinceName = result.addressComponents.province;
				provinceName = provinceName.substring(0,
						provinceName.length() - 1);
				cityName = result.addressComponents.city;
				areaName = result.addressComponents.district;
				Log.i("ditu", provinceName + cityName + areaName);
				new Thread(new locationAreaThread(provinceName, cityName,
						areaName)).start();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				new Thread(new GetProvinceThreadAfterLocation()).start();
			}

		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1,
				int arg2) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}
	}
}
