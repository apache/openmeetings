package org.openmeetings.app.documents;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

public class LibraryDocuments {
	
	private static final Logger log = Logger.getLogger(LibraryDocuments.class);

	private static LibraryDocuments instance;

	private LibraryDocuments() {}

	public static synchronized LibraryDocuments getInstance() {
		if (instance == null) {
			instance = new LibraryDocuments();
		}
		return instance;
	}
}
