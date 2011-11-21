package org.openmeetings.app.data.basic;

public class AuthLevelmanagement {
	 
	public boolean checkUserLevel(Long user_level) {
		if (user_level == 1 || user_level == 2 || user_level == 3) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean checkModLevel(Long user_level) {
		if (user_level == 2 || user_level == 3) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean checkAdminLevel(Long user_level) {
		if (user_level == 3) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean checkWebServiceLevel(Long user_level) {
		if (user_level == 3 || user_level == 4) {
			return true;
		} else {
			return false;
		}
	}

}
