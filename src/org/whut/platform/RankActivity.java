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
import android.content.Context;
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
import android.widget.Toast;




public class RankActivity extends Activity {

	private ListView listView;
	private MyHandler myHandler;
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
		setContentView(R.layout.activity_rank);	
		listView=(ListView)findViewById(R.id.list_serach_rank);
		

		
			listView.setOnItemClickListener(new ListView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					ListView list=(ListView)parent;
					@SuppressWarnings("unchecked")
					HashMap<String, String> map=(HashMap<String, String>) list.getItemAtPosition(position);
					 String province=map.get("province");
					 Intent intent=new Intent(RankActivity.this,CityRankActivity.class);
					 intent.putExtra("province", province);
					 startActivity(intent);
					
				}
			});
			//new provinceTask().execute();
			new Thread(new provinceThread()).start();
			myHandler=new MyHandler(this);
			
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
						startActivity(new Intent(RankActivity.this,MapActivity.class));
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

	}
	
	static class MyHandler extends Handler{
		WeakReference<RankActivity> myActivity;
		MyHandler(RankActivity activity)
		{
			myActivity=new WeakReference<RankActivity>(activity);
		}
		@Override
		public void handleMessage(Message msg) {
			RankActivity theActivity=myActivity.get();
			@SuppressWarnings("unchecked")
			List<Map<String, String>> province=(List<Map<String, String>>) msg.obj;
			SimpleAdapter adapter=new SimpleAdapter(theActivity,  province, R.layout.show_list, new 

					String[]{"id","avgRiskValue","province"}, new int[]{R.id.tv_list_id,R.id.tv_list_name,R.id.tv_list_riskvalue});
					theActivity.listView.setAdapter(adapter);
		}
		
	}
	
	class provinceThread implements Runnable{

		@Override
		public void run() {
			 Message msg=Message.obtain();
			 HashMap<String, String> map=new HashMap<String, String>();
			try{
				
				String message = CasClient.getInstance().doPost(UrlStrings.GET_PROVINCE_RISK,map);	
				Log.v("province", message);
				List<Map<String,String>> list = JsonUtils.getProvinceRisk(message);
				msg.obj=list;
				myHandler.sendMessage(msg);
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
	}
	
}
