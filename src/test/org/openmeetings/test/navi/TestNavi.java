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
package org.openmeetings.test.navi;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openmeetings.app.persistence.beans.basic.Naviglobal;
import org.openmeetings.app.persistence.beans.basic.Navimain;
import org.openmeetings.app.persistence.beans.basic.Navisub;
import org.openmeetings.app.persistence.beans.basic.Sessiondata;
import org.openmeetings.app.persistence.beans.domain.Organisation_Users;
import org.openmeetings.app.persistence.beans.user.Users;
import org.openmeetings.app.remote.MainService;
import org.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.springframework.beans.factory.annotation.Autowired;

public class TestNavi extends AbstractOpenmeetingsSpringTest {
	@Autowired
	private MainService mService;
	
	@Test
	public void testGetNavi(){
		
		Sessiondata sessionData = mService.getsessiondata();
		
		Users us = (Users) mService.loginUser(sessionData.getSession_id(), username, userpass, false, null, -1L);
		
		assertNotNull("User is unable to login", us);
		System.out.println("us: "+ us.getFirstname());
		
        for (Organisation_Users ou : us.getOrganisation_users()) {

            Long organization_id = ou.getOrganisation().getOrganisation_id();
            List<Naviglobal> ll = mService.getNavi(sessionData.getSession_id(), 1, organization_id);

            assertTrue("GlobalNavi size should be greater than zero: " + ll.size(), ll.size() > 0);
            System.out.println("NaviGlobal size: " + ll.size());

            for (Naviglobal navigl : ll) {
                System.out.println("Naviglobal label: " + navigl.getLabel().getValue());

                for (Navimain navim : navigl.getMainnavi()) {
                    System.out.println("-->" + navim.getLabel().getValue());

                    for (Navisub navis : navim.getSubnavi()) {
                        System.out.println("---->"
                                + navis.getLabel().getValue());
                    }

                }
            }
        }
		
	}

}
