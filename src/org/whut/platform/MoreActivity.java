package org.whut.platform;

import org.whut.application.MyApplication;
import org.whut.client.CasClient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MoreActivity extends Activity{

	private ImageView iv_topbar_left_back;
	private TextView tv_topbar_middle_detail;
	private RelativeLayout tv_topbar_right_map_layout;

	private CheckBox nav_settings_always_light_cb;
	private CheckBox nav_settings_always_light_cb1;
	
	private TextView log_out;
	
	private SharedPreferences preference;
	private Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_more);

		preference = getSharedPreferences("user_settings",MODE_PRIVATE);
		editor = preference.edit();
		
		MyApplication.getInstance().addActivity(MoreActivity.this);

		
		iv_topbar_left_back = (ImageView) findViewById(R.id.iv_topbar_left_back);
		iv_topbar_left_back.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});


		tv_topbar_middle_detail = (TextView) findViewById(R.id.tv_topbar_middle_detail);
		tv_topbar_middle_detail.setText("更多");

		tv_topbar_right_map_layout = (RelativeLayout) findViewById(R.id.tv_topbar_right_map_layout);
		tv_topbar_right_map_layout.setVisibility(View.GONE);
		
		nav_settings_always_light_cb = (CheckBox) findViewById(R.id.nav_settings_always_light_cb);
		nav_settings_always_light_cb1 = (CheckBox) findViewById(R.id.nav_settings_always_light_cb1);
		
		nav_settings_always_light_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					editor.putBoolean("always_light_on", true);
					nav_settings_always_light_cb.setBackgroundResource(R.drawable.nav_checkin_icon);
					editor.commit();
				}else{
					editor.putBoolean("always_light_on", false);
					nav_settings_always_light_cb.setBackgroundResource(R.drawable.nav_checkout_icon);
					editor.commit();
				}
				
			}
		});
		
		nav_settings_always_light_cb1.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					editor.putBoolean("no_picture", true);
					nav_settings_always_light_cb1.setBackgroundResource(R.drawable.nav_checkin_icon);
					editor.commit();
				}else{
					editor.putBoolean("no_picture", false);
					nav_settings_always_light_cb1.setBackgroundResource(R.drawable.nav_checkout_icon);
					editor.commit();
				}
			}
		});
		
		if(preference.contains("always_light_on")){
			nav_settings_always_light_cb.setChecked(preference.getBoolean("always_light_on", false));	
		}
		
		if(preference.contains("no_picture")){
			nav_settings_always_light_cb1.setChecked(preference.getBoolean("no_picture", false));
		}
		
		log_out = (TextView) findViewById(R.id.log_out);
		log_out.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				CasClient.getInstance().reset();
				final Intent it = getPackageManager().getLaunchIntentForPackage(getPackageName());
				it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(it);
			}
		});
		
	}	



}
