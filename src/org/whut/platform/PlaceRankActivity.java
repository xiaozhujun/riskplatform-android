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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class PlaceRankActivity extends Activity {

	private ListView listView;
	private MyHandler myHandler;
	private TextView textView;
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
		setContentView(R.layout.activity_placerank);
		
		user_id = getIntent().getExtras().getInt("user_id");
		Log.i("msg", "从上一Activity中传来的user_id为"+user_id);
		
		listView=(ListView)findViewById(R.id.list_serach_rank);
		textView=(TextView)findViewById(org.whut.platform.R.id.tv_topbar_middle_detail);
		textView.setText(getIntent().getExtras().getString("area")+"风险排名");

		myHandler=new MyHandler(this);
		MyApplication.getInstance().addActivity(this);

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
						Intent it = new Intent(PlaceRankActivity.this,AreaRankActivity.class);
						it.putExtra("province", getIntent().getExtras().getString("province"));
						it.putExtra("city",getIntent().getExtras().getString("city"));
						it.putExtra("tag",getIntent().getExtras().getString("tag"));
						it.putExtra("user_id", user_id);
						startActivity(it);
						finish();
					}else{
						finish();
					}
			}
		});

		TextView tv = (TextView) findViewById(R.id.tv_topbar_right_map);

		tv.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(getIntent().getExtras().getString("tag")!=null){
					PlaceRankActivity.this.finish();
				}else{
					Intent it = new Intent(PlaceRankActivity.this,MapActivity.class);
					it.putExtra("province",getIntent().getExtras().getString("province"));
					it.putExtra("city",getIntent().getExtras().getString("city"));
					it.putExtra("area",getIntent().getExtras().getString("area"));
					it.putExtra("user_id", user_id);
					startActivity(it);
				//	PlaceRankActivity.this.finish();
				}
			}
		});

		new Thread(new placeThread()).start();

	}

	static class MyHandler extends Handler{
		WeakReference<PlaceRankActivity> myActivity;
		MyHandler(PlaceRankActivity acitvity)
		{
			myActivity=new WeakReference<PlaceRankActivity>(acitvity);
		}
		@Override
		public void handleMessage(Message msg) {
			@SuppressWarnings("unchecked")
			List<Map<String, String>> place=(List<Map<String, String>>) msg.obj;
			final PlaceRankActivity theAcitvity=myActivity.get();
			SimpleAdapter simpleAdapter = new SimpleAdapter(theAcitvity, place, R.layout.rank_view_area, new String[]{"id","company_name","riskValue"}, new int[]{R.id.layout_title,R.id.riskValue,R.id.use_point}){

				@Override
				public View getView(final int position, View convertView,
						final ViewGroup parent) {
					// TODO Auto-generated method stub
					convertView = super.getView(position, convertView, parent);
					RelativeLayout button_detail = (RelativeLayout) convertView.findViewById(R.id.button_route_layout);
					button_detail.setOnClickListener(new OnClickListener() {						
						@Override
						public void onClick(View v) {		
							ListView listView = (ListView)parent;
							@SuppressWarnings("unchecked")
							HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(position);
							String unitAddress = map.get("company_name");
							Intent it = new Intent(theAcitvity,EquipInfoActivity.class);
							it.putExtra("unitAddress", unitAddress);
							it.putExtra("user_id", user_id);
							theAcitvity.startActivity(it);
						}
					});

					return convertView;

				}

			};
			theAcitvity.listView.setAdapter(simpleAdapter);
			theAcitvity.dialog.dismiss();
		}

	}

	class placeThread implements Runnable{

		@Override
		public void run() {
			Message msg=Message.obtain();
			HashMap<String,String> request_data = new HashMap<String,String>();
			List<Map<String,String>> list = null;
			try{
				request_data.put("province", getIntent().getExtras().getString("province"));
				request_data.put("city", getIntent().getExtras().getString("city"));
				request_data.put("area",getIntent().getExtras().getString("area"));
				String message = CasClient.getInstance().doPost(UrlStrings.SHOW_RANK, request_data);			
				list = JsonUtils.getCompanyRankList(message);
				msg.obj=list;
				myHandler.sendMessage(msg);
			}catch(Exception e){
				e.printStackTrace();
			}

		}

	}


}
