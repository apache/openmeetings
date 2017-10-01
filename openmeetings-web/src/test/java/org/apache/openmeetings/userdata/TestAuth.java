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
package org.apache.openmeetings.userdata;

import org.apache.openmeetings.AbstractJUnitDefaults;
import org.apache.openmeetings.db.dao.server.SessiondataDao;
import org.apache.openmeetings.db.entity.server.Sessiondata;
import org.apache.openmeetings.util.crypt.CryptProvider;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestAuth extends AbstractJUnitDefaults {
	@Autowired
	private SessiondataDao sessionDao;

	@Test
	public void testTestAuth() {
		Sessiondata sessionData = sessionDao.create(1L, 1L);

		System.out.println("sessionData: " + sessionData.getSessionId());

		String tTemp = CryptProvider.get().hash("test");

		System.out.println("tTemp: " + tTemp);

	}

}
