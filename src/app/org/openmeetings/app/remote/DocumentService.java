package org.openmeetings.app.remote;

import java.util.List;

import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;

/**
 * 
 * @author swagner
 * 
 */
public class DocumentService {

	@SuppressWarnings("unused")
	private static final Logger log = Red5LoggerFactory.getLogger(
			DocumentService.class, ScopeApplicationAdapter.webAppRootKey);

	public List<?> getUserFiles(String SID, String parentFolder) {

		return null;
	}

}
