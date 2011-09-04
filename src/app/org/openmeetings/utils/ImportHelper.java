package org.openmeetings.utils;

import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.app.persistence.beans.basic.Configuration;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;

public class ImportHelper {
	private static final org.slf4j.Logger log = Red5LoggerFactory.getLogger(
			ImportHelper.class, ScopeApplicationAdapter.webAppRootKey);
	public static final int DEFAULT_MAX_UPLOAD_SIZE = 1024 * 1024 * 1024; //1GB
	
	public static final int getMaxUploadSize(Configurationmanagement cfgManagement) {
		return getMaxUploadSize(cfgManagement, 3L);
	}
	
	public static final int getMaxUploadSize(Configurationmanagement cfgManagement, Long userLevel) {
		Configuration cfg = cfgManagement.getConfKey(userLevel, "cfgManagement");
		int result = DEFAULT_MAX_UPLOAD_SIZE;
		if (cfg != null) {
			String val = cfg.getConf_value();
			try {
				result = (int)Math.min(Long.parseLong(val), (long)Integer.MAX_VALUE);
			} catch (Exception e) {
				log.error("Invalid value saved for maxUploadSize: " + val, e);
			}
		}
		return result;
	}
}
