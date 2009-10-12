package org.openmeetings.calendar;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmeetings.app.data.user.dao.UsersDaoImpl;

public class TestDatabaseStructureGetUserStart extends TestCase {

	

	private static final Logger log = Logger.getLogger(TestDatabaseStructureGetUserStart.class);

	

	public TestDatabaseStructureGetUserStart(String testname){

		super(testname);

	}

	

	public void testAddingGroup(){

		try {

			UsersDaoImpl.getInstance().getUser(new Long(1));

		} catch (Exception err) {

			log.error("[testAddingGroup]",err);

		}

		

		

	}



}
