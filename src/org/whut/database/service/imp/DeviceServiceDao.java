package org.whut.database.service.imp;

import org.whut.database.DBHelper;
import org.whut.database.entity.Device;
import org.whut.database.service.DeviceService;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DeviceServiceDao implements DeviceService{

	private DBHelper mySQLite;
	private SQLiteDatabase db;
	
	
	
	public DeviceServiceDao(Context context) {
		mySQLite = DBHelper.getInstance(context);
		db = mySQLite.getWritableDatabase();
	}



	@Override
	public void addDevice(Device device){
		// TODO Auto-generated method stub
		db.beginTransaction();
		db.execSQL("insert into device(device_id,unitAddress,equipmentVariety,riskValue,usePoint) values(?,?,?,?,?)", new Object[]{null,device.getUnitAddress(),device.getEquipmentVariety(),device.getRiskValue(),device.getUsePoint()});
		Log.i("msg", "已完成设备添加.");
		db.setTransactionSuccessful();
		db.endTransaction();
	
	}


	//查找设备是否已经添加，已添加返回device_id,未添加返回0
	@Override
	public int findDevice(Device device){
		// TODO Auto-generated method stub
		Cursor cursor = db.rawQuery("select * from device where unitAddress=? and equipmentVariety=? and riskValue=? and usePoint=?",new String[]{device.getUnitAddress(),device.getEquipmentVariety(),device.getRiskValue()+"",device.getUsePoint()});
		if(cursor.moveToNext()){
			return cursor.getInt(cursor.getColumnIndex("device_id"));
		}
		return 0;
	}

	@Override
	public Device findDeviceById(int device_id){
		// TODO Auto-generated method stub
		Cursor cursor = db.rawQuery("select * from device where device_id=?", new String[]{device_id+""});
		while(cursor.moveToNext()){
			String unitAddress = cursor.getString(cursor.getColumnIndex("unitAddress"));
			String equipmentVariety = cursor.getString(cursor.getColumnIndex("equipmentVariety"));
			int riskValue = cursor.getInt(cursor.getColumnIndex("riskValue"));
			String usePoint = cursor.getString(cursor.getColumnIndex("usePoint"));
			return new Device(device_id,unitAddress,equipmentVariety,riskValue,usePoint);
		}
		return null;
	}
	
	
	
	

}
