package org.openmeetings.app.documents;

import org.slf4j.Logger;
import org.openmeetings.app.remote.red5.ScopeApplicationAdapter;
import org.red5.logging.Red5LoggerFactory;

public class LibraryDocuments {
	
	private static final Logger log = Red5LoggerFactory.getLogger(LibraryDocuments.class, ScopeApplicationAdapter.webAppRootKey);

	private static LibraryDocuments instance;

	private LibraryDocuments() {}

	public static synchronized LibraryDocuments getInstance() {
		if (instance == null) {
			log.debug("instance created");
			instance = new LibraryDocuments();
		}
		return instance;
	}
}
