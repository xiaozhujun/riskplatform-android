package org.whut.database.entity;

public class Device {

	private int device_id;
	private String unitAddress;
	private String equipmentVariety;
	private int riskValue;
	private String usePoint;
	
	public Device(int device_id, String unitAddress, String equipmentVariety,
			int riskValue, String usePoint) {
		super();
		this.device_id = device_id;
		this.unitAddress = unitAddress;
		this.equipmentVariety = equipmentVariety;
		this.riskValue = riskValue;
		this.usePoint = usePoint;
	}

	public Device(String unitAddress, String equipmentVariety,
			int riskValue, String usePoint) {
		super();
		this.unitAddress = unitAddress;
		this.equipmentVariety = equipmentVariety;
		this.riskValue = riskValue;
		this.usePoint = usePoint;
	}
	
	
	public int getDevice_id() {
		return device_id;
	}

	public void setDevice_id(int device_id) {
		this.device_id = device_id;
	}

	public String getUnitAddress() {
		return unitAddress;
	}

	public void setUnitAddress(String unitAddress) {
		this.unitAddress = unitAddress;
	}

	public String getEquipmentVariety() {
		return equipmentVariety;
	}

	public void setEquipmentVariety(String equipmentVariety) {
		this.equipmentVariety = equipmentVariety;
	}

	public int getRiskValue() {
		return riskValue;
	}

	public void setRiskValue(int riskValue) {
		this.riskValue = riskValue;
	}

	public String getUsePoint() {
		return usePoint;
	}

	public void setUsePoint(String usePoint) {
		this.usePoint = usePoint;
	}
	
	
	
}
