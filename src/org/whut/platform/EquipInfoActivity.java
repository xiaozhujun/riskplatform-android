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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class EquipInfoActivity extends Activity{

	private TextView tv;
	private Spinner sp1;
	private Spinner sp2;
	private ListView listView;
	private MyHandler handler;
	private String[] riskValues = {"风险值","6","5","4","3","2","1"};
	private String[] equipmentVarieties = {"设备类型","冶金桥式起重机","通用门式起重机","电动葫芦门式起重机","门式起重机","轨道式集装箱门式起重机","轮胎式门式起重机"};
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
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_equip_info);


		tv = (TextView) findViewById(R.id.textview1);


		sp1 = (Spinner) findViewById(R.id.spinner01);
		sp2 = (Spinner) findViewById(R.id.spinner02);
		
		sp1.setAdapter(new ArrayAdapter<String>(getApplicationContext(),R.layout.item_sp_types,riskValues));
		sp2.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.item_sp_types, equipmentVarieties));
		
		listView = (ListView) findViewById(R.id.listview1);
		
		handler = new MyHandler(this);
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				    String unitAddress = tv.getText().toString();
				    Intent it = new Intent(EquipInfoActivity.this,InfoActivity.class);
				    it.putExtra("unitAddress", unitAddress);
				    startActivity(it);
			}
		
		
		});
		
		
		new Thread(new GetWebDataThread()).start();

		
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
					startActivity(new Intent(EquipInfoActivity.this,MapActivity.class));
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
		WeakReference<EquipInfoActivity> mActivity;

		MyHandler(EquipInfoActivity activity){
			mActivity = new WeakReference<EquipInfoActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			EquipInfoActivity theActivity = mActivity.get();
			@SuppressWarnings("unchecked")
			List<Map<String,String>> list  = (List<Map<String, String>>) msg.obj;
			theActivity.tv.setText(list.get(0).get("unitAddress"));
			theActivity.listView.setAdapter(new SimpleAdapter(theActivity, list, R.layout.layout_show_company, new String[]{"equipmentVariety","riskValue","userPoint"}, new int[]{R.id.tv01,R.id.tv02,R.id.tv03}));

		}
		
	}
	
	class GetWebDataThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			HashMap<String,String> params = new HashMap<String,String>();
			params.put("unitAddress", getIntent().getExtras().getString("unitAddress"));
			String message = CasClient.getInstance().doPost(UrlStrings.GET_ONE_UNIT_ADDRESS_INFO, params);			
			Message msg = Message.obtain();
			try {
				List<Map<String,String>> list = JsonUtils.getEquipInfoData(message);
				msg.obj = list;
				handler.sendMessage(msg);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
	}
	
	

}
