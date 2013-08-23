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
package org.apache.openmeetings.test.userdata;

import org.apache.openmeetings.persistence.beans.basic.Sessiondata;
import org.apache.openmeetings.remote.MainService;
import org.apache.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.apache.openmeetings.utils.crypt.ManageCryptStyle;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestAuth extends AbstractOpenmeetingsSpringTest {
	@Autowired
	private ManageCryptStyle manageCryptStyle;
	@Autowired
	private MainService mService;

	@Test
	public void testTestAuth() {
		Sessiondata sessionData = mService.getsessiondata();

		System.out.println("sessionData: " + sessionData.getSession_id());

		String tTemp = manageCryptStyle.getInstanceOfCrypt().createPassPhrase(
				"test");

		System.out.println("tTemp: " + tTemp);

	}

}
