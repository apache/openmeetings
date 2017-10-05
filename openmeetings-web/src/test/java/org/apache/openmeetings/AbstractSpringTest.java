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
package org.apache.openmeetings;

import static org.junit.Assert.fail;

import org.apache.openmeetings.db.dao.label.LabelDao;
import org.apache.openmeetings.util.OmFileHelper;
import org.apache.tomcat.util.scan.Constants;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({})
@ContextConfiguration(locations={"classpath:applicationContext.xml"}, inheritLocations = true)
public abstract class AbstractSpringTest extends AbstractJUnit4SpringContextTests {

	@BeforeClass
	public static void init() {
		setOmHome();
		System.setProperty(Constants.SKIP_JARS_PROPERTY, "*");
		LabelDao.initLanguageMap();
		if (LabelDao.getLanguages().isEmpty()) {
			fail("Failed to set languages");
		}
	}

	public static void setOmHome() {
		String webappsDir = System.getProperty("om.home", ".");
		OmFileHelper.setOmHome(webappsDir);
		if (!OmFileHelper.getOmHome().exists() || !OmFileHelper.getOmHome().isDirectory()) {
			fail("Invalid directory is specified as OM HOME: " + webappsDir);
		}
	}
}
