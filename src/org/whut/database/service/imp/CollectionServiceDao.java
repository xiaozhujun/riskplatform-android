package org.whut.database.service.imp;

import java.util.ArrayList;
import java.util.List;

import org.whut.database.DBHelper;
import org.whut.database.entity.Collection;
import org.whut.database.service.CollectionService;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CollectionServiceDao implements CollectionService{

	private DBHelper mySQLite;
	private SQLiteDatabase db;
	
	public CollectionServiceDao(Context context){
		mySQLite = DBHelper.getInstance(context);
		db = mySQLite.getWritableDatabase();
	}
	
	
	@Override
	public void addCollection(Collection collection){
		// TODO Auto-generated method stub
		db.beginTransaction();
		db.execSQL("insert into collection(collection_id,user_id,device_id) values(?,?,?)",new Object[]{null,collection.getUser_id(),collection.getDevice_id()});
		Log.i("msg", "用户"+collection.getUser_id()+"已完成对设备"+collection.getDevice_id()+"的添加.");
		db.setTransactionSuccessful();
		db.endTransaction();
	}

	@Override
	public void deleteCollection(int user_id, int device_id){
		// TODO Auto-generated method stub
		db.beginTransaction();
		db.execSQL("delete from collection where user_id=? and device_id=?", new Integer[]{user_id,device_id});
		Log.i("msg","用户"+user_id+"对设备"+device_id+"取消收藏");
		db.setTransactionSuccessful();
		db.endTransaction();
	}


	//有收藏，返回list中存放有Collection数据，无收藏，则返回list为空
	@Override
	public List<Collection> getAllCollections(int user_id){
		// TODO Auto-generated method stub
		Cursor cursor =  db.rawQuery("select * from collection where user_id = ?", new String[]{user_id+""});
		List<Collection> list = new ArrayList<Collection>();
		while(cursor.moveToNext()){
			Collection collection = new Collection(user_id,cursor.getInt(cursor.getColumnIndex("device_id")));
			list.add(collection);
		}
		Log.i("msg", list.toString());
		return list;
	}


	@Override
	public boolean findCollection(int user_id, int device_id) {
		// TODO Auto-generated method stub
		Cursor cursor = db.rawQuery("select * from collection where user_id=? and device_id=?", new String[]{user_id+"",device_id+""});
		while(cursor.moveToNext()){
			return true;
		}
		return false;
	}
	
	
}
