package org.openmeetings.server;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.apache.log4j.Logger;

/**
 * @author sebastianwagner
 *
 */
public class TestScrollbar extends TestCase {
	
	//private static final Logger log = Logger.getLogger(TestSocket.class);
	private static Logger log = Logger.getLogger(TestScrollbar.class);
	
	
	public TestScrollbar(String testname){
		super(testname);
	}
	
	public void testTestSocket(){
		try {
			
//			ServerSocketProcess serverSocketProcess = new ServerSocketProcess();
//			
//			serverSocketProcess.run();
			
			System.out.println("TEST COMPLETE _ _");
		
		} catch (Exception err) {
			log.error("[TestSocket] ",err);
		}
		
	}

}
