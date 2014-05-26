package org.whut.platform;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.whut.application.MyApplication;
import org.whut.client.CasClient;
import org.whut.strings.UrlStrings;
import org.whut.utils.JsonUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CityRankActivity extends Activity {

	private ListView listView;
	private MyHandler myHandler;
	private TextView textView;
	private ImageButton ibtn_home;
	private ImageButton ibtn_list;
	private ImageButton ibtn_risk;
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cityrank);
		listView=(ListView)findViewById(R.id.list_serach_rank);
		textView=(TextView)findViewById(R.id.tv_province);
		
		
		final String province=getIntent().getExtras().getString("province");
		textView.setText(province+"省风险排名");
		listView.setOnItemClickListener(new ListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ListView list=(ListView)parent;
				@SuppressWarnings("unchecked")
				HashMap<String, String> map=(HashMap<String, String>) list.getItemAtPosition(position);
				 String city=map.get("city");
				 Intent intent=new Intent(CityRankActivity.this,AreaRankActivity.class);
				 intent.putExtra("province", province);
				 intent.putExtra("city", city);
				 startActivity(intent);
				
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
				}else if(event.getAction() == MotionEvent.ACTION_UP){     
					//改为抬起时的图片     
					((ImageButton)v).setImageResource(R.drawable.actionbar_map); 
					startActivity(new Intent(CityRankActivity.this,MapActivity.class));
					finish();
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
				}else if(event.getAction() == MotionEvent.ACTION_UP){     
					//改为抬起时的图片     
					((ImageButton)v).setImageResource(R.drawable.actionbar_risk);     
				}
				return true;
			}
		});
		
		MyApplication.getInstance().addActivity(this);
		
		new Thread(new cityThread()).start();
		myHandler=new MyHandler(this);
	}
	
	static class MyHandler extends Handler{
		WeakReference<CityRankActivity> myActivity;
		MyHandler(CityRankActivity activity)
		{
			myActivity=new WeakReference<CityRankActivity>(activity);
		}
		@Override
		public void handleMessage(Message msg) {
			CityRankActivity theActivity=myActivity.get();
			@SuppressWarnings("unchecked")
			List<Map<String, String>> city=(List<Map<String, String>>) msg.obj;
			SimpleAdapter adapter=new SimpleAdapter(theActivity, city , R.layout.show_list, new String[]{"id","city","avgRiskValue"}, new int[]{R.id.tv_list_id,R.id.tv_list_name,R.id.tv_list_riskvalue});
			theActivity.listView.setAdapter(adapter);
		}
		
	}
	
	class cityThread implements Runnable{

		@Override
		public void run() {
			Message msg=Message.obtain();
			HashMap<String, String>map=new HashMap<String, String>();
			List<Map<String, String>> list=null;
			map.put("province",getIntent().getExtras().getString("province"));

			try {
				String message=CasClient.getInstance().doPost(UrlStrings.GET_CITY_RISK,map);
				Log.v("city", message);
				list=JsonUtils.getCityRisk(message);
				msg.obj=list;
				myHandler.sendMessage(msg);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
