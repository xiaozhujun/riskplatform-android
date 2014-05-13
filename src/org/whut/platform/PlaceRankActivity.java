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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class PlaceRankActivity extends Activity {

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
		setContentView(R.layout.activity_placerank);
		listView=(ListView)findViewById(R.id.list_rank);
		textView=(TextView)findViewById(R.id.tv_place);
		textView.setText(getIntent().getExtras().getString("area")+"风险排名");
		
		
		
		listView.setOnItemClickListener(new ListView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ListView listView = (ListView)parent;  
			    @SuppressWarnings("unchecked")
			    HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(position);  
			    String unitAddress = map.get("company_name");
			    Intent it = new Intent(PlaceRankActivity.this,EquipInfoActivity.class);
			    it.putExtra("unitAddress", unitAddress);
			    startActivity(it);
				
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
					startActivity(new Intent(PlaceRankActivity.this,MapActivity.class));
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
		
		new Thread(new placeThread()).start();
		myHandler=new MyHandler(this);
	}
	
	static class MyHandler extends Handler{
		WeakReference<PlaceRankActivity> myActivity;
		MyHandler(PlaceRankActivity acitvity)
		{
			myActivity=new WeakReference<PlaceRankActivity>(acitvity);
		}
		@Override
		public void handleMessage(Message msg) {
			@SuppressWarnings("unchecked")
			List<Map<String, String>> place=(List<Map<String, String>>) msg.obj;
			PlaceRankActivity theAcitvity=myActivity.get();
			SimpleAdapter simpleAdapter = new SimpleAdapter(theAcitvity, place, R.layout.show_list, new String[]{"id","company_name","riskValue"}, new int[]{R.id.tv_list_id,R.id.tv_list_name,R.id.tv_list_riskvalue});
			theAcitvity.listView.setAdapter(simpleAdapter);
		}
		
	}
	
	class placeThread implements Runnable{

		@Override
		public void run() {
			Message msg=Message.obtain();
			HashMap<String,String> request_data = new HashMap<String,String>();
			List<Map<String,String>> list = null;
			try{
			request_data.put("province", getIntent().getExtras().getString("province"));
			request_data.put("city", getIntent().getExtras().getString("city"));
			request_data.put("area",getIntent().getExtras().getString("area"));
			String message = CasClient.getInstance().doPost(UrlStrings.SHOW_RANK, request_data);			
			list = JsonUtils.getCompanyRankList(message);
			msg.obj=list;
			myHandler.sendMessage(msg);
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
	}
	

}
