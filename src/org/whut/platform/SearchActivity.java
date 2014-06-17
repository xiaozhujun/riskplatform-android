package org.whut.platform;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

import org.whut.application.MyApplication;
import org.whut.client.CasClient;
import org.whut.strings.UrlStrings;
import org.whut.utils.JsonUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.View;

import android.widget.ImageView;


import android.widget.RelativeLayout;
import android.widget.TextView;


public class SearchActivity extends Activity{

	private RelativeLayout tv_topbar_right_map_layout;
	private TextView tv_topbar_middle_detail;
	private ImageView iv_topbar_left_back;

	private RelativeLayout province_choose;
	private RelativeLayout city_choose;
	private RelativeLayout area_choose;
	private RelativeLayout equip_choose;
	private RelativeLayout age_choose;
	private RelativeLayout range_choose;

	private static TextView province_value;
	private static TextView city_value;
	private static TextView area_value;
	private static TextView equip_value;
	private static TextView age_value;
	private static TextView range_value;

	private ProgressDialog dialog;
	private MyHandler handler;

	private static List<String> provinceList;
	private static List<String> cityList;
	private static List<String> areaList;

	private static String equipmentVarieties[];
	private static String useTimes[];
	private static String rangeValues[];

	private static int province_choice = 0;
	private static int city_choice = 0;

	private int user_id;
	
