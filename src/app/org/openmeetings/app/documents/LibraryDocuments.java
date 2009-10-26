package org.openmeetings.app.documents;

import org.slf4j.Logger;
import org.red5.logging.Red5LoggerFactory;

public class LibraryDocuments {
	
	private static final Logger log = Red5LoggerFactory.getLogger(LibraryDocuments.class, "openmeetings");

	private static LibraryDocuments instance;

	private LibraryDocuments() {}

	public static synchronized LibraryDocuments getInstance() {
		if (instance == null) {
			instance = new LibraryDocuments();
		}
		return instance;
	}
}
