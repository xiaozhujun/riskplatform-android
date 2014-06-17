package org.whut.platform;



import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.whut.application.MyApplication;
import org.whut.client.CasClient;
import org.whut.database.entity.Collection;
import org.whut.database.service.imp.CollectionServiceDao;
import org.whut.strings.ToastStrings;
import org.whut.strings.UrlStrings;
import org.whut.utils.JsonUtils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

public class InfoActivity extends Activity {

	private MyHandler handler;
	private TextView  tv_topbar_middle_detail,unitAddress,addressId, organizeCode,userPoint,
	safeManager,contactPhone,equipmentVariety,unitNumber,manufactureUnit,manufactureDate,specification,
	workLevel,riskValue,ratedLiftWeight;

	private CheckBox tv_topbar_right_collect;

	private ImageView iv_topbar_left_back;


	private static int user_id,device_id;


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

		user_id = getIntent().getExtras().getInt("user_id");
		device_id = getIntent().getExtras().getInt("device_id");

		final CollectionServiceDao CollectionServicedao = new CollectionServiceDao(InfoActivity.this);


		iv_topbar_left_back = (ImageView) findViewById(R.id.iv_topbar_left_back);
		iv_topbar_left_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(getIntent().getExtras().getString("tag").equals("from_equip_to_info")){
					Intent it = new Intent(InfoActivity.this,EquipInfoActivity.class);
					it.putExtra("user_id", user_id);
					it.putExtra("unitAddress", getIntent().getExtras().getString("unitAddress"));
					startActivity(it);
					finish();
				}

				if(getIntent().getExtras().getString("tag").equals("from_collection_to_info")){
					Intent it = new Intent(InfoActivity.this,CollectionActivity.class);
					it.putExtra("user_id", user_id);
					startActivity(it);
					finish();
				}

				if(getIntent().getExtras().getString("tag").equals("from_result_to_info")){
					finish();
				}

			}
		});




		tv_topbar_middle_detail = (TextView) findViewById(R.id.tv_topbar_middle_detail);

		tv_topbar_middle_detail.setText("设备详情");

		tv_topbar_right_collect = (CheckBox) findViewById(R.id.tv_topbar_right_collect);

		if(CollectionServicedao.findCollection(user_id, device_id)){
			tv_topbar_right_collect.setChecked(true);
			tv_topbar_right_collect.setText("取消");
		}

		tv_topbar_right_collect.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(tv_topbar_right_collect.isChecked()){	
					CollectionServicedao.addCollection(new Collection(user_id,device_id));
					Toast.makeText(InfoActivity.this, ToastStrings.COLLECTION_SUCCESS, Toast.LENGTH_SHORT).show();
					tv_topbar_right_collect.setText("取消");
				}else{
					CollectionServicedao.deleteCollection(user_id, device_id);
					Toast.makeText(InfoActivity.this, ToastStrings.DECOLLECTION_SUCCESS, Toast.LENGTH_SHORT).show();
					tv_topbar_right_collect.setText("收藏");
				}
			}
		});

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
