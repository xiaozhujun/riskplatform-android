package org.whut.platform;



import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.whut.application.MyApplication;
import org.whut.client.CasClient;
import org.whut.data.CollectionData;
import org.whut.data.dataDao;
import org.whut.strings.UrlStrings;
import org.whut.utils.JsonUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.Activity;

public class InfoActivity extends Activity {

	private MyHandler handler;
	private TextView  unitAddress,addressId, organizeCode,userPoint,
	safeManager,contactPhone,equipmentVariety,unitNumber,manufactureUnit,manufactureDate,specification,
	workLevel,riskValue,ratedLiftWeight;

	private TextView textView;
	private ImageView imageView;
	String address, Variety, Value, Point;
	private List<CollectionData> list;
	private CollectionData[] info;
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

	@SuppressLint("NewApi")
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
		textView=(TextView)findViewById(R.id.tv_collection);
		imageView=(ImageView)findViewById(R.id.image);
		
		new Thread(new getInfoThread()).start();

		list=new ArrayList<CollectionData>() ;
	    address=getIntent().getExtras().getString("unitAddress");
		initData();

		if (info.length==0) {
			
			textView.setOnClickListener(new MyListener());
			
		}else {
			for (int i = 0; i < info.length; i++) {
				if (info[i].getunitAddress().equals(address)) {
					textView.setText("取消收藏");
					imageView.setBackground(getResources().getDrawable(R.drawable.movie_rating_full));
					
						 textView.setOnClickListener(new MyListener());
					
		          
				}else {
					textView.setOnClickListener(new MyListener());
	               
				}
			}
		}
		MyApplication.getInstance().addActivity(this);
		
	}
	
	private void initData(){
		dataDao ddDao=new dataDao(InfoActivity.this);
	    list = ddDao.get();
		info = new CollectionData[list.size()];
		
		int m = 0;
		for (CollectionData cData : list) {
			info[m] = new CollectionData(cData.getunitAddress(),
					cData.getequipmentVariety(), cData.getriskValue(),
					cData.getuserPoint());
			m++;
		}
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
			theActivity.Variety=list.get(0).get("equipmentVariety");
			theActivity.Value=list.get(0).get("riskValue");
			theActivity.Point=list.get(0).get("userPoint");
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
	public class MyListener implements OnClickListener{

		@SuppressLint("NewApi")
		@Override
		public void onClick(View v) {
			if (textView.getText().toString().equals("收藏")) {
				imageView.setBackground(getResources().getDrawable(R.drawable.movie_rating_full));
				textView.setText("取消收藏");
				dataDao dd = new dataDao(InfoActivity.this);
				CollectionData cd = new CollectionData(address,Variety, Value, Point);
				dd.insert(cd);
				
			}else {
				imageView.setBackground(getResources().getDrawable(R.drawable.movie_rating_empty));
				textView.setText("收藏");
				dataDao ddDao=new dataDao(InfoActivity.this);
				ddDao.delete(address);
				
				initData();
			}
			
		}
		
	}
}
