package org.whut.database.service;

import java.util.List;

import org.whut.database.entity.Collection;

public interface CollectionService {

	public void addCollection(Collection collection);
	
	public void deleteCollection(int user_id,int device_id);
	
	public List<Collection> getAllCollections(int user_id);
	
	public boolean findCollection(int user_id,int device_id);
	
}
