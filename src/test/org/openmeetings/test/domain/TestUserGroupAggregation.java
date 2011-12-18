package org.openmeetings.test.domain;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.openmeetings.app.data.user.Organisationmanagement;
import org.openmeetings.app.persistence.beans.domain.Organisation;
import org.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.springframework.beans.factory.annotation.Autowired;

public class TestUserGroupAggregation extends AbstractOpenmeetingsSpringTest {

	@Autowired
	private Organisationmanagement organisationmanagement;

	private static final Logger log = Logger
			.getLogger(TestUserGroupAggregation.class);

	@Test
	public void testitNow() {

		List<Organisation> orgUser = organisationmanagement.getOrganisationsByUserId(3, 1, 0,
				100, "organisation_id", true);

		log.error("testitNow" + orgUser.size());

		for (Iterator<Organisation> it2 = orgUser.iterator(); it2.hasNext();) {
			Organisation orgUserObj = it2.next();
			log.error("testitNow" + orgUserObj.getOrganisation_id());
			log.error(orgUserObj.getName());
		}

		List<Organisation> orgUser2 = organisationmanagement.getRestOrganisationsByUserId(3,
				1, 0, 100, "organisation_id", true);

		log.error("testitNow" + orgUser2.size());

		for (Iterator<Organisation> it2 = orgUser2.iterator(); it2.hasNext();) {
			Organisation orgUserObj = it2.next();
			log.error("testitNow" + orgUserObj.getOrganisation_id());
			log.error(orgUserObj.getName());
		}

	}
}
