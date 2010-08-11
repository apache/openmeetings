package org.openmeetings.app.data.basic.dao;

import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

public class LdapConfigDaoImpl {

	private static final Logger log = Red5LoggerFactory.getLogger(LdapConfigDaoImpl.class, ScopeApplicationAdapter.webAppRootKey);

	private LdapConfigDaoImpl() {
	}

	private static LdapConfigDaoImpl instance = null;
 
	public static synchronized LdapConfigDaoImpl getInstance() {
		if (instance == null) {
			instance = new LdapConfigDaoImpl();
		}
		return instance;
	}
	
}
