package org.whut.database.entity;

public class Collection {

	private int user_id;
	private int device_id;
	private int collection_id;
	
	public Collection(int user_id, int device_id, int collection_id) {
		super();
		this.user_id = user_id;
		this.device_id = device_id;
		this.collection_id = collection_id;
	}
	
	public Collection(int user_id, int device_id) {
		super();
		this.user_id = user_id;
		this.device_id = device_id;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public int getDevice_id() {
		return device_id;
	}

	public void setDevice_id(int device_id) {
		this.device_id = device_id;
	}

	public int getCollection_id() {
		return collection_id;
	}

	public void setCollection_id(int collection_id) {
		this.collection_id = collection_id;
	}
	
	
	
}
