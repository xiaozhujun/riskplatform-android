package org.whut.utils;

import java.util.ArrayList;
import java.util.List;
import org.whut.database.entity.Device;
import org.whut.database.service.imp.DeviceServiceDao;

import android.content.Context;

public class SQLiteUtils {

	public static List<Integer> getDeviceIds(Context context,List<Device> list){
		List<Integer> list1 = new ArrayList<Integer>();
		DeviceServiceDao  deviceServiceDao = new DeviceServiceDao(context); 
		for(Device device : list){
			//返回所有Device的ID
			list1.add(deviceServiceDao.findDevice(device));
		}
		return list1;
	}
	
	public static void addDevices(Context context,List<Device> list){
		DeviceServiceDao deviceServiceDao = new DeviceServiceDao(context);
		for(Device device: list){
			if(deviceServiceDao.findDevice(device)==0){
				deviceServiceDao.addDevice(device);	
			}
		}
		
	}

}
