package org.whut.platform;



import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.whut.application.MyApplication;
import org.whut.client.CasClient;
import org.whut.strings.UrlStrings;
import org.whut.utils.JsonUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class InfoActivity extends Activity {

	private MyHandler handler;
	private TextView  unitAddress,addressId, organizeCode,userPoint,
	safeManager,contactPhone,equipmentVariety,unitNumber,manufactureUnit,manufactureDate,specification,
	workLevel,riskValue,ratedLiftWeight;
//	private String data;
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
		setContentView(R.layout.activity_info);
		handler = new MyHandler(this);
		unitAddress = (TextView) findViewById(R.id.unitAddress);
		addressId=(TextView) findViewById(R.id.addressId);
		organizeCode = (TextView) findViewById(R.id.organizeCode);
		userPoint = (TextView) findViewById(R.id.usePoint);
		safeManager = (TextView) findViewById(R.id.safeManager);
		contactPhone = (TextView) findViewById(R.id.contactPhone);
		equipmentVariety = (TextView) findViewById(R.id.equipmentVariety);
		unitNumber = (TextView)findViewById(R.id.unitNumber);
		manufactureUnit=(TextView)findViewById(R.id.manufactureUnit);
		manufactureDate=(TextView)findViewById(R.id.manufactureDate);
		specification=(TextView)findViewById(R.id.specification);
		workLevel = (TextView)findViewById(R.id.workLevel);
		riskValue=(TextView)findViewById(R.id.riskValue);
		ratedLiftWeight=(TextView)findViewById(R.id.ratedLiftWeight);
		
		new Thread(new getInfoThread()).start();
	
		
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
					startActivity(new Intent(InfoActivity.this,MapActivity.class));
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
		
		WeakReference<InfoActivity> mActivity;
		
		MyHandler(InfoActivity activity){
			mActivity = new WeakReference<InfoActivity>(activity);
		}
		
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			InfoActivity theActivity = mActivity.get();
			@SuppressWarnings("unchecked")
			List<Map<String,String>> list  = (List<Map<String, String>>) msg.obj; 
			theActivity.unitAddress.setText(list.get(0).get("unitAddress"));
			theActivity.addressId.setText(list.get(0).get("addressId"));
			theActivity.organizeCode.setText(list.get(0).get("organizeCode"));
			theActivity.userPoint.setText(list.get(0).get("userPoint"));
			theActivity.safeManager.setText(list.get(0).get("safeManager"));
			theActivity.contactPhone.setText(list.get(0).get("contactPhone"));
			theActivity.equipmentVariety.setText(list.get(0).get("equipmentVariety"));
			theActivity.unitNumber.setText(list.get(0).get("unitNumber"));
			theActivity.manufactureUnit.setText(list.get(0).get("manufactureUnit"));
			theActivity.manufactureDate.setText(list.get(0).get("manufactureDate"));
			theActivity.specification.setText(list.get(0).get("specification"));
			theActivity.workLevel.setText(list.get(0).get("workLevel"));
			theActivity.riskValue.setText(list.get(0).get("riskValue"));
			theActivity.ratedLiftWeight.setText(list.get(0).get("ratedLiftWeight"));
			
		}
		
	}
	
	class getInfoThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			HashMap<String,String> params = new HashMap<String,String>();
			params.put("unitAddress", getIntent().getExtras().getString("unitAddress"));
			String message = CasClient.getInstance().doPost(UrlStrings.GET_ONE_UNIT_ADDRESS_INFO, params);			
			try {	
				Message msg = Message.obtain();
				List<Map<String,String>> infoData = JsonUtils.getInfoData(message);
				msg.obj = infoData;
				handler.sendMessage(msg);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		}
		
	}
}
