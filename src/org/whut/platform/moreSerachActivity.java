package org.whut.platform;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.whut.client.CasClient;
import org.whut.strings.UrlStrings;
import org.whut.utils.JsonUtils;

import android.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class moreSerachActivity extends Activity{
	
	private receiveDatahandle datahandle;
	private List<String> provinces;
	private List<String> equipmentvarietys;
	public List<String> cities;
	public List<String> areas;
	
	private Spinner sp_provinces;
	private Spinner sp_equipmentvariety;
	private Spinner sp_cities;
	private Spinner sp_areas;
	protected Spinner sp_riskvalue;
	protected Spinner sp_useyear;
	
	private ProgressDialog dialog;
	
	
	
	class receiveDatahandle extends Handler
	{
		WeakReference<moreSerachActivity> theactivity;
		public receiveDatahandle(moreSerachActivity moreserachActivity) {
			theactivity=new WeakReference<moreSerachActivity>(moreserachActivity);
		}
		
		@Override
		public void handleMessage(Message msg)
		{
			moreSerachActivity mActivity=theactivity.get();
			if (msg.what==1) {
				mActivity.sp_provinces.setAdapter(new ArrayAdapter<String>(mActivity, R.layout.simple_list_item_1,provinces));
			}
			else if (msg.what==2) {
				mActivity.sp_equipmentvariety.setAdapter(new ArrayAdapter<String>(mActivity, R.layout.simple_list_item_1,equipmentvarietys));
				mActivity.dialog.dismiss();
			}
			else if (msg.what==3) {
				mActivity.sp_cities.setAdapter(new ArrayAdapter<String>(mActivity, R.layout.simple_list_item_1,cities));
				mActivity.dialog.dismiss();
			}
			else if (msg.what==4) {
				mActivity.sp_areas.setAdapter(new ArrayAdapter<String>(mActivity, R.layout.simple_list_item_1,areas));
				mActivity.dialog.dismiss();
			}
		}
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(org.whut.platform.R.layout.activity_moreserach);

		sp_provinces=(Spinner)findViewById(org.whut.platform.R.id.sp_province);
		sp_equipmentvariety=(Spinner)findViewById(org.whut.platform.R.id.sp_equipmentvareity);
		sp_cities=(Spinner)findViewById(org.whut.platform.R.id.sp_city);
		sp_areas=(Spinner)findViewById(org.whut.platform.R.id.sp_area);
		sp_riskvalue=(Spinner)findViewById(org.whut.platform.R.id.sp_riskvalue);
		sp_useyear=(Spinner)findViewById(org.whut.platform.R.id.sp_useyear);
		
		sp_useyear.setAdapter(ArrayAdapter.createFromResource(this, org.whut.platform.R.array.useyearSpinnerData, R.layout.simple_list_item_1));
		sp_riskvalue.setAdapter(ArrayAdapter.createFromResource(this, org.whut.platform.R.array.riskvalueSpinnerData, R.layout.simple_list_item_1));
		
		datahandle=new receiveDatahandle(this);
		
		initdata();
		setspinner();
		((Button)findViewById(org.whut.platform.R.id.btn_serach)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String province=sp_provinces.getSelectedItem().toString().equals("请选择省份")?"":sp_provinces.getSelectedItem().toString();
				String city=sp_cities.getSelectedItem().toString().equals("请选择城市")?"":sp_cities.getSelectedItem().toString();
				String area=sp_areas.getSelectedItem().toString().equals("请选择地区")?"":sp_areas.getSelectedItem().toString();
				String equipment=(sp_equipmentvariety.getSelectedItem().toString()).equals("设备类型")?"0":sp_equipmentvariety.getSelectedItem().toString();
				String riskvalue=(sp_riskvalue.getSelectedItem().toString()).equals("风险区间")?"1;9":sp_riskvalue.getSelectedItem().toString().replace("-", ";");
				String useyear=(sp_useyear.getSelectedItem().toString()).equals("使用年限")?"0":sp_useyear.getSelectedItem().toString().replace("-", ";");
				
				
				Intent intent=new Intent();
				
				if (sp_provinces.getSelectedItem().toString()!=provinces.get(0)) {
					intent.setClass(moreSerachActivity.this, moreSerachResultActivity.class);
					intent.putExtra("province", sp_provinces.getSelectedItem().toString());
					if (sp_cities.getSelectedItem().toString()!=cities.get(0)) {
						intent.setClass(moreSerachActivity.this, AreaRankActivity.class);
						intent.putExtra("city", sp_cities.getSelectedItem().toString());
						if (sp_areas.getSelectedItem().toString()!=areas.get(0)) {
							intent.setClass(moreSerachActivity.this, PlaceRankActivity.class);
							intent.putExtra("area", sp_areas.getSelectedItem().toString());
						}

					}
				}else {
					intent.setClass(moreSerachActivity.this, moreSerachResultActivity.class);
				}
				intent.putExtra("equipmentVariety", equipment);
				intent.putExtra("useTime", useyear);
				intent.putExtra("value", riskvalue);
				startActivity(intent);
			}
		});
		((ImageButton)findViewById(org.whut.platform.R.id.ibtn_back)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				moreSerachActivity.this.finish();
			}
		});
	}


	private void setspinner() {
		
		
		
		sp_provinces.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				new Thread(new GetCityListThread()).start();
				if (position!=0) {
					
					dialog.show();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
		sp_cities.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				new Thread(new GetAreaListThread()).start();
				if (position!=0) {
					
					dialog.show();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}


	private void initdata() {
		
		dialog = new ProgressDialog(this);
		dialog.setTitle("提示");
		dialog.setMessage("正在获取数据，请稍后...");
		dialog.setCancelable(true);
		dialog.setIndeterminate(false);
		dialog.show();
		

		new Thread(new GetProvinceListThread()).start();
		new Thread(new GetEquipmentThread()).start();
		
		
		
	}


	class GetAreaListThread implements Runnable
	{
		
		@Override
		synchronized public void run() {
			// TODO Auto-generated method stub
			HashMap<String, String> params=new HashMap<String, String>();
			params.put("province",sp_provinces.getSelectedItem().toString());
			params.put("city", sp_cities.getSelectedItem().toString());
			Message msg=Message.obtain();
			try {
				msg.what=4;
				areas=JsonUtils.initArea(CasClient.getInstance().doPost(UrlStrings.GET_AREA_BY_CITY, params));
				datahandle.sendMessage(msg);
			} catch (Exception e) {
			}
		}
	}
	
	class GetCityListThread implements Runnable
	{
		
		@Override
		synchronized public void run() {
			// TODO Auto-generated method stub
			HashMap<String, String> params=new HashMap<String, String>();
			params.put("province",sp_provinces.getSelectedItem().toString());
			Message msg=Message.obtain();
			try {
				msg.what=3;
				cities=JsonUtils.initCity(CasClient.getInstance().doPost(UrlStrings.GET_CITY_BY_PROVINCE, params));
				datahandle.sendMessage(msg);
			} catch (Exception e) {
			}
		}
	}
	
	class GetProvinceListThread implements Runnable
	{

		@Override
		synchronized public void run() {
			// TODO Auto-generated method stub
			HashMap<String, String> params=new HashMap<String, String>();
			params.put("list","得到权限内所有省份");
			Message msg=Message.obtain();
			try {
				msg.what=1;
				provinces=JsonUtils.initProvince(CasClient.getInstance().doPost(UrlStrings.GET_PROVINCE_LIST, params));
				datahandle.sendMessage(msg);
			} catch (Exception e) {
			}
		}
	}
	
	class GetEquipmentThread implements Runnable
	{
		@Override
		synchronized public void run() {
			// TODO Auto-generated method stub
			Message msg=Message.obtain();
			try {
				msg.what=2;
				equipmentvarietys=JsonUtils.getEquipmentVarietyList(CasClient.getInstance().doPostNoParams(UrlStrings.GET_getEquipmentVarietyList));
				datahandle.sendMessage(msg);
			} catch (Exception e) {
			}
		}
	}
}
