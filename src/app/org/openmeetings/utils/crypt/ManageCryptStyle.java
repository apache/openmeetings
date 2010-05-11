package org.openmeetings.utils.crypt;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;

public class ManageCryptStyle {
	
	private static final Logger log = Red5LoggerFactory.getLogger(ManageCryptStyle.class, ScopeApplicationAdapter.webAppRootKey);
	
	private ManageCryptStyle() {}

	private static ManageCryptStyle instance = null;

	public static synchronized ManageCryptStyle getInstance() {
		if (instance == null) {
			instance = new ManageCryptStyle();
		}
		return instance;
	}	
	
	public CryptStringAdapter getInstanceOfCrypt() {
		try {
			
			log.debug("getInstanceOfCrypt: "+this);
			
			log.debug("getInstanceOfCrypt: "+Configurationmanagement.getInstance());
			
			//String configKeyCryptClassName = "org.openmeetings.utils.crypt.MD5Implementation";
			String configKeyCryptClassName = ScopeApplicationAdapter.getCryptKey();
			
			log.debug("configKeyCryptClassName: "+configKeyCryptClassName);
			
			CryptStringAdapter t = (CryptStringAdapter) Class.forName(configKeyCryptClassName).newInstance();
			//t.getTime();
			return t;

		} catch (Exception err) {
			log.error("[getInstanceOfCrypt]",err);
		}
		return null;
	}

}
