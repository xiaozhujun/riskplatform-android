package org.whut.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{

	private static DBHelper instance;

	
	private DBHelper(Context context) {
		super(context, "db", null, 1);
		// TODO Auto-generated constructor stub
	}
	
	public static DBHelper getInstance(Context context){
		if(instance==null){
			synchronized(DBHelper.class){
				if(instance==null){
					instance = new DBHelper(context);
				}
			}
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		Log.i("msg", "DB正在创建...");
		db.execSQL("CREATE TABLE IF NOT EXISTS USER(user_id integer primary key autoincrement,username varchar(255),password varchar(255))");
		db.execSQL("CREATE TABLE IF NOT EXISTS DEVICE(device_id integer primary key,unitAddress varchar(255),equipmentVariety varchar(255),riskValue int,usePoint varchar(255))");
		db.execSQL("CREATE TABLE IF NOT EXISTS COLLECTION(collection_id integer primary key,user_id bigint(20) not null,device_id bigint(20) not null)");
		Log.i("msg", "DB创建完成...");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	       	db.execSQL("DROP TABLE IF EXISTS USER");  
	        db.execSQL("DROP TABLE IF EXISTS DEVICE");  
	        db.execSQL("DROP TABLE IF EXISTS COLLECTION");   
	        onCreate(db); 
	}

}
