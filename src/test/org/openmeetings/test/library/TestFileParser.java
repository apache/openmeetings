package org.openmeetings.test.library;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.openmeetings.app.documents.LibraryWmlLoader;
import org.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.springframework.beans.factory.annotation.Autowired;


public class TestFileParser extends AbstractOpenmeetingsSpringTest {
	
	private static final Logger log = Logger.getLogger(TestFileParser.class);
	@Autowired
	private LibraryWmlLoader libraryWmlLoader;
	
	@Test
	public void testLoadWmlFile(){
		
		try {
			
			libraryWmlLoader.loadWmlFile("src/","filename1");
			
		} catch (Exception err) {
			
			log.error("TestLoadWmlFile",err);
			
		}
		
	}
	
}
