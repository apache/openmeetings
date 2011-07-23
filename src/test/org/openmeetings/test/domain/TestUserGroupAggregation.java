package org.openmeetings.test.domain;

import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmeetings.app.data.user.Organisationmanagement;
import org.openmeetings.app.persistence.beans.domain.Organisation;

public class TestUserGroupAggregation extends TestCase {
	
	private static final Logger log = Logger.getLogger(TestUserGroupAggregation.class);
	
	public TestUserGroupAggregation(String testname){
		super(testname);
	}
	
	public void testitNow(){
		
		List orgUser = Organisationmanagement.getInstance().getOrganisationsByUserId(3, 1, 0, 100, "organisation_id", true);
		
		log.error("testitNow"+orgUser.size());
		
		for (Iterator it2 = orgUser.iterator();it2.hasNext();){
			Organisation orgUserObj = (Organisation) it2.next();
			log.error("testitNow"+orgUserObj.getOrganisation_id());
			log.error(orgUserObj.getName());
		}
		
		List orgUser2 = Organisationmanagement.getInstance().getRestOrganisationsByUserId(3, 1, 0, 100, "organisation_id", true);
		
		log.error("testitNow"+orgUser2.size());
		
		for (Iterator it2 = orgUser2.iterator();it2.hasNext();){
			Organisation orgUserObj = (Organisation) it2.next();
			log.error("testitNow"+orgUserObj.getOrganisation_id());
			log.error(orgUserObj.getName());
		}		
		
	}
}
