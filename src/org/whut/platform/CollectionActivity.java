package org.whut.platform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.whut.adapters.MyAdapter;
import org.whut.database.entity.Collection;
import org.whut.database.entity.Device;
import org.whut.database.service.imp.CollectionServiceDao;
import org.whut.database.service.imp.DeviceServiceDao;
import org.whut.platform.R.color;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class CollectionActivity extends Activity{

	private ListView listView;
	private RelativeLayout no_collection;
	private DeviceServiceDao deviceServiceDao;
	private CollectionServiceDao collectionServiceDao;
	private MyAdapter adapter;
	private List<Map<String,String>> data;
	private Map<String,String> map;

	private static int user_id;
	private static int device_id;

	private boolean ButtonsOn =false;

	private static int[] device_ids;

	private static int[] riskValues;

	private ImageView iv_topbar_left_back;
	private RelativeLayout tv_topbar_right_map_layout;
	private TextView tv_topbar_right_edit;
	private FrameLayout fav_bottom_bar1;
	private Button btnCancelAll;
	private Button btnSelAll;
	private Button btnDelAll;



	
	
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(ButtonsOn){
			fav_bottom_bar1.setVisibility(View.GONE);
			tv_topbar_right_edit.setText("编辑");
			adapter.setVisibility(View.GONE);
			dataChanged();
			ButtonsOn = false;
		}else{
			CollectionActivity.this.finish();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_collection);

		iv_topbar_left_back = (ImageView) findViewById(R.id.iv_topbar_left_back);

		tv_topbar_right_map_layout = (RelativeLayout) findViewById(R.id.tv_topbar_right_map_layout);

		tv_topbar_right_edit = (TextView) findViewById(R.id.tv_topbar_right_edit);

		fav_bottom_bar1 = (FrameLayout) findViewById(R.id.fav_bottom_bar1);

		listView = (ListView) findViewById(R.id.ListView_favorite);

		no_collection = (RelativeLayout) findViewById(R.id.emptyList);
		
		btnCancelAll = (Button) findViewById(R.id.btnCancelAll);
		
		btnSelAll = (Button) findViewById(R.id.btnSelAll);
		
		btnDelAll = (Button) findViewById(R.id.btnDelAll);

		data = new ArrayList<Map<String,String>>();

		user_id = getIntent().getExtras().getInt("user_id");

		deviceServiceDao = new DeviceServiceDao(this);

		collectionServiceDao = new CollectionServiceDao(this);





		iv_topbar_left_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});



		if(collectionServiceDao.getAllCollections(user_id).size()!=0){

			initData();

			adapter = new MyAdapter(data, this);

			listView.setAdapter(adapter);

			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					TextView textview = (TextView) arg1.findViewById(R.id.content_reason);
					String unitAddress = textview.getText().toString().split("：")[1];	
					device_id = device_ids[arg2];
					Intent it = new Intent(CollectionActivity.this,InfoActivity.class);
					it.putExtra("user_id", user_id);
					it.putExtra("device_id", device_id);
					it.putExtra("unitAddress", unitAddress);
					it.putExtra("tag", "from_collection_to_info");
					startActivity(it);
					finish();
				}
			});
			

			tv_topbar_right_map_layout.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					TextView view = (TextView)tv_topbar_right_map_layout.findViewById(R.id.tv_topbar_right_edit);

					if(view.getText().toString().equals("编辑")){
						fav_bottom_bar1.setVisibility(View.VISIBLE);
						view.setText("完成");
						adapter.setVisibility(View.VISIBLE);
						ButtonsOn = true;
						dataChanged();
					}else{
						fav_bottom_bar1.setVisibility(View.GONE);
						view.setText("编辑");
						adapter.setVisibility(View.INVISIBLE);
						ButtonsOn = false;
						dataChanged();
					}
				}
			});
			
			btnSelAll.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					for(int i=0;i<data.size();i++){
						MyAdapter.getIsSelected().put(i, true);
					}
					dataChanged();			
				}
			});
			
			btnCancelAll.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					for(int i=0;i<data.size();i++){
						MyAdapter.getIsSelected().put(i, false);
					}
					dataChanged();
				}
			});
			
			btnDelAll.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					changeData();
					dataChanged();
				}
			});
			
			
		}else{
			no_collection.setVisibility(View.VISIBLE);
			tv_topbar_right_map_layout.setClickable(false);
			tv_topbar_right_map_layout.setBackgroundResource(R.drawable.common_topbar_right_text_bg);
			tv_topbar_right_edit.setClickable(false);
			tv_topbar_right_edit.setTextColor(color.gray);
		}
	}

	private void initData(){
		no_collection.setVisibility(View.GONE);
		List<Collection> list = collectionServiceDao.getAllCollections(user_id);
		riskValues = new int[list.size()];
		device_ids = new int[list.size()];
		for(int i=0;i<list.size();i++){
			Device device = deviceServiceDao.findDeviceById(list.get(i).getDevice_id());
			map = new HashMap<String, String>();
			map.put("unitAddress", "所属地点："+device.getUnitAddress());
			map.put("equipmentVariety", (i+1)+"."+device.getEquipmentVariety());
			map.put("riskValue", "风险值："+device.getRiskValue()+"");
			map.put("usePoint", "使用地点："+device.getUsePoint());
			riskValues[i] = device.getRiskValue();
			device_ids[i] = device.getDevice_id();
			data.add(map);
		}
	}
	
	private void changeData(){
		List<Integer> selectedItem = new ArrayList<Integer>();
		for(int i=0;i<data.size();i++){
			if(MyAdapter.getIsSelected().get(i)){
				selectedItem.add(i);
			}
		}	
		for(int j=0;j<selectedItem.size();j++){
			collectionServiceDao.deleteCollection(user_id, device_ids[selectedItem.get(j)]);
		}
		data=new ArrayList<Map<String,String>>(); ;
		initData();
		adapter = new MyAdapter(data,this);
		adapter.setVisibility(View.VISIBLE);
		listView.setAdapter(adapter);
		if(data.size()==0){
			no_collection.setVisibility(View.VISIBLE);
			tv_topbar_right_map_layout.setClickable(false);
			tv_topbar_right_map_layout.setBackgroundResource(R.drawable.common_topbar_right_text_bg);
			tv_topbar_right_edit.setClickable(false);
			tv_topbar_right_edit.setText("编辑");
			tv_topbar_right_edit.setTextColor(color.gray);
			fav_bottom_bar1.setVisibility(View.GONE);
		}
	}

	private void dataChanged() {  
		adapter.notifyDataSetChanged();  
	}
	
	
}  

