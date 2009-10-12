package org.openmeetings.app.data.basic;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthLevelmanagement {
	 
	private static final Logger log = Logger.getLogger(AuthLevelmanagement.class);

	private AuthLevelmanagement() {}
	
	private static AuthLevelmanagement instance = null;
	
	public static synchronized AuthLevelmanagement getInstance() {
		if (instance == null) {
			instance = new AuthLevelmanagement();
		}
		return instance;
	}

	public boolean checkUserLevel(Long user_level) {
		if (user_level > 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean checkModLevel(Long user_level) {
		if (user_level > 1) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean checkAdminLevel(Long user_level) {
		if (user_level > 2) {
			return true;
		} else {
			return false;
		}
	}

}
