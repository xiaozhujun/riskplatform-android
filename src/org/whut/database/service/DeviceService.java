package org.whut.database.service;

import org.whut.database.entity.Device;

public interface DeviceService {

	public void addDevice(Device device);
	
	public int findDevice(Device device);
	
	public Device findDeviceById(int device_id);
	
}
