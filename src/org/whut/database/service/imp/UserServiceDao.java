package org.whut.database.service.imp;

import org.whut.database.DBHelper;
import org.whut.database.entity.User;
import org.whut.database.service.UserService;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class UserServiceDao implements UserService{

	private DBHelper mySQLite;
	private SQLiteDatabase db;

	
	public UserServiceDao(Context context) throws Exception{
		mySQLite = DBHelper.getInstance(context);
		db = mySQLite.getWritableDatabase();
	}


	@Override
	public void addUser(User user) throws Exception{
		// TODO Auto-generated method stub
		db.beginTransaction();
		db.execSQL("insert into user(user_id,username,password) values(?,?,?)",new Object[]{null,user.getUsername(),user.getPassword()});
		Log.i("msg", "已完成用户添加.");
		db.setTransactionSuccessful();
		db.endTransaction();
	}


	@Override
	public boolean findUser(User user) throws Exception{
		// TODO Auto-generated method stub
		Cursor cursor = db.rawQuery("select * from user where username=?", new String[]{user.getUsername()});	
		while(cursor.moveToNext()){
			Log.i("msg",cursor.getString(cursor.getColumnIndex("username")));
			Log.i("msg",cursor.getString(cursor.getColumnIndex("password")));
			Log.i("msg",cursor.getString(cursor.getColumnIndex("user_id")));
			return true;
		}
		return false;
	}


	@Override
	public int getUserID(User user) throws Exception{
		// TODO Auto-generated method stub
		Cursor cursor = db.rawQuery("select * from user where username=?", new String[]{user.getUsername()});	
		while(cursor.moveToNext()){
			return cursor.getInt(cursor.getColumnIndex("user_id"));
		}
		return 0;
	}
	
	
}
	

