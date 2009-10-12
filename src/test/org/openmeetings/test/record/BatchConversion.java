package org.openmeetings.test.record;

import junit.framework.TestCase;

import org.openmeetings.app.data.record.WhiteboardConvertionJobManager;
import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;

public class BatchConversion extends TestCase {
	
	private static final Logger log = Logger.getLogger(BatchConversion.class);
	
	public BatchConversion(String testname){
		super(testname);
	}
	
	public void testBatchConversion(){
		try {
			
			for (int i=0;i<350;i++) {
				WhiteboardConvertionJobManager.getInstance().initJobs();
			}
			
		} catch (Exception err) {
			
			log.error("testBatchConversion",err);
			
		}
		
	}

}
