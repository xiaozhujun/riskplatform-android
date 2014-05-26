package org.whut.platform;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.whut.application.MyApplication;
import org.whut.client.CasClient;
import org.whut.strings.UrlStrings;
import org.whut.utils.JsonUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
//import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class ShowListActivity extends Activity {

	private TextView tv_title;
	private ListView listView;
	private SimpleAdapter simpleAdapter;
	private String province;
	private String city;
	private String area;
	private ProgressDialog dialog;
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
		setContentView(R.layout.activity_show_list);

		tv_title = (TextView) findViewById(R.id.tv_title);

		
		listView = (ListView) findViewById(R.id.show_list);	
		dialog = new ProgressDialog(this);
		dialog.setTitle("提示");
		dialog.setMessage("正在获取数据，请稍后...");



		province = getIntent().getExtras().getString("province");
		city = getIntent().getExtras().getString("city");
		area = getIntent().getExtras().getString("area");
		
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
			    ListView listView = (ListView)arg0;  
			    @SuppressWarnings("unchecked")
			    HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(arg2);  
			    String unitAddress = map.get("company_name");
			    Intent it = new Intent(ShowListActivity.this,EquipInfoActivity.class);
			    it.putExtra("unitAddress", unitAddress);
			    startActivity(it);
			}
		
		});

		new GetDataTask().execute(province,city,area);
		
		
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
					startActivity(new Intent(ShowListActivity.this,MapActivity.class));
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

	class GetDataTask extends AsyncTask<String,Void,List<Map<String,String>>>{


		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog.show();
		}



		@Override
		protected List<Map<String, String>> doInBackground(String... arg0) {
			// TODO Auto-generated method stub

			HashMap<String,String> request_data = new HashMap<String,String>();
			List<Map<String,String>> list = null;
			try{
			request_data.put("province", arg0[0]);
			request_data.put("city", arg0[1]);
			request_data.put("area",arg0[2]);
			String message = CasClient.getInstance().doPost(UrlStrings.SHOW_RANK, request_data);			
			list = JsonUtils.getRankList(message);
			}catch(Exception e){
				e.printStackTrace();
			}
			return list;
		}

		@Override
		protected void onPostExecute(List<Map<String, String>> result) {
			// TODO Auto-generated method stub
			tv_title.setText(province+city+area);				
			simpleAdapter = new SimpleAdapter(getApplicationContext(), result, R.layout.layout_show_list, new String[]{"riskValue","company_name","types"}, new int[]{R.id.tv_riskValue,R.id.tv_company_name,R.id.tv_types});
			listView.setAdapter(simpleAdapter);
			simpleAdapter.notifyDataSetChanged();			
			dialog.dismiss();
		}

	}

}
