package org.whut.platform;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.whut.client.CasClient;
import org.whut.strings.UrlStrings;
import org.whut.utils.JsonUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;




public class DistributeActivity extends Activity{

	private ImageView iv_topbar_left_back;
	private TextView tv_topbar_middle_detail;
	private RelativeLayout tv_topbar_right_map_layout;
	
	private TextView risk_level;
	
	private List<String> areas;
	
	private String province;
	private String city;
	private String area;

	
	private List<Map<String,String>> data;
	private List<String> risk_Level;
	
	private MyHandler handler;
	private ListView listView;
	private SimpleAdapter adapter;
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_distribute);
	
		province = getIntent().getExtras().getString("province");
		
		city = getIntent().getExtras().getString("city");
		
		area = getIntent().getExtras().getString("area");
		
		handler = new MyHandler(this);
		
		listView = (ListView) findViewById(R.id.listView_distribute);
		
		risk_Level = new ArrayList<String>();	
		risk_Level.add("风险等级 1 : 风险值（0-10）");
		risk_Level.add("风险等级 2 : 风险值（11-20）");
		risk_Level.add("风险等级 3 : 风险值（21-30）");
		risk_Level.add("风险等级 4 : 风险值（31-40）");
		risk_Level.add("风险等级 5 : 风险值（41-50）");
		risk_Level.add("风险等级 6 : 风险值（51-60）");
		risk_Level.add("风险等级 7 : 风险值（61-70）");
		risk_Level.add("风险等级 8 : 风险值（71-80）");
		risk_Level.add("风险等级 9 : 风险值（81-90）");
		risk_Level.add("风险等级 10 : 风险值（91-100）");
		
		new Thread(new AreaThread()).start();
		new Thread(new GetAreasThread()).start();
		
		iv_topbar_left_back = (ImageView) findViewById(R.id.iv_topbar_left_back);
		iv_topbar_left_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		tv_topbar_middle_detail = (TextView) findViewById(R.id.tv_topbar_middle_detail);

		tv_topbar_middle_detail.setText(area+"风险分布");

		tv_topbar_right_map_layout = (RelativeLayout) findViewById(R.id.tv_topbar_right_map_layout);
		tv_topbar_right_map_layout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Builder alertDialog = new AlertDialog.Builder(DistributeActivity.this);
				alertDialog.setTitle("切换地区");
				alertDialog.setItems(areas.toArray(new String[areas.size()]), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						area = areas.get(which);
						new Thread(new AreaThread()).start();
					}
				}).show();
			}
		});
		
		risk_level = (TextView) findViewById(R.id.risk_level);
		risk_level.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Builder alertDialog = new AlertDialog.Builder(DistributeActivity.this);
				alertDialog.setAdapter(new ArrayAdapter<String>(DistributeActivity.this, R.layout.common_dialogitem_risk_level, risk_Level){

					@Override
					public View getView(int position, View convertView,
							ViewGroup parent) {
						// TODO Auto-generated method stub
						convertView = super.getView(position, convertView, parent);
						TextView v = (TextView)convertView.findViewById(R.id.textView_one);
						switch(position){
						case 0:
							v.setTextColor(Color.parseColor("#ff737373"));
							break;
						case 1:
							v.setTextColor(Color.parseColor("#00FFFF"));
							break;
						case 2:
							v.setTextColor(Color.parseColor("#00FF7F"));
							break;
						case 3:
							v.setTextColor(Color.parseColor("#20B2AA"));
							break;
						case 4:
							v.setTextColor(Color.parseColor("#32CD32"));
							break;
						case 5:
							v.setTextColor(Color.parseColor("#DAA520"));
							break;
						case 6:
							v.setTextColor(Color.parseColor("#FFA500"));
							break;
						case 7:
							v.setTextColor(Color.parseColor("#FF4500"));
							break;
						case 8:
							v.setTextColor(Color.parseColor("#FF1493"));
							break;
						case 9:
							v.setTextColor(Color.RED);
							break;
					}
						
						return convertView;
					}
					
					
					
					
				}, null).setTitle("风险等级说明")
				.setNegativeButton("确定", null).show();
			}
		});
		
	}
	
	
	static class MyHandler extends Handler{
		
		WeakReference<DistributeActivity> myActivity;
	
		MyHandler(DistributeActivity activity){
			myActivity=new WeakReference<DistributeActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			DistributeActivity theActivity = myActivity.get();
			theActivity.adapter = new SimpleAdapter(theActivity, theActivity.data, R.layout.common_listview_distribute, new String[]{"riskLevel","craneNumbers","scale"}, new int[]{R.id.risk_level,R.id.craneNumber,R.id.scale}){

				@Override
				public View getView(int position, View convertView,
						ViewGroup parent) {
					// TODO Auto-generated method stub
					convertView = super.getView(position, convertView, parent);
				
					TextView v = (TextView) convertView.findViewById(R.id.risk_level);
					switch(position){
						case 0:
							v.setTextColor(Color.parseColor("#ff737373"));
							break;
						case 1:
							v.setTextColor(Color.parseColor("#00FFFF"));
							break;
						case 2:
							v.setTextColor(Color.parseColor("#00FF7F"));
							break;
						case 3:
							v.setTextColor(Color.parseColor("#20B2AA"));
							break;
						case 4:
							v.setTextColor(Color.parseColor("#32CD32"));
							break;
						case 5:
							v.setTextColor(Color.parseColor("#DAA520"));
							break;
						case 6:
							v.setTextColor(Color.parseColor("#FFA500"));
							break;
						case 7:
							v.setTextColor(Color.parseColor("#FF4500"));
							break;
						case 8:
							v.setTextColor(Color.parseColor("#FF1493"));
							break;
						case 9:
							v.setTextColor(Color.RED);
							break;
					}
					
					
					return convertView;
				
				}
				
			};
			theActivity.listView.setAdapter(theActivity.adapter);
			theActivity.adapter.notifyDataSetChanged();
			theActivity.tv_topbar_middle_detail.setText(theActivity.area+"风险分布");
		}	
	}	
	
	class AreaThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("province", province);
			params.put("city", city);
			params.put("area", area);
			Message msg = Message.obtain();
			try {
				String message = CasClient.getInstance().doPost(UrlStrings.GET_AREA_INFO, params);
				data = JsonUtils.GetDistributeData(message);
				msg.what = 1;
				handler.sendMessage(msg);
			}catch(Exception e){
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	
	}
	
	class GetAreasThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("province", province);
			params.put("city", city);
			Message msg = Message.obtain();
			try {
				msg.what=2;
				areas=JsonUtils.initArea(CasClient.getInstance().doPost(UrlStrings.GET_AREA_BY_CITY, params));
				handler.sendMessage(msg);	
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

	}
}
