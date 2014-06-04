package org.whut.platform;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import org.whut.application.MyApplication;
import org.whut.client.CasClient;
import org.whut.strings.UrlStrings;
import org.whut.utils.JsonUtils;

import android.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class moreSerachActivity extends Activity{

	private receiveDatahandle datahandle;
	private static List<String> provinces;
	private static List<String> equipmentvarietys;
	public static List<String> cities;
	public static List<String> areas;

	private static Spinner sp_provinces;
	private static Spinner sp_cities;
	private static Spinner sp_areas;
	private Spinner sp_equipmentvariety;
	protected Spinner sp_riskvalue;
	protected Spinner sp_useyear;

	private ProgressDialog dialog;



	static class receiveDatahandle extends Handler
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
				moreSerachActivity.sp_provinces.setAdapter(new ArrayAdapter<String>(mActivity, R.layout.simple_list_item_1,provinces));
				for(int i=0;i<provinces.size();i++){
					if(provinces.get(i)==mActivity.getIntent().getExtras().getString("provinceName")){
						sp_provinces.setSelection(i);
					}
				}
			}
			else if (msg.what==2) {
				mActivity.sp_equipmentvariety.setAdapter(new ArrayAdapter<String>(mActivity, R.layout.simple_list_item_1,equipmentvarietys));
				mActivity.dialog.dismiss();
			}
			else if (msg.what==3) {
				moreSerachActivity.sp_cities.setAdapter(new ArrayAdapter<String>(mActivity, R.layout.simple_list_item_1,cities));
				for(int i=0;i<cities.size();i++){
					if(cities.get(i)==mActivity.getIntent().getExtras().getString("cityName")){
						sp_cities.setSelection(i);
					}
				}

				mActivity.dialog.dismiss();
			}
			else if (msg.what==4) {
				moreSerachActivity.sp_areas.setAdapter(new ArrayAdapter<String>(mActivity, R.layout.simple_list_item_1,areas));
				for(int i=0;i<areas.size();i++){
					if(areas.get(i)==mActivity.getIntent().getExtras().getString("areaName")){
						sp_areas.setSelection(i);
					}
				}

				mActivity.dialog.dismiss();
			}
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(org.whut.platform.R.layout.activity_moreserach);

		sp_provinces=(Spinner)findViewById(org.whut.platform.R.id.sp_province);
		sp_cities=(Spinner)findViewById(org.whut.platform.R.id.sp_city);
		sp_areas=(Spinner)findViewById(org.whut.platform.R.id.sp_area);
		sp_equipmentvariety=(Spinner)findViewById(org.whut.platform.R.id.sp_equipmentvareity);
		sp_riskvalue=(Spinner)findViewById(org.whut.platform.R.id.sp_riskvalue);
		sp_useyear=(Spinner)findViewById(org.whut.platform.R.id.sp_useyear);

		sp_provinces.setAdapter(ArrayAdapter.createFromResource(this, org.whut.platform.R.array.defaultProvinceSpinnerData, R.layout.simple_list_item_1));
		sp_cities.setAdapter(ArrayAdapter.createFromResource(this, org.whut.platform.R.array.defaultCitiesSpinnerData, R.layout.simple_list_item_1));
		sp_areas.setAdapter(ArrayAdapter.createFromResource(this, org.whut.platform.R.array.defaultAreasSpinnerData, R.layout.simple_list_item_1));
		sp_equipmentvariety.setAdapter(ArrayAdapter.createFromResource(this, org.whut.platform.R.array.defaultDevicesSpinnerData, R.layout.simple_list_item_1));
		sp_useyear.setAdapter(ArrayAdapter.createFromResource(this, org.whut.platform.R.array.useyearSpinnerData, R.layout.simple_list_item_1));
		sp_riskvalue.setAdapter(ArrayAdapter.createFromResource(this, org.whut.platform.R.array.riskvalueSpinnerData, R.layout.simple_list_item_1));

		datahandle=new receiveDatahandle(this);
		MyApplication.getInstance().addActivity(this);
		
		((Button)findViewById(org.whut.platform.R.id.btn_serach)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				String province=sp_provinces.getSelectedItem().toString().equals("请选择省份")?"":sp_provinces.getSelectedItem().toString();
//				String city=sp_cities.getSelectedItem().toString().equals("请选择城市")?"":sp_cities.getSelectedItem().toString();
//				String area=sp_areas.getSelectedItem().toString().equals("请选择地区")?"":sp_areas.getSelectedItem().toString();
//				String equipment=(sp_equipmentvariety.getSelectedItem().toString()).equals("设备类型")?"0":sp_equipmentvariety.getSelectedItem().toString();
//				String riskvalue=(sp_riskvalue.getSelectedItem().toString()).equals("风险区间")?"1;9":sp_riskvalue.getSelectedItem().toString().replace("-", ";");
//				String useyear=(sp_useyear.getSelectedItem().toString()).equals("使用年限")?"0":sp_useyear.getSelectedItem().toString().replace("-", ";");
				Intent intent=new Intent();

				if (sp_provinces.getSelectedItem().toString()!=provinces.get(0)) {
					intent.setClass(moreSerachActivity.this, CityRankActivity.class);
					intent.putExtra("province", sp_provinces.getSelectedItem().toString());
					if (sp_cities.getSelectedItem().toString()!=cities.get(0)) {
						intent.setClass(moreSerachActivity.this, AreaRankActivity.class);
						intent.putExtra("city", sp_cities.getSelectedItem().toString());
						if (sp_areas.getSelectedItem().toString()!=areas.get(0)) {
							intent.setClass(moreSerachActivity.this, PlaceRankActivity.class);
							intent.putExtra("area", sp_areas.getSelectedItem().toString());
						}
					}
					startActivity(intent);
				}else {
					intent.setClass(moreSerachActivity.this, ProvinceRankActivity.class);
					intent.putExtra("province", getIntent().getExtras().getString("provinceName"));
					startActivity(intent);
				}
			}
		});

		((ImageView)findViewById(org.whut.platform.R.id.iv_topbar_left_back)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				moreSerachActivity.this.finish();
			}
		});
		
		TextView textView_select = (TextView) findViewById(org.whut.platform.R.id.tv_topbar_middle_detail);
		textView_select.setText("条件筛选");
		
		RelativeLayout rl = (RelativeLayout) findViewById(org.whut.platform.R.id.tv_topbar_right_map_layout);
		rl.setVisibility(View.INVISIBLE);
		rl.setFocusable(false);
		
		initdata();
		setspinner();

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
		public void run() {
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
		public void run() {
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
		public void run() {
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
		public void run() {
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
