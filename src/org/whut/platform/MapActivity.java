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
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;


import android.app.Activity;
import android.app.ProgressDialog;
import android.view.MotionEvent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MapActivity extends Activity
{
	private BMapManager mapManager;    
	private MapView mMapView;    
	private MapController mapController;
	private EditText et;
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

	private ImageButton ibtn_home;
	private ImageButton ibtn_list;
	private ImageButton ibtn_risk;
	
	private PopupOverlay pop = null;

	private MyOverlay mOverlay = null;
	private TextView  popupText = null;
	private View popupInfo = null;
	private View viewCache=null;

	private static final float ZOOM_LEVEL_ALL =  (float) 4.6;
	private static final int ZOOM_LEVEL_PROVINCE = 8;
	private static final int ZOOM_LEVEL_CITY = 10;
	private static final int ZOOM_LEVEL_AREA = 13;

	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
		
		mapManager=new BMapManager(getApplication());  
		mapManager.init(new MKGeneralListener() {

			@Override
			public void onGetPermissionState(int arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), ToastStrings.KEY_FAILED, Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onGetNetworkState(int arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), ToastStrings.NETWORK_FAILED,Toast.LENGTH_SHORT).show();
			}
		});

		setContentView(R.layout.activity_map);

		handler = new MyHandler(this);

		mMapView = (MapView) findViewById(R.id.mMapView1);    

		mMapView.setBuiltInZoomControls(true); 
		
		
		GeoPoint point = new GeoPoint((int) (35.3349 * 1E6), (int) (103.2319* 1E6));    

		mapController = mMapView.getController();    

		mapController.setCenter(point);    

		mapController.setZoom(ZOOM_LEVEL_ALL);

		mapController.setCompassMargin(100, 100);
		



		dialog = new ProgressDialog(this);
		dialog.setTitle("提示");
		dialog.setMessage("正在获取数据，请稍后...");
		dialog.setCancelable(true);
		dialog.setIndeterminate(false);
				
		
		
		et = (EditText) findViewById(R.id.et);
		sp_layout = (LinearLayout) findViewById(R.id.sp_layout);
		sp_province = (Spinner) findViewById(R.id.spinner1);
		sp_city = (Spinner) findViewById(R.id.spinner2);
		sp_area = (Spinner) findViewById(R.id.spinner3);
		
		et.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sp_layout.setVisibility(View.VISIBLE);
			}
		});
		
		mMapView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				sp_layout.setVisibility(View.GONE);
			}
		});
		

		ibtn_list=(ImageButton)findViewById(R.id.ibtn_list);
		ibtn_list.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){     
		            //更改为按下时的背景图片     
					((ImageButton)v).setImageResource(R.drawable.actionbar_list_down);
					}
				else if(event.getAction() == MotionEvent.ACTION_UP){     
		            //改为抬起时的图片     
					((ImageButton)v).setImageResource(R.drawable.actionbar_list);  
					Intent it = new Intent(MapActivity.this,ShowListActivity.class);
					if(sp_province.getSelectedItem().toString()==provinces[0]||
							sp_city.getSelectedItem().toString()==cities[0]||
							sp_area.getSelectedItem().toString()==areas[0])
					{
						Toast.makeText(getApplicationContext(), ToastStrings.SELECTION_COMPLETE, Toast.LENGTH_SHORT).show();
					}else{
						it.putExtra("province", sp_province.getSelectedItem().toString());
						it.putExtra("city", sp_city.getSelectedItem().toString());
						it.putExtra("area",sp_area.getSelectedItem().toString());
						startActivity(it);
					}
				}
				return true;
			}
		});
	
		ibtn_home=(ImageButton)findViewById(R.id.ibtn_home);
		ibtn_home.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){     
		            //更改为按下时的背景图片     
					((ImageButton)v).setImageResource(R.drawable.actionbar_map_down);					
					}
				else if(event.getAction() == MotionEvent.ACTION_UP){     
		            //改为抬起时的图片     
					((ImageButton)v).setImageResource(R.drawable.actionbar_map);     
					
				}
				return true;
			}
		});
	
		
		ibtn_risk=(ImageButton)findViewById(R.id.ibtn_risk);
		ibtn_risk.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN){     
		            //更改为按下时的背景图片     
					((ImageButton)v).setImageResource(R.drawable.actionbar_risk_down);
					Intent intent=new Intent();

					if (sp_province.getSelectedItem().toString()!=provinces[0]) {
						intent.setClass(MapActivity.this, CityRankActivity.class);
						intent.putExtra("province", sp_province.getSelectedItem().toString());
						if (sp_city.getSelectedItem().toString()!=cities[0]) {
							intent.setClass(MapActivity.this, AreaRankActivity.class);
							intent.putExtra("city", sp_city.getSelectedItem().toString());
							if (sp_area.getSelectedItem().toString()!=areas[0]) {
								intent.setClass(MapActivity.this, PlaceRankActivity.class);
								intent.putExtra("area", sp_area.getSelectedItem().toString());

							}

						}
					}else {
						intent.setClass(MapActivity.this, RankActivity.class);
					}
					startActivity(intent);
					}
				else if(event.getAction() == MotionEvent.ACTION_UP){     
		            //改为抬起时的图片     
					((ImageButton)v).setImageResource(R.drawable.actionbar_risk);     
				}
				return true;
			}
		});


		//初始化spinner数据       
		initSpinner();

		setSpinner();
		
		MyApplication.getInstance().addActivity(this);

	}

	private void initOverlay() {
		// TODO Auto-generated method stub
		mOverlay = new MyOverlay(getResources().getDrawable(R.drawable.icon_gcoding), mMapView);
		viewCache = getLayoutInflater().inflate(R.layout.layout_popup,null);
		popupInfo = viewCache.findViewById(R.id.popup);
		popupText = (TextView) viewCache.findViewById(R.id.textcache);
		
		for(int i=0;i<CompanyPositions.length;i++){
			double mLng = Double.parseDouble(CompanyPositions[i].split(",")[0]);
			double mLat = Double.parseDouble(CompanyPositions[i].split(",")[1]);
			int mRiskValue = Integer.parseInt(CompanyPositions[i].split(",")[2]);
			GeoPoint p = new GeoPoint((int)(mLat*1E6),(int)(mLng*1E6));
			OverlayItem item = new OverlayItem(p, unitAddresses[i], "");
			switch(mRiskValue){
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
				item.setMarker(getResources().getDrawable(R.drawable.yellow));
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
				// TODO Auto-generated method stub				
				startActivity(it);
			}
		};

		pop=new PopupOverlay(mMapView,popListener);

	}

	private void removeOverlay(){
		mMapView.getOverlays().clear();
		mMapView.refresh();
	}
	
	private void initSpinner(){		
		dialog.show();
		new Thread(new GetProvinceThread()).start();
		
	}

	private void setSpinner() {
		// TODO Auto-generated method stub

		sp_province.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				//此线程初始化city数据的同时，获取province的lat和lng
				new Thread(new GetCityThread()).start();
				if(sp_province.getSelectedItem().toString()!=provinces[0]){
					dialog.show();
				}
				

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub				
			}		
		});

		sp_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				new Thread(new GetAreaThread()).start();
				if(sp_city.getSelectedItem().toString()!=cities[0]){
					dialog.show();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

		sp_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				//改变地图坐标的同时，获取所有区域内公司的经纬度信息并加载标记点
				new Thread(new AreaThread()).start();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
		
		


	}

	@Override
	protected void onPause() {

		mMapView.onPause();
		if(mapManager!=null){
			mapManager.stop();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {

		mMapView.onResume();
		if(mapManager!=null){
			mapManager.start();
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {

		mMapView.destroy();
		if(mapManager!=null){
			mapManager.destroy();
			mapManager=null;
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
        // TODO Auto-generated method stub  
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

	static class MyHandler extends Handler{
		WeakReference<MapActivity> mActivity;

		MyHandler(MapActivity activity){
			mActivity = new WeakReference<MapActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			MapActivity theActivity = mActivity.get();
			if(msg.what==1){
				theActivity.removeOverlay();
				theActivity.sp_province.setAdapter(new ArrayAdapter<String>(theActivity,R.layout.item_sp_types,provinces));
				theActivity.dialog.dismiss();
			}else if(msg.what == 2){
				theActivity.removeOverlay();
				theActivity.sp_city.setAdapter(new ArrayAdapter<String>(theActivity,R.layout.item_sp_types,cities));
				theActivity.dialog.dismiss();
				if(msg.obj!=null){					
					Double lat = Double.parseDouble(msg.obj.toString().split(",")[0]);
					Double lng = Double.parseDouble(msg.obj.toString().split(",")[1]);
					GeoPoint point = new GeoPoint((int)(lng*1E6),(int)(lat*1E6));
					theActivity.mapController.setZoom(msg.arg1);
					theActivity.mapController.setCenter(point);
				}
			}else if(msg.what==3){
				theActivity.removeOverlay();
				theActivity.sp_area.setAdapter(new ArrayAdapter<String>(theActivity,R.layout.item_sp_types,areas));					
				theActivity.dialog.dismiss();
				if(msg.obj!=null){
					Double lat = Double.parseDouble(msg.obj.toString().split(",")[0]);
					Double lng = Double.parseDouble(msg.obj.toString().split(",")[1]);
					GeoPoint point = new GeoPoint((int)(lng*1E6),(int)(lat*1E6));
					theActivity.mapController.setZoom(msg.arg1);
					theActivity.mapController.setCenter(point);
				}
			}else if(msg.what==4){
					theActivity.removeOverlay();
				if(msg.obj!=null){
					Double lat = Double.parseDouble(msg.obj.toString().split(",")[0]);
					Double lng = Double.parseDouble(msg.obj.toString().split(",")[1]);
					GeoPoint point = new GeoPoint((int)(lng*1E6),(int)(lat*1E6));
					theActivity.mapController.setZoom(msg.arg1);
					theActivity.mapController.setCenter(point);
					theActivity.initOverlay();
				}
			}
		}


	}

	class MyOverlay extends ItemizedOverlay<OverlayItem>{

		public MyOverlay(Drawable arg0, MapView arg1) {
			super(arg0, arg1);
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean onTap(GeoPoint arg0, MapView arg1) {
			// TODO Auto-generated method stub
			if(pop != null){
				pop.hidePop();
			}

			return false;
		}

		@Override
		protected boolean onTap(int arg0) {
			// TODO Auto-generated method stub
			OverlayItem item = getItem(arg0);
			popupText.setText(item.getTitle());
			it = new Intent(MapActivity.this,EquipInfoActivity.class);
			it.putExtra("unitAddress", item.getTitle());
			Bitmap[] bitMaps={		
					BMapUtils.getBitmapFromView(popupInfo) 			
			};
			pop.showPopup(bitMaps, item.getPoint(), 32);

			return true;
		}
	}

	class AreaThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			List<String> list = null;
			HashMap<String,String> params = new HashMap<String,String>();
			if(sp_area.getSelectedItem()!=areas[0]){
				params.put("province", sp_province.getSelectedItem().toString());
				params.put("city", sp_city.getSelectedItem().toString());
				params.put("area", sp_area.getSelectedItem().toString());
				Message msg = Message.obtain();
				responseMsg = CasClient.getInstance().doPost(UrlStrings.GET_LAT_LNG_BY_AREA, params);
				String message = CasClient.getInstance().doPost(UrlStrings.GET_AREA_INFO, params);
				try {
					msg.obj = JsonUtils.getLatLng(responseMsg);
					list = JsonUtils.getCompanyPosition(message);
					CompanyPositions = list.toArray(new String[list.size()]);
					list = JsonUtils.getUnitAddress(message);
					unitAddresses = list.toArray(new String[list.size()]);
					msg.what = 4;
					msg.arg1 = ZOOM_LEVEL_AREA;
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}

	}
	
	
	class GetProvinceThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			List<String> list = null;
			HashMap<String,String> params = new HashMap<String,String>();
			params.put("list","得到权限内所有省份");
			String message = CasClient.getInstance().doPost(UrlStrings.GET_PROVINCE_LIST, params);
			Message msg = Message.obtain();
			try {
				list = JsonUtils.initProvince(message);
				provinces = list.toArray(new String[list.size()]);
				msg.what=1;				
				handler.sendMessage(msg);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	class GetCityThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			List<String> list = null;
			HashMap<String,String> params = new HashMap<String,String>();
			params.put("province",sp_province.getSelectedItem().toString());
			Message msg = Message.obtain();
			String message = CasClient.getInstance().doPost(UrlStrings.GET_CITY_BY_PROVINCE, params);
			if(sp_province.getSelectedItem().toString()!=provinces[0]){
				String message2 = CasClient.getInstance().doPost(UrlStrings.GET_LAT_LNG_BY_PROVINCE, params);
				try {
					String latlng = JsonUtils.getLatLng(message2);
					msg.obj = latlng;
				} catch (Exception e) {
					// TODO Auto-generated catch block
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
				// TODO: handle exception
				e.printStackTrace();
			}
		}

	}

	class GetAreaThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			List<String> list = null;
			HashMap<String,String> params = new HashMap<String,String>();
			params.put("province",sp_province.getSelectedItem().toString());
			params.put("city", sp_city.getSelectedItem().toString());
			Message msg = Message.obtain();
			String message = CasClient.getInstance().doPost(UrlStrings.GET_AREA_BY_CITY, params);
			if(sp_city.getSelectedItem().toString()!=cities[0]){
				String message2 = CasClient.getInstance().doPost(UrlStrings.GET_LAT_LNG_BY_CITY, params);
				try {
					String latlng = JsonUtils.getLatLng(message2);
					msg.obj = latlng;
				} catch (Exception e) {
					// TODO: handle exception
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
				// TODO: handle exception
				e.printStackTrace();
			}
		}

	}
}

