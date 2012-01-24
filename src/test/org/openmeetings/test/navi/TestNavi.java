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

import java.util.Iterator;
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
		
		Users us = (Users) mService.loginUser(sessionData.getSession_id(), "SebastianWagner", "test",false,null,-1L);
		
		System.out.println("us: "+us.getFirstname());
		
        for (Iterator<Organisation_Users> iter = us.getOrganisation_users()
                .iterator(); iter.hasNext();) {

            Long organization_id = iter.next().getOrganisation()
                    .getOrganisation_id();
            List<Naviglobal> ll = mService.getNavi(sessionData.getSession_id(), 1, organization_id);

            System.out.println("NaviGlobal size: " + ll.size());

            for (Iterator<Naviglobal> it2 = ll.iterator(); it2.hasNext();) {
                Naviglobal navigl = it2.next();
                System.out.println(navigl.getLabel().getValue());
                List<Navimain> s = navigl.getMainnavi();

                for (Iterator<Navimain> it3 = s.iterator(); it3.hasNext();) {
                    Navimain navim = it3.next();
                    System.out.println("-->" + navim.getLabel().getValue());

                    for (Iterator<Navisub> it4 = navim.getSubnavi().iterator(); it4
                            .hasNext();) {
                        Navisub navis = it4.next();
                        System.out.println("---->"
                                + navis.getLabel().getValue());
                    }

                }
            }
        }
		
	}

}
