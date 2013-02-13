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
package org.apache.openmeetings.test;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.data.calendar.daos.AppointmentDao;
import org.apache.openmeetings.data.user.UserManager;
import org.apache.openmeetings.data.user.dao.UsersDao;
import org.apache.openmeetings.installation.ImportInitvalues;
import org.apache.openmeetings.installation.InstallationConfig;
import org.apache.openmeetings.persistence.beans.calendar.Appointment;
import org.apache.openmeetings.persistence.beans.user.User;
import org.apache.openmeetings.utils.OmFileHelper;
import org.apache.openmeetings.utils.crypt.ManageCryptStyle;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@TestExecutionListeners({})
@ContextConfiguration(locations={"classpath:/openmeetings-applicationContext.xml"}, inheritLocations = true)
public abstract class AbstractOpenmeetingsSpringTest extends AbstractJUnit4SpringContextTests {
	private static final Logger log = Red5LoggerFactory.getLogger(AbstractOpenmeetingsSpringTest.class);
	
	protected static final String username = "swagner";
	protected static final String userpass = "qweqwe";
	private static final String orgname = "smoketest";
	private static final String timeZone = "Europe/Berlin";
	private static final String useremail = "junit@openmeetings.de";
	
	@Autowired
	private AppointmentDao appointmentDao;
	@Autowired
	private UserManager userManager;
	@Autowired
	private UsersDao usersDao;
	@Autowired
	private ImportInitvalues importInitvalues;
	@Autowired
	private ConfigurationDao configurationDao;
	@Autowired
	private ManageCryptStyle cryptManager;

	@Before
	public void setUp() throws Exception {
		setOmHome();
        if (usersDao.count() < 1) {
            makeDefaultScheme();
            log.info("Default scheme created successfully");
        } else {
            log.info("Default scheme already created");
        }
    }

	public Appointment createAppointment() throws Exception {
		assertNotNull("Can't access to appointment dao implimentation", appointmentDao);

		// add new appointment
		Appointment ap = new Appointment();

		ap.setAppointmentName("appointmentName");
		ap.setAppointmentLocation("appointmentLocation");

		Date appointmentstart = new Date();
		Date appointmentend = new Date();
		appointmentend.setTime(appointmentstart.getTime() + 3600);

		ap.setAppointmentStarttime(appointmentstart);
		ap.setAppointmentEndtime(appointmentend);
		ap.setAppointmentDescription("appointmentDescription");
		ap.setStarttime(new Date());
		ap.setDeleted(false);
		ap.setIsDaily(false);
		ap.setIsWeekly(false);
		ap.setIsMonthly(false);
		ap.setIsYearly(false);
		ap.setIsPasswordProtected(false);

		ap.setUserId(usersDao.get(1L));
		ap.setIsConnectedEvent(false);
		Long id = appointmentDao.addAppointmentObj(ap);
		assertNotNull("Cann't add appointment", id);
		return ap;
	}

	public User createUser(int rnd) throws Exception {
		User users = new User();
		// add user
		users.setFirstname("firstname" + rnd);
		users.setLastname("lastname" + rnd);
		users.setLogin("login");
		users.updatePassword(cryptManager, configurationDao, "pass" + rnd);
		users.setLanguage_id(1L);
		Long user_id = userManager.addUser(users);
		assertTrue("Cann't add user", user_id > 0);
		users = userManager.getUserByIdAndDeleted(user_id);
		assertNotNull("User should not be null", users);
		return users;
	}

	protected void setOmHome() {
		String webappsDir = System.getProperty("om.home", ".");
		OmFileHelper.setOmHome(webappsDir);
		if (!OmFileHelper.getOmHome().exists() || !OmFileHelper.getOmHome().isDirectory()) {
			fail("Invalid directory is specified as OM HOME: " + webappsDir);
		}
	}
	
	private void makeDefaultScheme() throws Exception {
		importInitvalues.loadAll(new InstallationConfig(), username, userpass,
				useremail, orgname, timeZone, false);
	}
}
