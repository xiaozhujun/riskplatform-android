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

public class AreaRankActivity extends Activity {

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
		setContentView(R.layout.activity_arearank);

		user_id = getIntent().getExtras().getInt("user_id");
		Log.i("msg", "从上一Activity中传来的user_id为"+user_id);
		
		listView=(ListView)findViewById(R.id.list_serach_rank);

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
				if(getIntent().getExtras().getString("tag")!=null){
					Intent it = new Intent(AreaRankActivity.this,CityRankActivity.class);
					it.putExtra("province",getIntent().getExtras().getString("province"));
					it.putExtra("tag",getIntent().getExtras().getString("tag"));
					it.putExtra("user_id", user_id);
					startActivity(it);
					finish();
				}else{
					finish();
				}
			}
		});


		TextView textView_title = (TextView) findViewById(org.whut.platform.R.id.tv_topbar_middle_detail);
		textView_title.setText("区域排行");

		RelativeLayout rl = (RelativeLayout) findViewById(org.whut.platform.R.id.tv_topbar_right_map_layout);
		rl.setVisibility(View.INVISIBLE);
		rl.setFocusable(false);

		myHandler=new MyHandler(this);
		MyApplication.getInstance().addActivity(this);	
		new Thread(new areaThread()).start();


	}

	static class MyHandler extends Handler{
		WeakReference<AreaRankActivity> myActivity;
		MyHandler(AreaRankActivity activity)
		{
			myActivity=new WeakReference<AreaRankActivity>(activity);
		}
		@Override
		public void handleMessage(Message msg) {
			final AreaRankActivity theActivity=myActivity.get();
			@SuppressWarnings("unchecked")
			List<Map<String, String>> area=(List<Map<String, String>>) msg.obj;
			SimpleAdapter adapter=new SimpleAdapter(theActivity, area, R.layout.rank_view_area, new String[]{"area_rank","craneNumber","avgRiskValue"}, new int[]{R.id.layout_title,R.id.riskValue,R.id.use_point}){


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
							String area = map.get("area");
							Intent it = new Intent(theActivity,PlaceRankActivity.class);
							it.putExtra("province", theActivity.getIntent().getExtras().getString("province"));
							it.putExtra("city", theActivity.getIntent().getExtras().getString("city"));
							it.putExtra("area", area);
							it.putExtra("user_id", user_id);
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

	class areaThread implements Runnable{

		@Override
		public void run() {
			Message msg=Message.obtain();
			List<Map<String, String>> area=null;
			HashMap<String, String> map=new HashMap<String, String>();

			try {
				map.put("province", getIntent().getExtras().getString("province"));
				map.put("city", getIntent().getExtras().getString("city"));
				String message=CasClient.getInstance().doPost(UrlStrings.GET_AREA_RISK, map);
				area=JsonUtils.getAreaRisk(message);
				msg.obj=area;
				myHandler.sendMessage(msg);
			} catch (Exception e) {

				e.printStackTrace();
			}

		}

	}


}