	private TextView search_start;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_search);

		tv_topbar_right_map_layout = (RelativeLayout) findViewById(R.id.tv_topbar_right_map_layout);
		tv_topbar_right_map_layout.setVisibility(View.INVISIBLE);
		tv_topbar_middle_detail = (TextView) findViewById(R.id.tv_topbar_middle_detail);
		tv_topbar_middle_detail.setText("条件搜索");
		iv_topbar_left_back = (ImageView) findViewById(R.id.iv_topbar_left_back);
		iv_topbar_left_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		province_choose = (RelativeLayout) findViewById(R.id.province_choose);
		city_choose = (RelativeLayout) findViewById(R.id.city_choose);
		area_choose = (RelativeLayout) findViewById(R.id.area_choose);
		equip_choose = (RelativeLayout) findViewById(R.id.equip_choose);
		age_choose = (RelativeLayout) findViewById(R.id.age_choose);
		range_choose = (RelativeLayout) findViewById(R.id.range_choose);

		province_value = (TextView) findViewById(R.id.province_value);
		city_value = (TextView) findViewById(R.id.city_value);
		area_value = (TextView) findViewById(R.id.area_value);
		equip_value = (TextView) findViewById(R.id.equip_value);
		age_value = (TextView) findViewById(R.id.age_value);
		range_value = (TextView) findViewById(R.id.range_value);
		
		search_start = (TextView) findViewById(R.id.go_to_search);
		

		handler = new MyHandler(this);
		MyApplication.getInstance().addActivity(this);
		initdata();

		equip_choose.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Builder alertDialog = new AlertDialog.Builder(SearchActivity.this);
				alertDialog.setTitle("设备类型");
				alertDialog.setItems(equipmentVarieties, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if(which!=0){
							equip_value.setText(equipmentVarieties[which]);
						}else{
							equip_value.setText("");
						}
					}
				}).show();
			}
		});

		age_choose.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Builder alertDialog = new AlertDialog.Builder(SearchActivity.this);
				alertDialog.setTitle("使用年限");
				alertDialog.setItems(useTimes, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if(which!=0){
							age_value.setText(useTimes[which]);
						}else{
							age_value.setText("");
						}
					}
				}).show();
			}
		});

		range_choose.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Builder alertDialog = new AlertDialog.Builder(SearchActivity.this);
				alertDialog.setTitle("风险区间");
				alertDialog.setItems(rangeValues, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if(which!=0){
							range_value.setText(rangeValues[which]);
						}else{
							range_value.setText("");
						}
					}
				}).show();

			}
		});
		
		
		search_start.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(equip_value.getText()==""&&age_value.getText()==""&&range_value.getText()==""){
					if(province_value.getText()==""){
						Intent it = new Intent(SearchActivity.this,ProvinceRankActivity.class);
						it.putExtra("user_id",user_id);
						startActivity(it);
					}else{
						if(city_value.getText()==""){
							Intent it = new Intent(SearchActivity.this,CityRankActivity.class);
							it.putExtra("province", province_value.getText().toString());
							it.putExtra("user_id",user_id);
							startActivity(it);
						}else{
							if(area_value.getText()==""){
								Intent it = new Intent(SearchActivity.this,AreaRankActivity.class);
								it.putExtra("province", province_value.getText().toString());
								it.putExtra("city",city_value.getText().toString());
								it.putExtra("user_id",user_id);
								startActivity(it);
							}else{
								Intent it = new Intent(SearchActivity.this,PlaceRankActivity.class);
								it.putExtra("province", province_value.getText().toString());
								it.putExtra("city",city_value.getText().toString());
								it.putExtra("area",area_value.getText().toString());
								it.putExtra("user_id",user_id);
								startActivity(it);
							}
						}
					}
				}else{
					Intent it = new Intent(SearchActivity.this,ResultActivity.class);
					it.putExtra("province", province_value.getText().toString());
					it.putExtra("city",city_value.getText().toString());
					it.putExtra("area",area_value.getText().toString());
					it.putExtra("equipmentVariety",equip_value.getText().toString());
					if(age_value.getText()!=""){
						it.putExtra("useTime", age_value.getText().toString().replace("-", ";"));
					}else{
						it.putExtra("useTime", "0");
					}
					
					if(range_value.getText()!=""){
						it.putExtra("value",range_value.getText().toString().replace("-", ";"));
					}else{
						it.putExtra("value", "0");
					}
						
					it.putExtra("user_id", user_id);
					startActivity(it);
				}
			}
		});
	}

	
	
	private void initdata(){

		dialog = new ProgressDialog(this);
		dialog.setTitle("提示");
		dialog.setMessage("正在获取数据，请稍后...");
		dialog.setCancelable(true);
		dialog.setIndeterminate(false);
		dialog.show();
		
		user_id = getIntent().getExtras().getInt("user_id");
		Log.i("msg", "传来的user_id为"+user_id);

		equipmentVarieties = new String[]{"请选择设备类型","电葫芦桥式起重机","冶金桥式起重机","汽车起重机","履带起重机"
				,"集装箱正面吊运起重机","轮胎起重机","轮胎式起重机","汽车吊","履带式起重机","通用门式起重机","电葫芦门式起重机"
				,"门式起重机","电动门葫芦门式起重机","轨道式集装箱门式起重机","岸边集装箱起重机","岸边式集装箱起重机","半门式起重机"
				,"轮胎式龙门起重机","轮胎式门式起重机","门座起重机","固定式起重机","门座式起重机","港口门座起重机"};

		useTimes = new String[]{"请选择使用年限","0-5","5-10","10-15","15-20","20-25","25-30","30-35","35-40"};


		rangeValues = new String[]{"请选择风险区间","0-3","3-6","6-9"};



		new Thread(new GetProvinceListThread()).start();
		new Thread(new GetCityListThread()).start();
		new Thread(new GetAreaListThread()).start();
	}


	static class MyHandler extends Handler{
		WeakReference<SearchActivity> myActivity;

		MyHandler(SearchActivity activity){
			myActivity=new WeakReference<SearchActivity>(activity);
		}

		@Override
		public void handleMessage(final Message msg) {
			// TODO Auto-generated method stub
			final SearchActivity theActivity=myActivity.get();	
			switch(msg.what){
			case 1:
				theActivity.dialog.dismiss();
				theActivity.province_choose.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Builder alertDialog = new AlertDialog.Builder(theActivity);		    
						alertDialog.setTitle("省份选择");
						alertDialog.setItems(provinceList.toArray(new String[provinceList.size()]), new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								if(province_choice!=which){
									city_value.setText("");
									area_value.setText("");
								}
								province_choice = which;
								if(which!=0){
									province_value.setText(provinceList.get(which));
								}else{
									province_value.setText("");
									city_value.setText("");
									area_value.setText("");
								}
								new Thread(theActivity.new GetCityListThread()).start();
								new Thread(theActivity.new GetAreaListThread()).start();
							}	
						}).show();
					}
				});

				theActivity.city_choose.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Builder alertDialog = new AlertDialog.Builder(theActivity);
						alertDialog.setTitle("城市选择");
						alertDialog.setItems(cityList.toArray(new String[cityList.size()]), new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								if(city_choice!=which){
									area_value.setText("");
								}
								city_choice = which;
								if(which!=0){
									city_value.setText(cityList.get(which));
								}else{
									city_value.setText("");
									area_value.setText("");
								}
								new Thread(theActivity.new GetAreaListThread()).start();
							}
						}).show();
					}
				});

				theActivity.area_choose.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Builder alertDialog = new AlertDialog.Builder(theActivity);
						alertDialog.setTitle("区域选择");
						alertDialog.setItems(areaList.toArray(new String[areaList.size()]), new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								if(which!=0){
									area_value.setText(areaList.get(which));
								}else{
									area_value.setText("");
								}
							}
						}).show();

					}
				});
				break;
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
				provinceList=JsonUtils.initProvince(CasClient.getInstance().doPost(UrlStrings.GET_PROVINCE_LIST, params));			
				handler.sendMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class GetCityListThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			HashMap<String, String> params=new HashMap<String, String>();
			params.put("province",province_value.getText().toString());
			Message msg=Message.obtain();
			try {
				msg.what=2;
				cityList=JsonUtils.initCity(CasClient.getInstance().doPost(UrlStrings.GET_CITY_BY_PROVINCE, params));
				handler.sendMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class GetAreaListThread implements Runnable{

		@Override
		public void run(){
			// TODO Auto-generated method stub
			HashMap<String, String> params=new HashMap<String, String>();
			params.put("province",province_value.getText().toString());
			params.put("city",city_value.getText().toString());
			Message msg=Message.obtain();
			try {
				msg.what=3;
				areaList=JsonUtils.initArea(CasClient.getInstance().doPost(UrlStrings.GET_AREA_BY_CITY, params));
				handler.sendMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
