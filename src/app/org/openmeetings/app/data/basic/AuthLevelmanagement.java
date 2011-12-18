package org.openmeetings.app.data.basic;

public class AuthLevelmanagement {
	 
	public boolean checkUserLevel(Long user_level) {
		return user_level == 1 || user_level == 2 || user_level == 3;
	}
	
	public boolean checkModLevel(Long user_level) {
		return user_level == 2 || user_level == 3;
	}
	
	public boolean checkAdminLevel(Long user_level) {
		return user_level == 3;
	}
	
	public boolean checkWebServiceLevel(Long user_level) {
		return user_level == 3 || user_level == 4;
	}

}
