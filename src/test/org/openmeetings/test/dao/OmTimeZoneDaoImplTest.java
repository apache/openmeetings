package org.openmeetings.test.dao;

import org.openmeetings.app.data.basic.dao.OmTimeZoneDaoImpl;
import org.openmeetings.app.hibernate.beans.basic.OmTimeZone;
import org.openmeetings.test.dao.base.AbstractTestCase;


public class OmTimeZoneDaoImplTest extends AbstractTestCase {

	public OmTimeZoneDaoImplTest(String name){
		super(name);
	}
	
	final public void testOmTimeZoneDaoImpl() {
		OmTimeZoneDaoImpl timeZoneDao = OmTimeZoneDaoImpl.getInstance();
		assertNotNull("Cann't access to time zones dao implimentation", timeZoneDao);
		assertTrue("Count of time zones should be more than zero : " + timeZoneDao.getOmTimeZones().size(), timeZoneDao.getOmTimeZones().size() > 0 );
		
		// add new time zone
		String tzName = "Test_TimeZoneName";
		String tzLabel = "Test_TimeZoneLabel";
		String clName = "Test_iCal";
		Integer tzOrderId = 10000;
		
		// add new time zone
		Long tzId = timeZoneDao.addOmTimeZone(tzName, tzLabel, clName, tzOrderId);
		assertTrue("Time zones should be positive number : " + tzId, tzId > 0 );

		// get time zone
		OmTimeZone omTimeZone = timeZoneDao.getOmTimeZoneById(tzId);
		assertNotNull("Time zone should not be null", omTimeZone);
		assertEquals("Time zone name should be " + tzName, tzName, omTimeZone.getJname());
		assertEquals("Time zone label should be " + tzLabel, tzLabel, omTimeZone.getLabel());
		assertEquals("Time zone iCal should be " + clName, clName, omTimeZone.getIcal());
		assertEquals("Time zone order ID should be " + tzOrderId, tzOrderId, omTimeZone.getOrderId());
		
		// delete time zone is not implemented for OmTimeZoneDaoImpl.
	}

}
