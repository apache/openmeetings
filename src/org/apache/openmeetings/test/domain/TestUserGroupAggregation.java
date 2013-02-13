/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License") +  you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openmeetings.test.domain;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.openmeetings.data.user.OrganisationManager;
import org.apache.openmeetings.persistence.beans.domain.Organisation;
import org.apache.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestUserGroupAggregation extends AbstractOpenmeetingsSpringTest {

	@Autowired
	private OrganisationManager organisationManager;

	private static final Logger log = Logger
			.getLogger(TestUserGroupAggregation.class);

	@Test
	public void testitNow() {

		List<Organisation> orgUser = organisationManager.getOrganisationsByUserId(3, 1, 0,
				100, "organisation_id", true);

		assertTrue("Default user must belong to at least one organisation", orgUser.size() > 0);

		for (Organisation orgUserObj : orgUser) {
			log.error("testitNow: organisation Id: '" + orgUserObj.getOrganisation_id() + "'; name: '" + orgUserObj.getName() + "'");
		}

		List<Organisation> orgUser2 = organisationManager.getRestOrganisationsByUserId(3,
				1, 0, 100, "organisation_id", true);

		log.error("testitNow: rest organisations count: " + orgUser2.size());

		for (Organisation orgUserObj : orgUser2) {
			log.error("testitNow: organisation Id: '" + orgUserObj.getOrganisation_id() + "'; name: '" + orgUserObj.getName() + "'");
		}
		
	}
}
