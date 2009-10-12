package org.openmeetings.test.library;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmeetings.app.documents.LibraryWmlLoader;

import junit.framework.TestCase;


public class TestFileParser extends TestCase {
	
	private static final Logger log = Logger.getLogger(TestFileParser.class);
	
	public TestFileParser(String testname){
		super(testname);
	}
	
	public void testLoadWmlFile(){
		
		try {
			
			LibraryWmlLoader.getInstance().loadWmlFile("src/","filename1");
			
		} catch (Exception err) {
			
			log.error("TestLoadWmlFile",err);
			
		}
		
	}
	
}
