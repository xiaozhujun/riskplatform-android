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
import android.app.ProgressDialog;
import android.app.Notification.Action;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class moreSerachResultActivity extends Activity {
	private ProgressDialog dialog;
	public receiveDatahandle datahandle;
	public ListView list_serachResult;
	private List<Map<String, String>> dataMap;
	
	class receiveDatahandle extends Handler
	{
		WeakReference<moreSerachResultActivity> theactivity;
		public receiveDatahandle(moreSerachResultActivity moreserachresultActivity) {
			theactivity=new WeakReference<moreSerachResultActivity>(moreserachresultActivity);
		}
		
		@Override
		public void handleMessage(Message msg)
		{
			moreSerachResultActivity mActivity=theactivity.get();
			if (msg.what==1) {
				mActivity.list_serachResult.setAdapter(new SimpleAdapter(mActivity, dataMap, R.layout.layout_listitem_moreserach, new String[]{"riskValue","province","craneNumber"}, new int[]{R.id.layout_moreserach_listitem_value,R.id.layout_moreserach_listitem_name,R.id.layout_moreserach_listitem_devicenum}));
			}
			else if (msg.what==2) {
				mActivity.list_serachResult.setAdapter(new SimpleAdapter(mActivity, dataMap, R.layout.layout_listitem_moreserach, new String[]{"riskValue","city","craneNumber"}, new int[]{R.id.layout_moreserach_listitem_value,R.id.layout_moreserach_listitem_name,R.id.layout_moreserach_listitem_devicenum}));
				mActivity.dialog.dismiss();
			}
			else if (msg.what==3) {
//				mActivity.sp_cities.setAdapter(new ArrayAdapter<String>(mActivity, R.layout.simple_list_item_1,cities));
				mActivity.dialog.dismiss();
			}
			else if (msg.what==4) {
//				mActivity.sp_areas.setAdapter(new ArrayAdapter<String>(mActivity, R.layout.simple_list_item_1,areas));
				mActivity.dialog.dismiss();
			}
		}
		
	}
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_moreserach_result);
		list_serachResult=(ListView)findViewById(R.id.list_serach_rank);
		dataMap=new ArrayList<Map<String, String>>();

		datahandle=new receiveDatahandle(this);
		
		dialog = new ProgressDialog(this);
		dialog.setTitle("提示");
		dialog.setMessage("正在获取数据，请稍后...");
		dialog.setCancelable(true);
		dialog.setIndeterminate(false);

		if (null == getIntent().getStringExtra("province")) {
			dialog.show();
			new Thread(new GetProvinceInfoWithDataRuleByCondition()).start();
		} else if (null == getIntent().getStringExtra("city")) {
			dialog.show();
			new Thread(new GetCityInfoByCondition()).start();
		} else if (null == getIntent().getStringExtra("area")) {
			dialog.show();
			new Thread(new GetAreaInfoByCondition()).start();
		} else {
			dialog.show();
			new Thread(new GetCraneInfoByCondition()).start();
		}
	}

	class GetCraneInfoByCondition implements Runnable {

		@Override
		public void run() {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("province", getIntent().getStringExtra("province"));
			params.put("city", getIntent().getStringExtra("city"));
			params.put("area", getIntent().getStringExtra("area"));
			params.put("equipmentVariety",getIntent().getStringExtra("equipment"));
			params.put("useTime", getIntent().getStringExtra("useyear"));
			params.put("value", getIntent().getStringExtra("riskvalue"));
			Message msg = Message.obtain();
			try {
				msg.what = 4;
				dataMap = JsonUtils.getCraneInfoByCondition(CasClient.getInstance().doPost(
						UrlStrings.GetCraneInfoByCondition, params));
				datahandle.sendMessage(msg);
			} catch (Exception e) {
			}
		}

	}

	class GetAreaInfoByCondition implements Runnable {

		@Override
		public void run() {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("province", getIntent().getStringExtra("province"));
			params.put("city", getIntent().getStringExtra("city"));
			params.put("equipmentVariety",getIntent().getStringExtra("equipment"));
			params.put("useTime", getIntent().getStringExtra("useyear"));
			params.put("value", getIntent().getStringExtra("riskvalue"));
			Message msg = Message.obtain();
			try {
				msg.what = 3;
				dataMap = JsonUtils.getAreaInfoByCondition(CasClient.getInstance().doPost(
						UrlStrings.GetAreaInfoByCondition, params));
				datahandle.sendMessage(msg);
			} catch (Exception e) {
			}
		}

	}

	class GetCityInfoByCondition implements Runnable {

		@Override
		public void run() {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("province", getIntent().getStringExtra("province"));
			params.put("equipmentVariety",getIntent().getStringExtra("equipmentVariety"));
			params.put("useTime", getIntent().getStringExtra("useTime"));
			params.put("value", getIntent().getStringExtra("value"));
			Message msg = Message.obtain();
			try {
				msg.what = 2;
				String temp=CasClient.getInstance().doPost(
						UrlStrings.GetCityInfoByCondition, params);
				dataMap = JsonUtils.getCityInfoByCondition(temp);
				datahandle.sendMessage(msg);
			} catch (Exception e) {
			}
		}

	}

	class GetProvinceInfoWithDataRuleByCondition implements Runnable {

		@Override
		public void run() {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("equipmentVariety",getIntent().getStringExtra("equipment"));
			params.put("useTime", getIntent().getStringExtra("useyear"));
			params.put("value", getIntent().getStringExtra("riskvalue"));
			Message msg = Message.obtain();
			try {
				msg.what = 1;
				dataMap = JsonUtils.getProvinceInfoWithDataRule(CasClient.getInstance().doPost(
						UrlStrings.GetProvinceInfoWithDataRule,
						params));
				datahandle.sendMessage(msg);
			} catch (Exception e) {
			}
		}

	}

}
