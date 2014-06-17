package org.whut.platform;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.whut.application.MyApplication;
import org.whut.client.CasClient;
import org.whut.database.entity.Collection;
import org.whut.database.entity.Device;
import org.whut.database.service.imp.CollectionServiceDao;
import org.whut.strings.ToastStrings;
import org.whut.strings.UrlStrings;
import org.whut.utils.JsonUtils;
import org.whut.utils.SQLiteUtils;

import android.app.Activity;
import android.content.Intent;
//import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class EquipInfoActivity extends Activity{

	private TextView tv;
	private ListView listView;
	private MyHandler handler;
	
	private String[] unitAddresses;

	private static int user_id;
	private static Integer[] device_ids;

	
	
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



		tv = (TextView) findViewById(R.id.tv_topbar_middle_detail);

	

		user_id = getIntent().getExtras().getInt("user_id");
		Log.i("msg", "从上一Activity中传来的user_id为"+user_id);
		
		handler = new MyHandler(this);

		listView = (ListView) findViewById(R.id.listview1);

		findViewById(R.id.iv_topbar_left_back).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

					EquipInfoActivity.this.finish();
				
			}
		});

		TextView textView_title = (TextView)findViewById(org.whut.platform.R.id.tv_topbar_middle_detail);
		textView_title.setText("设备信息");

		RelativeLayout rl = (RelativeLayout) findViewById(org.whut.platform.R.id.tv_topbar_right_map_layout);
		rl.setVisibility(View.INVISIBLE);
		rl.setFocusable(false);

		new Thread(new GetWebDataThread()).start();




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
			final EquipInfoActivity theActivity = mActivity.get();
			@SuppressWarnings("unchecked")
			List<Map<String,String>> list  = (List<Map<String, String>>) msg.obj;
			//此处有误,公司地址和unitAddresses混淆（服务器端数据库设计问题，暂不修改，后期改正）
			theActivity.tv.setText(theActivity.unitAddresses[0]);

			SimpleAdapter adapter = new SimpleAdapter(theActivity, list, R.layout.popup_bottom_view, new String[]{"equipmentVariety","riskValue","userPoint"}, new int[]{R.id.layout_title,R.id.riskValue,R.id.use_point}){
				@Override
				public View getView(final int position, View convertView, ViewGroup parent) {
					// TODO Auto-generated method stub
					convertView = super.getView(position, convertView, parent);
					final Intent it = new Intent(theActivity,InfoActivity.class);
					RelativeLayout button_detail = (RelativeLayout) convertView.findViewById(R.id.button_route_layout);
					button_detail.setOnClickListener(new OnClickListener() {						
						@Override
						public void onClick(View v) {		
							theActivity.startActivity(it);
							theActivity.finish();
						}
					});

					final RelativeLayout button_collect = (RelativeLayout) convertView.findViewById(R.id.button_call_layout);
					final CheckBox check_box = (CheckBox) button_collect.findViewById(R.id.button_call); 
					
					final CollectionServiceDao collectionServiceDao = new CollectionServiceDao(theActivity);
					if(collectionServiceDao.findCollection(user_id,device_ids[position])){
						check_box.setChecked(true);
						check_box.setText("取消收藏");
						it.putExtra("unitAddress", theActivity.tv.getText().toString());	
						it.putExtra("user_id", user_id);
						it.putExtra("device_id", device_ids[position]);
						it.putExtra("tag", "from_equip_to_info");
						Log.i("msg", "用户"+user_id+"对"+device_ids[position]+"为已收藏");
						}else{
						it.putExtra("unitAddress", theActivity.tv.getText().toString());
						it.putExtra("user_id", user_id);
						it.putExtra("device_id", device_ids[position]);
						it.putExtra("tag", "from_equip_to_info");
							Log.i("msg", "用户"+user_id+"对"+device_ids[position]+"为未收藏");
						}
				
			
					
					check_box.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						
						@Override
						public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
							// TODO Auto-generated method stub
							if(check_box.isChecked()){
								//此处添加收藏逻辑代码
								collectionServiceDao.addCollection(new Collection(user_id,device_ids[position]));
								Toast.makeText(theActivity, ToastStrings.COLLECTION_SUCCESS, Toast.LENGTH_SHORT).show();
								check_box.setText("取消收藏");
							}else{
								collectionServiceDao.deleteCollection(user_id, device_ids[position]);
								Toast.makeText(theActivity, ToastStrings.DECOLLECTION_SUCCESS, Toast.LENGTH_SHORT).show();
								check_box.setText("收藏");
							}
						}
					});
					return convertView;
				}
			};
			theActivity.listView.setAdapter(adapter);			

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
				List<String> list2 = JsonUtils.getUnitAddress(message);
				unitAddresses = list2.toArray(new String[list2.size()]);
				List<Device> list3 = JsonUtils.getEquipInfoDataForDeviceId(message);
				SQLiteUtils.addDevices(EquipInfoActivity.this, list3);
				device_ids = SQLiteUtils.getDeviceIds(EquipInfoActivity.this,list3).toArray(new Integer[list3.size()]);
				msg.obj = list;
				handler.sendMessage(msg);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

	}		
}
