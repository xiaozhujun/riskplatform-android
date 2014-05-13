package org.whut.platform;

import java.lang.ref.WeakReference;

import org.whut.client.CasClient;
import org.whut.strings.ToastStrings;
import org.whut.strings.UrlStrings;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity {

	private EditText edt_uname;
	private EditText edt_pwd;
	private CheckBox cb_show_pwd;
	//private EditText edt_check_code;
	private Spinner sp_types;
	private Button btn_login;
	private ArrayAdapter<String> adapter;
	private String[] data = {"普通用户","政府用户"};
	private String username;
	private String password;
	private MyHandler handler;
	private ProgressDialog dialog;

	public void setActivityChanged(){
		dialog.dismiss();
		startActivity(new Intent(this,MapActivity.class));
		finish();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_main);
		edt_uname = (EditText) findViewById(R.id.edt_uname);
		edt_pwd = (EditText) findViewById(R.id.edt_pwd);		
		//		edt_check_code = (EditText) findViewById(R.id.edt_check_code);
		cb_show_pwd = (CheckBox) findViewById(R.id.cb_show_pwd);
		sp_types = (Spinner) findViewById(R.id.sp_types);
		btn_login = (Button) findViewById(R.id.btn_login);
		dialog = new ProgressDialog(this);
		dialog.setTitle("提示");
		dialog.setMessage("正在登录，请稍后...");
		dialog.setIndeterminate(false);
		dialog.setCancelable(true);

		adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.item_sp_types, data);
		adapter.setDropDownViewResource(R.layout.item_sp_types);
		sp_types.setAdapter(adapter);
		sp_types.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});
		sp_types.setVisibility(View.VISIBLE);

		cb_show_pwd.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(cb_show_pwd.isChecked()){
					edt_pwd.setTransformationMethod(HideReturnsTransformationMethod
							.getInstance());
				}else{
					edt_pwd.setTransformationMethod(PasswordTransformationMethod
							.getInstance());
				}
			}

		});


		btn_login.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_DOWN){     
					//更改为按下时的背景图片     
					((Button)v).setBackgroundResource(R.drawable.loginbtn_bg_press_color);
				}
				else if(event.getAction() == MotionEvent.ACTION_UP){     
					//改为抬起时的图片     
					((Button)v).setBackgroundResource(R.drawable.loginbtn_bg_normal_color);     
					new Thread(new LoginThread()).start();
					dialog.show();
				}
				return true;
			}
		});

		handler = new MyHandler(this);

	}

	static class MyHandler extends Handler{
		WeakReference<MainActivity> mActivity;

		MyHandler(MainActivity activity){
			mActivity = new WeakReference<MainActivity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			MainActivity theActivity = mActivity.get();
			switch(msg.what){
			case 0:
				theActivity.dialog.dismiss();
				Toast.makeText(theActivity, ToastStrings.LOGIN_FAILED, Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(theActivity, ToastStrings.LOGIN_SUCCESS, Toast.LENGTH_SHORT).show();
				theActivity.setActivityChanged();
				break;
			}
		}


	}

	class LoginThread implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub

			Message msg = Message.obtain();
			username= edt_uname.getText().toString();
			password = edt_pwd.getText().toString();
			boolean loginResult = CasClient.getInstance().login(username, password, UrlStrings.SECURITY_CHECK);
			if(loginResult){
				msg.what=1;
				handler.sendMessage(msg);
			}else{
				msg.what=0;
				handler.sendMessage(msg);
			}
		}
	}

}

