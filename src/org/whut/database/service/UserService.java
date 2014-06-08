package org.whut.database.service;

import org.whut.database.entity.User;

public interface UserService {
	
	public void addUser(User user) throws Exception;
	
	public boolean findUser(User user) throws Exception;
	
	public int getUserID(User user) throws Exception;
	
}
