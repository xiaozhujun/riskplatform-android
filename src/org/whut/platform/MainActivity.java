package org.whut.platform;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import org.whut.client.CasClient;
import org.whut.database.entity.User;
import org.whut.database.service.imp.UserServiceDao;
import org.whut.strings.ToastStrings;
import org.whut.strings.UrlStrings;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	private AutoCompleteTextView edt_uname;
	private EditText edt_pwd;
	private CheckBox cb_show_pwd;

	private Button btn_login;

	private static String username;
	private static String password;
	private MyHandler handler;
	private ProgressDialog dialog;
	protected SharedPreferences preferences;
	protected Editor editor;

	public void setActivityChanged(Intent intent){
		dialog.dismiss();
		startActivity(intent);
		finish();
	}

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		preferences = getSharedPreferences("usenamedata",Context.MODE_PRIVATE);
		edt_uname = (AutoCompleteTextView) findViewById(R.id.aedt_uname);
		if (!(null==preferences.getStringSet("username", null))) {
			HashSet<String> temp=(HashSet<String>)preferences.getStringSet("username", null);
			String[] str=(String[])temp.toArray(new String[temp.size()]);
			edt_uname.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,str));
		}

		edt_pwd = (EditText) findViewById(R.id.edt_pwd);		
		//		edt_check_code = (EditText) findViewById(R.id.edt_check_code);
		cb_show_pwd = (CheckBox) findViewById(R.id.cb_show_pwd);
		btn_login = (Button) findViewById(R.id.btn_login);
		dialog = new ProgressDialog(this);
		dialog.setTitle("提示");
		dialog.setMessage("正在登录，请稍后...");
		dialog.setIndeterminate(false);
		dialog.setCancelable(true);


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

		edt_uname.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				edt_pwd.setText((String)preferences.getString((String)parent.getItemAtPosition(position), null));

			}
		});
		//		edt_uname.setOnItemSelectedListener(new OnItemSelectedListener() {
		//
		//			@Override
		//			public void onItemSelected(AdapterView<?> parent, View view,
		//					int position, long id) {
		//				edt_pwd.setText((String)preferences.getString((String)parent.getItemAtPosition(position), null));
		//			}
		//
		//			@Override
		//			public void onNothingSelected(AdapterView<?> parent) {
		//				// TODO Auto-generated method stub
		//				
		//			}
		//		});

		btn_login.setOnTouchListener(new View.OnTouchListener() {

			@SuppressLint("NewApi")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction() == MotionEvent.ACTION_DOWN){     
					//更改为按下时的背景图片     
					((Button)v).setBackgroundResource(R.drawable.button_my_login_down);
				}
				else if(event.getAction() == MotionEvent.ACTION_UP){     
					//改为抬起时的图片     
					((Button)v).setBackgroundResource(R.drawable.button_my_login);     
					//储存用户名代码
					editor = preferences.edit();
					if (null==preferences.getStringSet("username", null)) {
						HashSet<String> tempSet=new HashSet<String>();
						tempSet.add(edt_uname.getText().toString());
						editor.putStringSet("username", tempSet);
						editor.commit();
					}
					else {
						if (!preferences.getStringSet("username", null).contains(edt_uname.getText().toString())) {
							HashSet<String> tempSet=(HashSet<String>) preferences.getStringSet("username", null);
							tempSet.add(edt_uname.getText().toString());
							editor.putStringSet("username", tempSet);
							editor.commit();
						}
					}
					//记住密码功能代码
					if (((CheckBox)findViewById(R.id.cb_rem_pwd)).isChecked()) {
						editor.putString(edt_uname.getText().toString(), edt_pwd.getText().toString());
						editor.commit();
					}
					//线程登录代码
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
				User user = new User(username,password);

				Toast.makeText(theActivity, ToastStrings.LOGIN_SUCCESS, Toast.LENGTH_SHORT).show();
				try{
					UserServiceDao userDao = new UserServiceDao(theActivity);
					if(userDao.findUser(user)){
						//do something
						Log.i("msg", userDao.getUserID(user)+"");
					}else{
						userDao.addUser(user);
						Log.i("msg", userDao.getUserID(user)+"");
					}
					//将正在登陆用户的user_id传过去，验证登录用户
					Intent it = new Intent(theActivity,MapActivity.class);
					it.putExtra("user_id", userDao.getUserID(user));
					theActivity.setActivityChanged(it);
				}catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
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

