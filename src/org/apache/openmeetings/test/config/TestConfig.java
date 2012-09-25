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
package org.apache.openmeetings.test.config;

import static junit.framework.Assert.assertEquals;

import java.util.List;

import org.apache.openmeetings.data.basic.Configurationmanagement;
import org.apache.openmeetings.persistence.beans.basic.Configuration;
import org.apache.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.apache.openmeetings.test.calendar.TestAppointmentAddAppointment;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class TestConfig extends AbstractOpenmeetingsSpringTest {
	
	private static final Logger log = LoggerFactory.getLogger(
			TestAppointmentAddAppointment.class);
	
	@Autowired
	private Configurationmanagement configurationmanagement;


	@Test
	public void getConfigKey() {
		
		try {

			System.err.println("THIS");

			Configuration smtp_server = configurationmanagement.getConfKey(3L,
					"smtp_server");
			
			System.err.println("smtp_server " + smtp_server.getUser_id());
			System.err.println("smtp_server " + smtp_server.getUsers());
			
			assertEquals(null, smtp_server.getUsers());

		} catch (Exception err) {
			log.error("[startConversion]", err);
		}
		
	}

	@Test
	public void getConfigs() {
		
		try {
			List<Configuration> list = configurationmanagement.get(0, 1);
			
			for (Configuration conf : list) {
				System.err.println("conf.getUser_id() " + conf.getUser_id());
				System.err.println("conf.getUsers() " + conf.getUsers());
			}

			assertEquals(list.size(), 1);

		} catch (Exception err) {
			log.error("[startConversion]", err);
		}
		
	}
}
