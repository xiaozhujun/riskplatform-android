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
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;




public class ProvinceRankActivity extends Activity {

	private ListView listView;
	private MyHandler myHandler;
	private ProgressDialog dialog;
	
	private static int user_id;


	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_provincerank);	
		
		user_id = getIntent().getExtras().getInt("user_id");
		Log.i("msg", "从上一Activity中传过来的user_id为"+user_id);

		dialog = new ProgressDialog(this);
		dialog.setTitle("提示");
		dialog.setMessage("正在获取数据，请稍后...");
		dialog.setCancelable(true);
		dialog.setIndeterminate(false);
		dialog.show();

		findViewById(R.id.iv_topbar_left_back).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(getIntent().getExtras()!=null){
					if(getIntent().getExtras().getString("tag")!=null){
						MapActivity.view_record = true;	
					}
					finish();
				}else{
					finish();
				}
			}
		});

		listView = (ListView) findViewById(R.id.list_serach_rank);
		new Thread(new provinceThread()).start();
		myHandler=new MyHandler(this);
		MyApplication.getInstance().addActivity(this);



		TextView textView_title = (TextView) findViewById(org.whut.platform.R.id.tv_topbar_middle_detail);
		textView_title.setText("省份排行");

		RelativeLayout rl = (RelativeLayout) findViewById(org.whut.platform.R.id.tv_topbar_right_map_layout);
		rl.setVisibility(View.INVISIBLE);
		rl.setFocusable(false);
	}

	static class MyHandler extends Handler{
		WeakReference<ProvinceRankActivity> myActivity;
		MyHandler(ProvinceRankActivity activity)
		{
			myActivity=new WeakReference<ProvinceRankActivity>(activity);
		}
		@Override
		public void handleMessage(Message msg) {
			final ProvinceRankActivity theActivity=myActivity.get();
			@SuppressWarnings("unchecked")
			List<Map<String, String>> province=(List<Map<String, String>>) msg.obj;
			SimpleAdapter adapter=new SimpleAdapter(theActivity,  province, R.layout.rank_view_province, new String[]{"province_rank","craneNumber","avgRiskValue"}, new int[]{R.id.layout_title,R.id.riskValue,R.id.use_point}){

				@Override
				public View getView(final int position, View convertView,
						final ViewGroup parent) {
					// TODO Auto-generated method stub
					convertView =  super.getView(position, convertView, parent);
					RelativeLayout button_detail = (RelativeLayout) convertView.findViewById(R.id.button_route_layout);
					button_detail.setOnClickListener(new OnClickListener() {						
						@Override
						public void onClick(View v) {		
							ListView listView = (ListView)parent;
							@SuppressWarnings("unchecked")
							HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(position);
							String province = map.get("province");
							Intent it = new Intent(theActivity,CityRankActivity.class);
							it.putExtra("province", province);
							it.putExtra("user_id",user_id);
							theActivity.startActivity(it);
						}
					});

					return convertView;	
				}
			};
			theActivity.listView.setAdapter(adapter);
			theActivity.dialog.dismiss();
		}

	}

	class provinceThread implements Runnable{

		@Override
		public void run() {
			Message msg=Message.obtain();
			HashMap<String, String> map=new HashMap<String, String>();
			try{
				String message = CasClient.getInstance().doPost(UrlStrings.GET_PROVINCE_RISK,map);	
				Log.v("province", message);
				List<Map<String,String>> list = JsonUtils.getProvinceRisk(message);
				msg.obj=list;
				myHandler.sendMessage(msg);
			}catch(Exception e){
				e.printStackTrace();
			}

		}

	}

}
