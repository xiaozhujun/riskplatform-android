package org.whut.data;

public class CollectionData {

	private String unitAddress;
	private String equipmentVariety;
	private String riskValue;
	private String userPoint;

	public CollectionData(String unitAddress, String equipmentVariety,
			String riskValue, String userPoint) {
	
		this.unitAddress = unitAddress;
		this.equipmentVariety = equipmentVariety;
		this.riskValue = riskValue;
		this.userPoint = userPoint;
	}

	
	public void setunitAddress(String unitAddress) {
		this.unitAddress = unitAddress;
	}

	public String getunitAddress() {
		return unitAddress;
	}

	public void setequipmentVariety(String equipmentVariety) {
		this.equipmentVariety = equipmentVariety;
	}

	public String getequipmentVariety() {
		return equipmentVariety;
	}

	public void setriskValue(String riskValue) {
		this.riskValue = riskValue;
	}

	public String getriskValue() {
		return riskValue;
	}

	public void setuserPoint(String userPoint) {
		this.userPoint = userPoint;
	}

	public String getuserPoint() {
		return userPoint;
	}

}
