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

import static org.apache.openmeetings.util.OpenmeetingsVariables.webAppRootKey;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.openmeetings.core.data.user.OrganisationManager;
import org.apache.openmeetings.db.entity.user.Organisation;
import org.apache.openmeetings.test.AbstractJUnitDefaults;
import org.junit.Test;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class TestUserGroupAggregation extends AbstractJUnitDefaults {
	private static final Logger log = Red5LoggerFactory.getLogger(TestUserGroupAggregation.class, webAppRootKey);

	@Autowired
	private OrganisationManager organisationManager;

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
