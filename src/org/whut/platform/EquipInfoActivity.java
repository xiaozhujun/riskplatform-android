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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class EquipInfoActivity extends Activity{

	private TextView tv;
	private Spinner sp1;
	private Spinner sp2;
	private ListView listView;
	private MyHandler handler;
	private String[] riskValues = {"风险值","6","5","4","3","2","1"};
	private String[] equipmentVarieties = {"设备类型","冶金桥式起重机","通用门式起重机","电动葫芦门式起重机","门式起重机","轨道式集装箱门式起重机","轮胎式门式起重机"};
	//用来记录收藏按钮的状态
	private static boolean isCollected = false;
	
	private String[] unitAddresses;
	private String equipmentVariety;
	private String riskValue;
	private String userPoint;
	private List<CollectionData>list;
	private CollectionData[] info;
    private SimpleAdapter adapter;

    private dataDao data;

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_equip_info);

		tv = (TextView) findViewById(R.id.tv_topbar_middle_detail);
		 data=new dataDao(EquipInfoActivity.this);

		sp1 = (Spinner) findViewById(R.id.spinner01);
		sp2 = (Spinner) findViewById(R.id.spinner02);

		sp1.setAdapter(new ArrayAdapter<String>(getApplicationContext(),R.layout.item_sp_types,riskValues));
		sp2.setAdapter(new ArrayAdapter<String>(getApplicationContext(), R.layout.item_sp_types, equipmentVarieties));
 
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

        list=new ArrayList<CollectionData>();
		
		
		initData();
		
 
		
       
		MyApplication.getInstance().addActivity(this);

	}


	private void initData(){
		dataDao ddDao=new dataDao(EquipInfoActivity.this);
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
			
			theActivity.equipmentVariety = list.get(0).get("equipmentVariety");
			theActivity.riskValue = list.get(0).get("riskValue");
			theActivity.userPoint = list.get(0).get("userPoint");
			
			
			 theActivity.adapter = new SimpleAdapter(theActivity, list, R.layout.popup_bottom_view, new String[]{"equipmentVariety","riskValue","userPoint"}, new int[]{R.id.layout_title,R.id.riskValue,R.id.use_point})
			{

				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					// TODO Auto-generated method stub
					convertView = super.getView(position, convertView, parent);
					RelativeLayout button_detail = (RelativeLayout) convertView.findViewById(R.id.button_route_layout);
					button_detail.setOnClickListener(new OnClickListener() {						
						@Override
						public void onClick(View v) {		
							Intent it = new Intent(theActivity,InfoActivity.class);
							it.putExtra("unitAddress", theActivity.tv.getText().toString());
							theActivity.startActivity(it);				
						}
					});

					final RelativeLayout button_collect = (RelativeLayout) convertView.findViewById(R.id.button_call_layout);
					final Button button = (Button) button_collect.findViewById(R.id.button_call); 
					//button_collect.setOnClickListener(theActivity.new MyListener());
              
					button_collect.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							
							if(isCollected){						
								button.setText("收藏");
								Drawable drawable = theActivity.getResources().getDrawable(R.drawable.place_ratingbar_full_empty);
								drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
								button.setCompoundDrawables(drawable,null,null,null);					
								isCollected = false;
								theActivity.data.delete(theActivity.tv.getText().toString());
								
								theActivity.adapter.notifyDataSetChanged();
							}else{
								button.setText("取消收藏");	
								Drawable drawable = theActivity.getResources().getDrawable(R.drawable.place_ratingbar_full_filled);
								drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
								button.setCompoundDrawables(drawable,null,null,null);
								isCollected = true;	
								CollectionData cd = new CollectionData(theActivity.tv.getText().toString(),theActivity.equipmentVariety, theActivity.riskValue, theActivity.userPoint);
								theActivity.data.insert(cd);
								
								theActivity.adapter.notifyDataSetChanged();
							}
						}
					});

					return convertView;
				}
			};
			theActivity.listView.setAdapter(theActivity.adapter);			

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
				msg.obj = list;
				handler.sendMessage(msg);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

	}

}
