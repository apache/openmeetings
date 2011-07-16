package org.openmeetings.test.dao.base;

import javax.persistence.EntityManager;

import junit.framework.TestCase;
import org.apache.log4j.Logger;

public class AbstractTestCase extends TestCase {

	/**
	 *	Log instance for reporting messages
	 */
	private Logger	logger;
	
	
	private EntityManager em;

	/**
	  * 	Constructor
	  */

	public AbstractTestCase(String name){
		super(name);
		logger = this.getLogger();
	}
	
		
	 protected void setUp() throws Exception {
	        super.setUp();
	        try {
	            logger.info("Building JPA EntityManager for unit tests");
	            em = EntityFactoryUtils.getEntityManager();
	        } catch (Exception ex) {
	            ex.printStackTrace();
	            fail("Exception during JPA EntityManager instanciation.");
	        }
	    }

	    protected void tearDown() throws Exception {
	        super.tearDown();
	    }

	/**
	  *		Return the log4j object for this test case.
	  */

	public synchronized Logger getLogger()	{
		if (logger == null)	{
			logger = Logger.getLogger(getClass().getName() + "." + getName());
		}

		return logger;
	}
	
	public EntityManager getEntityManager(){
		if (em == null){
            em = EntityFactoryUtils.getEntityManager();
		}
		return em;
	}
}
