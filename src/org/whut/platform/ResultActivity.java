package org.whut.platform;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.whut.client.CasClient;
import org.whut.database.entity.Device;



import org.whut.strings.UrlStrings;
import org.whut.utils.JsonUtils;
import org.whut.utils.SQLiteUtils;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;


public class ResultActivity extends Activity{

	private ImageView iv_topbar_left_back;
	private TextView tv_topbar_middle_detail;
	private RelativeLayout tv_topbar_right_map_layout;
	private ListView listview1;
	private static SimpleAdapter adapter;


	private static int user_id;
	private static Integer[] device_ids;
	private static String province;
	private static String city;
	private static String area;
	private static String equipmentVariety;
	private static String useTime;
	private static String value;

	private static List<Map<String,String>> list;


	private static MyHandler handler;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_result);

		tv_topbar_right_map_layout = (RelativeLayout) findViewById(R.id.tv_topbar_right_map_layout);
		tv_topbar_right_map_layout.setVisibility(View.INVISIBLE);
		tv_topbar_middle_detail = (TextView) findViewById(R.id.tv_topbar_middle_detail);
		tv_topbar_middle_detail.setText("搜索结果");
		iv_topbar_left_back = (ImageView) findViewById(R.id.iv_topbar_left_back);
		iv_topbar_left_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		listview1 = (ListView) findViewById(R.id.listview1);

		handler = new MyHandler(this);

		user_id = getIntent().getExtras().getInt("user_id");
		province = getIntent().getExtras().getString("province");
		city = getIntent().getExtras().getString("city");
		area = getIntent().getExtras().getString("area");
		equipmentVariety = getIntent().getExtras().getString("equipmentVariety");
		useTime = getIntent().getExtras().getString("useTime");
		value = getIntent().getExtras().getString("value");


		new Thread(new GetResultThread()).start();

	}


	static class MyHandler extends Handler{
		WeakReference<ResultActivity> mActivity;

		MyHandler(ResultActivity activity){
			mActivity = new WeakReference<ResultActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			final ResultActivity theActivity = mActivity.get();
			if(msg.what==1){
				adapter  = new SimpleAdapter(theActivity,list,R.layout.common_listview_search_result,new String[]{"equipmentVariety","riskValue","useTime","unitAddress"},new int[]{R.id.layout_title,R.id.riskValue,R.id.traffic_violation,R.id.unitAddress}){

					@Override
					public View getView(final int position, View convertView,
							ViewGroup parent) {
						// TODO Auto-generated method stub
						convertView = super.getView(position, convertView, parent);
						final Intent it = new Intent(theActivity,InfoActivity.class);
						it.putExtra("unitAddress", list.get(position).get("unitAddress").split("：")[1]);
						it.putExtra("user_id",user_id);
						it.putExtra("device_id",device_ids[position]);
						it.putExtra("tag", "from_result_to_info");
						RelativeLayout button_detail = (RelativeLayout) convertView.findViewById(R.id.button_route_layout);
						button_detail.setOnClickListener(new OnClickListener() {						
							@Override
							public void onClick(View v) {		
								theActivity.startActivity(it);
							}
						});	
						return convertView;
					}
				};
				theActivity.listview1.setAdapter(adapter);	
			}else if(msg.what==2){
				theActivity.listview1.setVisibility(View.GONE);
				((TextView)theActivity.findViewById(R.id.no_data)).setVisibility(View.VISIBLE);
			}
		}
	}

	class GetResultThread implements Runnable{


		@Override
		public void run() {
			// TODO Auto-generated method stub
			HashMap<String, String> params=new HashMap<String, String>();
			if(province==""){
				params.put("province", null);
			}else{
				params.put("province", province);
			}

			if(city==""){
				params.put("city", null);
			}else{
				params.put("city", city);
			}

			if(area==""){
				params.put("area", null);
			}else{
				params.put("area", area);
			}

			if(equipmentVariety==""){
				params.put("equipmentVariety", null);
			}else{
				params.put("equipmentVariety", equipmentVariety);
			}

			params.put("useTime", useTime);

			params.put("value", value);


			Message msg=Message.obtain();
			try {
				msg.what = 1;
				String message = CasClient.getInstance().doPost(UrlStrings.GetCraneInfoByCondition, params);
				list = JsonUtils.getResultList(message);
				List<Device> list3 = JsonUtils.getEquipInfoDataForDeviceId(message);
				SQLiteUtils.addDevices(ResultActivity.this, list3);
				device_ids = SQLiteUtils.getDeviceIds(ResultActivity.this,list3).toArray(new Integer[list3.size()]);
				handler.sendMessage(msg);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

	}
}
