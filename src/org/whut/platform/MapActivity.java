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
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.util.Log;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class MapActivity extends Activity {
	private BMapManager mapManager;
	private MapView mMapView;
	private MapController mapController;
	private TextView et;
	private static String[] CompanyPositions;
	private static String[] unitAddresses;
	private MyHandler handler;
	private ViewSwitcher viewSwitcher;

	private ProgressDialog dialog;
	private MyOverlay mOverlay = null;

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

	//记录intent中的省市区名字
	private String provinceInIntent;
	private String cityInIntent;
	private String areaInIntent;

	private int user_id;
	
	private ViewStub viewStub ;
	private View searchbar;
	private View popupbottom;

	private ImageView title_bar_left;
	private TextView title_bar_middle;
	private TextView title_bar_right;

	private LinearLayout button_box;
	private LinearLayout button_previous;
	private LinearLayout button_next;
	private LinearLayout button_phone;
	private Button button_show_detail;

	//记录ViewStub是否infalte过
	private static boolean isInflated = false;
	
	
	/*	记录是否隐藏View是否正在显示，此处为public，便于后
	 * 面tag判断时在其他Activity中动态修改
	 */
	public static boolean view_record = false;
	
	
	//记录点击的标记点,离开此界面时一定要置为0
	private static int tap_record = 0;
	//记录按钮标记点的顺序 
	private static int show_record = 0;
	//记录定位图层是否存在
	private static boolean location_record = true;
	//记录用户选择信息
	private static int selectionInIntent = 0;
	//记录电话信息
	private static int phone_record;

	private ImageButton zoom_in;
	private ImageButton zoom_out;
	private static final int maxZoom = 19;
	private static final int minZoom = 3;
	private static int currentZoom = 0;
	private static final float ZOOM_LOCATION =  (float) 13;

	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null){
				return;
			}
			locationData.latitude = location.getLatitude();
			locationData.longitude = location.getLongitude();
			locationData.accuracy = location.getRadius();
			locationData.direction = location.getDerect();
			myLocationOverlay.setData(locationData);
			//Log.i("msg", "..");
			mMapView.refresh();

			if (isRequest || isFirstLoc) {
				// 移动地图到定位点
				point = new GeoPoint((int) (locationData.latitude * 1e6),
						(int) (locationData.longitude * 1e6));
				mapController.animateTo(point);
				mapController.setCenter(point);
				//	Log.i("msg", locationData.latitude+","+locationData.longitude);
				isRequest = false;
				myLocationOverlay.setLocationMode(LocationMode.NORMAL);
				mkSearch.reverseGeocode(point);
				//				Log.i("location", Double.toString(locationData.longitude));
				//				Log.i("location", Double.toString(locationData.latitude));
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

	private void initListener(final int i){
		if(!isInflated){
			showPopupBeforInflated();
		}else{
			showPopupAfterInflated();
		}
		//inflate之后获取组件
		title_bar_left = (ImageView) findViewById(R.id.iv_searchbox_search_back);
		title_bar_middle = (TextView) findViewById(R.id.edittext_searchbox_search_input);
		title_bar_right = (TextView) findViewById(R.id.tv_searchbox_list);
		button_previous = (LinearLayout) findViewById(R.id.btn_poidetail_nearby);
		button_next = (LinearLayout)findViewById(R.id.btn_poidetail_nav);
		button_phone = (LinearLayout) findViewById(R.id.btn_poidetail_phone);			

		//title_bar左边返回键
		title_bar_left.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(view_record){
					hidePopup();
					iconTapReturn(show_record);
					iconReturn(show_record);
				}
			}
		});

		//中间标题
		if(provinceInIntent!=null&&cityInIntent!=null&&areaInIntent!=null){
			//	Log.i("msg", provinceName+cityName+areaName+provinceInIntent+cityInIntent+areaInIntent);
			title_bar_middle.setText(provinceInIntent+cityInIntent+areaInIntent);
		}else{
			//	Log.i("msg", provinceName+cityName+areaName+provinceInIntent+cityInIntent+areaInIntent);
			title_bar_middle.setText(provinceName+cityName+areaName);
		}

		//右边切换到列表
		title_bar_right.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Intent it = new Intent(MapActivity.this,PlaceRankActivity.class);
				if(provinceInIntent!=null&&cityInIntent!=null&&areaInIntent!=null){
					it.putExtra("province", provinceInIntent);
					it.putExtra("city", cityInIntent);
					it.putExtra("area",areaInIntent);
					//表示是从地图页跳过去的
					it.putExtra("user_id", user_id);
					it.putExtra("tag", "from_map_to_list");
				}else{
					it.putExtra("province", provinceName);
					it.putExtra("city", cityName);
					it.putExtra("area",areaName);
					it.putExtra("user_id", user_id);
					it.putExtra("tag", "from_map_to_list");
				}
				show_record = 0;
				view_record = false;
				startActivity(it);
			}
		});



		button_next.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(show_record+1<CompanyPositions.length){
					iconReturn(show_record);
					show_record = show_record+1;
					initOverlayData(show_record);
					//	Log.i("msg", show_record+"没有溢出，数组长度为"+CompanyPositions.length);
				}else{
					iconReturn(show_record);
					show_record = 0;
					initOverlayData(show_record);
					//	Log.i("msg", show_record+"溢出");
				}
			}
		});	

		button_previous.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(show_record-1>0){
					iconReturn(show_record);
					show_record = show_record - 1;
					initOverlayData(show_record);
				}else{
					iconReturn(show_record);
					show_record = CompanyPositions.length-1;
					initOverlayData(show_record);
				}
			}
		});

		button_phone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(CompanyPositions[tap_record].split(",")[5]==null){
					Toast.makeText(getApplicationContext(), "暂无负责人联系方式", Toast.LENGTH_SHORT).show();
				}else{
					Builder alertDialog = new AlertDialog.Builder(MapActivity.this);		    
					alertDialog.setTitle(CompanyPositions[tap_record].split(",")[4]);

					switch(CompanyPositions[tap_record].split(",")[5].split("/").length){
					case 1:
						alertDialog.setSingleChoiceItems(new String[] {CompanyPositions[tap_record].split(",")[5].split("/")[0]}, 0, 
								new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								phone_record = which;
							}
						})
						.setPositiveButton("取消",new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						}).setNegativeButton("呼叫", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								Intent it = new Intent();
								it.setAction(Intent.ACTION_CALL);
								it.setData(Uri.parse("tel:"+CompanyPositions[tap_record].split(",")[5].split("/")[phone_record]));
								Log.i("msg", CompanyPositions[tap_record].split(",")[5].split("/")[phone_record]);
								startActivity(it);
								dialog.dismiss();
							}
						}).show();
						break;
					case 2:
						alertDialog.setSingleChoiceItems(new String[] {CompanyPositions[tap_record].split(",")[5].split("/")[0],CompanyPositions[tap_record].split(",")[5].split("/")[1]}, 0, new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								phone_record = which;	
							}
						}).setNegativeButton("呼叫",new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								Intent it = new Intent();
								it.setAction(Intent.ACTION_CALL);
								it.setData(Uri.parse("tel:"+CompanyPositions[tap_record].split(",")[5].split("/")[phone_record]));
								Log.i("msg", CompanyPositions[tap_record].split(",")[5].split("/")[phone_record]);
								startActivity(it);
								dialog.dismiss();
							}


						}).setPositiveButton("取消", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						}).show();
						break;
					}
				}
			}
		});

		button_show_detail = (Button) findViewById(R.id.btn_poidetail_showmap);
		button_show_detail.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(MapActivity.this,EquipInfoActivity.class);
				it.putExtra("unitAddress", unitAddresses[tap_record]);
				//标记，表示是从地图页跳过去
				it.putExtra("tag", tap_record);
				it.putExtra("user_id", user_id);
				startActivity(it);
			}
		});
	}


	private void iconTapReturn(int i){
		if(tap_record!=i){
			switch(Integer.parseInt(CompanyPositions[tap_record].split(",")[2])){
			case 1:
				mOverlay.getAllItem().get(tap_record).setMarker(getResources().getDrawable(R.drawable.blue));
				break;
			case 2:
				mOverlay.getAllItem().get(tap_record).setMarker(getResources().getDrawable(R.drawable.blue));
				break;
			case 3:
				mOverlay.getAllItem().get(tap_record).setMarker(getResources().getDrawable(R.drawable.green));
				break;
			case 4:
				mOverlay.getAllItem().get(tap_record).setMarker(getResources().getDrawable(R.drawable.green));
				break;
			case 5:
				mOverlay.getAllItem().get(tap_record).setMarker(getResources().getDrawable(R.drawable.yellow));
				break;
			case 6:
				mOverlay.getAllItem().get(tap_record).setMarker(getResources().getDrawable(R.drawable.yellow));
				break;
			case 7:
				mOverlay.getAllItem().get(tap_record).setMarker(getResources().getDrawable(R.drawable.pink));
				break;
			case 8:
				mOverlay.getAllItem().get(tap_record).setMarker(getResources().getDrawable(R.drawable.pink));
				break;
			case 9:
				mOverlay.getAllItem().get(tap_record).setMarker(getResources().getDrawable(R.drawable.red));
				break;
			case 10:
				mOverlay.getAllItem().get(tap_record).setMarker(getResources().getDrawable(R.drawable.red));
				break;
			}
			mOverlay.updateItem(mOverlay.getAllItem().get(tap_record));
			mMapView.refresh();
		}
	}



	private void initOverlayData(int i){
		show_record=i;
		((TextView)findViewById(R.id.tv_setctionA)).setText((i+1)+"."+unitAddresses[i]);
		((TextView)findViewById(R.id.tv_sectionB)).setText("设备数量："+CompanyPositions[i].split(",")[3]);
		((TextView)findViewById(R.id.tv_sectionC)).setText("负责人："+CompanyPositions[i].split(",")[4]);
		((TextView)findViewById(R.id.tv_sectionD)).setText("风险值："+CompanyPositions[i].split(",")[2]);
		mOverlay.getAllItem().get(i).setMarker(getResources().getDrawable(R.drawable.select_icon3));
		tap_record = i;
		mOverlay.updateItem(mOverlay.getAllItem().get(i));
		Double lat = Double.parseDouble(CompanyPositions[i].split(
				",")[0]);
		Double lng = Double.parseDouble(CompanyPositions[i].split(
				",")[1]);
		//		Log.i("msg", lat+","+lng);
		GeoPoint tap_point = new GeoPoint((int) (lng * 1E6),
				(int) (lat * 1E6));		
		mapController.animateTo(tap_point);
		mapController.setCenter(tap_point);
		mMapView.refresh();
		view_record = true;
	}


	private void showPopupAfterInflated(){
		searchbar.setFocusable(false);
		searchbar.setVisibility(View.GONE);
		button_box.setFocusable(false);
		button_box.setVisibility(View.GONE);
		popupbottom.setVisibility(View.VISIBLE);
		popupbottom.setFocusable(true);
		isInflated = true;
	}

	private void showPopupBeforInflated(){
		searchbar.setFocusable(false);
		searchbar.setVisibility(View.GONE);
		button_box.setFocusable(false);
		button_box.setVisibility(View.GONE);
		viewStub.inflate();
		popupbottom = findViewById(viewStub.getInflatedId());
		isInflated = true;
	};

	private void hidePopup(){
		searchbar.setVisibility(View.VISIBLE);
		searchbar.setFocusable(true);
		button_box.setVisibility(View.VISIBLE);
		button_box.setFocusable(true);
		popupbottom.setVisibility(View.GONE);
		view_record = false;
	}

	private void iconReturn(int i){
		switch(Integer.parseInt(CompanyPositions[i].split(",")[2])){
		case 1:
			mOverlay.getAllItem().get(i).setMarker(getResources().getDrawable(R.drawable.blue));
			break;
		case 2:
			mOverlay.getAllItem().get(i).setMarker(getResources().getDrawable(R.drawable.blue));
			break;
		case 3:
			mOverlay.getAllItem().get(i).setMarker(getResources().getDrawable(R.drawable.green));
			break;
		case 4:
			mOverlay.getAllItem().get(i).setMarker(getResources().getDrawable(R.drawable.green));
			break;
		case 5:
			mOverlay.getAllItem().get(i).setMarker(getResources().getDrawable(R.drawable.yellow));
			break;
		case 6:
			mOverlay.getAllItem().get(i).setMarker(getResources().getDrawable(R.drawable.yellow));
			break;
		case 7:
			mOverlay.getAllItem().get(i).setMarker(getResources().getDrawable(R.drawable.pink));
			break;
		case 8:
			mOverlay.getAllItem().get(i).setMarker(getResources().getDrawable(R.drawable.pink));
			break;
		case 9:
			mOverlay.getAllItem().get(i).setMarker(getResources().getDrawable(R.drawable.red));
			break;
		case 10:
			mOverlay.getAllItem().get(i).setMarker(getResources().getDrawable(R.drawable.red));
			break;
		}
		mOverlay.updateItem(mOverlay.getAllItem().get(i));
		mMapView.refresh();
	}

	private void setSelectionData(int i){
		mOverlay.getAllItem().get(i).setMarker(getResources().getDrawable(R.drawable.select_icon2));
		if(view_record){
			showPopupAfterInflated();
			if(areaInIntent!=areaName){
				removeOverlay();
			}
			initOverlayData(i);
			initListener(i);
		}else{
			showPopupBeforInflated();
			initOverlayData(i);
			initListener(i);
		}
	}





	private void getIntentData(Intent it){
		if(it.getExtras()!=null){
			provinceInIntent = it.getExtras().getString("province");
			cityInIntent = it.getExtras().getString("city");
			areaInIntent = it.getExtras().getString("area");
			user_id = it.getExtras().getInt("user_id");
			Log.i("msg", "登录的用户id为"+user_id);
			isInflated=false;
		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//初始化数据
		getIntentData(getIntent());

		mapManager = new BMapManager(getApplication());
		mapManager.init(new MKGeneralListener() {

			@Override
			public void onGetPermissionState(int arg0) {

				Toast.makeText(getApplicationContext(),
						ToastStrings.KEY_FAILED, Toast.LENGTH_SHORT).show();
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
		
		/*此处判断是否从登录页面跳转过来，如果是的，表示是第一次登录，Intent中
		*	不含有province等数据
		*/
		if(getIntent().getExtras().getString("province")!=null){
			isFirstLoc = false;
			new Thread(new getCompanyListInMap()).start();
		}


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

				if(currentZoom>=maxZoom){
					zoom_in.setEnabled(false);
				}

				if(currentZoom<=minZoom){
					zoom_out.setEnabled(false);
				}
			}
		});

		//定位Button
		viewSwitcher = (ViewSwitcher) findViewById(R.id.location);
		viewSwitcher.setDisplayedChild(0);
		viewSwitcher.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				viewSwitcher.showNext();
				removeOverlay();
				
				//点击定位button，清除Overlay数据后，intent中的参数置空，不然分布图显示的仍是Intent中所带过来的省市区
				provinceInIntent = null;
				cityInIntent = null;
				areaInIntent = null;
				
				tap_record = 0;
				isRequest = true;
				myLocationClient.start();
				if(!location_record){
					mMapView.getOverlays().add(myLocationOverlay);
					mapController.setZoom(ZOOM_LOCATION);
					mMapView.refresh();
					location_record = true;
				}
			}
		});


		//清除标记点数据
		ImageButton button_delete = (ImageButton) findViewById(R.id.map_clear);
		button_delete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(view_record){
					hidePopup();
				}
				removeOverlay();
			}
		});

		viewStub = (ViewStub) findViewById(R.id.view_stub);
		searchbar  = findViewById(R.id.searchbar);
		button_box = (LinearLayout) findViewById(R.id.buttonsbox);

		// 定位初始化
		myLocationClient = new LocationClient(getApplicationContext());
		locationData = new LocationData();
		myLocationClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		myLocationClient.setLocOption(option);
		viewSwitcher.showNext();
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

		dialog = new ProgressDialog(this);
		dialog.setTitle("提示");
		dialog.setMessage("正在获取数据，请稍后...");
		dialog.setCancelable(true);
		dialog.setIndeterminate(false);

		et = (TextView) findViewById(R.id.tv_searchbox_home_text);
		zoom_in = (ImageButton) findViewById(R.id.zoom_in);
		zoom_out = (ImageButton) findViewById(R.id.zoom_out);
		et.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(MapActivity.this,MoreSearchActivity.class);
				it.putExtra("provinceName",provinceName);
				it.putExtra("cityName",cityName);
				it.putExtra("areaName",areaName);
				tap_record = 0;
				show_record = 0;
				startActivity(it);
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


		RelativeLayout bottom_btn_rank = (RelativeLayout) findViewById(R.id.route);
		bottom_btn_rank.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				tap_record = 0;
				show_record = 0;
				Intent it = new Intent(MapActivity.this,ProvinceRankActivity.class);
				it.putExtra("user_id", user_id);
				startActivity(it);
			}
		});

		RelativeLayout bottom_btn_distribute = (RelativeLayout) findViewById(R.id.nearby);
		bottom_btn_distribute.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it=new Intent(MapActivity.this,AreaRankPieChart.class);
				if(provinceInIntent!=null){
					it.putExtra("province",provinceInIntent);
					it.putExtra("city",cityInIntent);
					it.putExtra("area",areaInIntent);
					Log.i("msg", provinceInIntent+cityInIntent+areaInIntent);
					startActivity(it);	
				}else{
					it.putExtra("province", provinceName);
					it.putExtra("city", cityName);
					it.putExtra("area", areaName);
					startActivity(it);
				}
			}
		});
		
		RelativeLayout bottom_btn_collection = (RelativeLayout) findViewById(R.id.navi);
		bottom_btn_collection.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent it = new Intent(MapActivity.this,CollectionActivity.class);
				it.putExtra("user_id", user_id);
				startActivity(it);
			}
		});
		
		
		MyApplication.getInstance().addActivity(this);

	}

	private void initOverlay() {
		mOverlay = new MyOverlay(getResources().getDrawable(
				R.drawable.icon_gcoding), mMapView);
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
					item.setMarker(getResources().getDrawable(R.drawable.green));
					break;
				case 5:
					item.setMarker(getResources().getDrawable(R.drawable.yellow));
					break;
				case 6:
					item.setMarker(getResources().getDrawable(R.drawable.yellow));
					break;
				case 7:
					item.setMarker(getResources().getDrawable(R.drawable.pink));
					break;
				case 8:
					item.setMarker(getResources().getDrawable(R.drawable.pink));
					break;
				case 9:
					item.setMarker(getResources().getDrawable(R.drawable.red));
					break;
				case 10:
					item.setMarker(getResources().getDrawable(R.drawable.red));
					break;
				}
				mOverlay.addItem(item);	
			}	
		}
		mMapView.getOverlays().add(mOverlay);
		mMapView.refresh();
	}

	private void removeOverlay() {
		location_record = false;
		mMapView.getOverlays().clear();
		mMapView.refresh();
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
			if(view_record){
				hidePopup();
				iconReturn(tap_record);	
			}else{
				//调用双击退出函数
				exitBy2Click();
			}

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
			if (msg.what == 5) {
				Log.i("msg","msg.what=5");
				if (CompanyPositions.length > 0) {
					Toast.makeText(theActivity,
							"你所在位置为" + provinceName + cityName + areaName,
							Toast.LENGTH_SHORT).show();
					theActivity.initOverlay();
					theActivity.myLocationClient.stop();
					theActivity.viewSwitcher.showNext();
				} else {
					Toast.makeText(theActivity,
							"你所在位置为" + provinceName + cityName + areaName,
							Toast.LENGTH_SHORT).show();
					Toast.makeText(theActivity, "你所在区域没有设备！",
							Toast.LENGTH_SHORT).show();
					theActivity.myLocationClient.stop();
					theActivity.viewSwitcher.showNext();
				}
			}else if(msg.what ==  4){
				theActivity.removeOverlay();
				theActivity.initOverlay();
				theActivity.viewSwitcher.showNext();
				theActivity.setSelectionData(selectionInIntent);
			}
		}

	}




	class MyOverlay extends ItemizedOverlay<OverlayItem> {

		public MyOverlay(Drawable arg0, MapView arg1) {
			super(arg0, arg1);
		}




		@Override
		public boolean onTap(GeoPoint arg0, MapView arg1) {

			return false;
		}

		@Override
		protected boolean onTap(int arg0) {
			iconTapReturn(arg0);			
			initListener(arg0);
			initOverlayData(arg0);
			return true;
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
				list = JsonUtils.getUnitAddress(message);
				unitAddresses = list.toArray(new String[list.size()]);
				msg.what = 5;
			} catch (Exception e) {
				e.printStackTrace();
			}
			handler.sendMessage(msg);
		}
	}

	class getCompanyListInMap implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			List<String> list = null;
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("province", getIntent().getExtras().getString("province"));
			params.put("city", getIntent().getExtras().getString("city"));
			params.put("area", getIntent().getExtras().getString("area"));
			Message msg = Message.obtain();
			String message = CasClient.getInstance().doPost(
					UrlStrings.GET_AREA_INFO, params);
			try {
				list = JsonUtils.getCompanyPosition(message);
				CompanyPositions = list.toArray(new String[list.size()]);
				list = JsonUtils.getUnitAddress(message);
				unitAddresses = list.toArray(new String[list.size()]);
				msg.what = 4;

			} catch (Exception e) {
				e.printStackTrace();
			}
			handler.sendMessage(msg);
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
				Log.i("msg", provinceName+cityName+areaName);
				new Thread(new locationAreaThread(provinceName, cityName,
						areaName)).start();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
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
