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
package org.openmeetings.test.config;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;
import org.openmeetings.app.data.basic.Configurationmanagement;
import org.openmeetings.test.AbstractOpenmeetingsSpringTest;
import org.openmeetings.test.calendar.TestAppointmentAddAppointment;
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
			String use_old_style_ffmpeg_map  = configurationmanagement.getConfValue("use.old.style.ffmpeg.map.option", String.class, "0");
			
			log.debug("use_old_style_ffmpeg_map "+use_old_style_ffmpeg_map);
			
			assertEquals(use_old_style_ffmpeg_map,"1");

		} catch (Exception err) {
			log.error("[startConversion]", err);
		}
		
	}
}
