package org.openmeetings.utils.crypt;

import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class ManageCryptStyle {

	private static final Logger log = Red5LoggerFactory.getLogger(
			ManageCryptStyle.class, ScopeApplicationAdapter.webAppRootKey);
	@Autowired
	private Configurationmanagement cfgManagement;

	@Autowired
	private ScopeApplicationAdapter scopeApplicationAdapter;

	public CryptStringAdapter getInstanceOfCrypt() {
		try {

			log.debug("getInstanceOfCrypt: " + this);

			log.debug("getInstanceOfCrypt: " + cfgManagement);

			// String configKeyCryptClassName =
			// "org.openmeetings.utils.crypt.MD5Implementation";
			String configKeyCryptClassName = scopeApplicationAdapter
					.getCryptKey();

			log.debug("configKeyCryptClassName: " + configKeyCryptClassName);

			CryptStringAdapter t = (CryptStringAdapter) Class.forName(
					configKeyCryptClassName).newInstance();
			// t.getTime();
			return t;

		} catch (Exception err) {
			log.error("[getInstanceOfCrypt]", err);
		}
		return null;
	}

}
