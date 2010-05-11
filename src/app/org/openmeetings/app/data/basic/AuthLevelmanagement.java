package org.openmeetings.app.data.basic;

import org.slf4j.Logger;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;

public class AuthLevelmanagement {
	 
	private static final Logger log = Red5LoggerFactory.getLogger(AuthLevelmanagement.class, ScopeApplicationAdapter.webAppRootKey);

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
