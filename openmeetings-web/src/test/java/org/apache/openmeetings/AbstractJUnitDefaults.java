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

import static org.apache.openmeetings.util.OpenmeetingsVariables.DEFAULT_CONTEXT_NAME;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getCryptClassName;
import static org.apache.openmeetings.util.OpenmeetingsVariables.setWicketApplicationName;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.calendar.AppointmentDao;
import org.apache.openmeetings.db.dao.user.GroupDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.Address;
import org.apache.openmeetings.db.entity.user.GroupUser;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.installation.ImportInitvalues;
import org.apache.openmeetings.installation.InstallationConfig;
import org.junit.Before;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractJUnitDefaults extends AbstractSpringTest {
	private static final Logger log = Red5LoggerFactory.getLogger(AbstractJUnitDefaults.class);
	private static final String timeZone = "Europe/Berlin";
	public static final int ONE_HOUR = 60 * 60 * 1000;
	public static final String adminUsername = "admin";
	public static final String regularUsername = "user";
	public static final String soapUsername = "soap";
	protected static final String groupAdminUsername = "groupAdmin";
	public static final String userpass = "Q!w2e3r4t5";
	public static final String group = "smoketest";
	public static final String email = "junit@openmeetings.apache.org";
	public static final Random rnd = new Random();

	@Autowired
	private AppointmentDao appointmentDao;
	@Autowired
	protected UserDao userDao;
	@Autowired
	protected GroupDao groupDao;
	@Autowired
	private ImportInitvalues importInitvalues;
	@Autowired
	protected ConfigurationDao cfgDao;

	@Before
	public void setUp() throws Exception {
		setWicketApplicationName(DEFAULT_CONTEXT_NAME);
		cfgDao.reinit();
		if (userDao.count() < 1) {
			makeDefaultScheme();
			// soap user
			createSystemUser(soapUsername, false, u -> {
				u.getRights().remove(User.Right.Room);
				u.getRights().remove(User.Right.Dashboard);
				u.getRights().add(User.Right.Soap);
			});

			// regular user
			createSystemUser(regularUsername, false);

			// group admin
			createSystemUser(groupAdminUsername, true);
			log.info("Default scheme created successfully");
		} else {
			log.info("Default scheme already created");
		}
		if (getCryptClassName() == null) {
			assertNotNull("Crypt class name should not be null", getCryptClassName());
		}
	}

	public Appointment getAppointment() {
		Date start = new Date();
		Date end = new Date();
		end.setTime(start.getTime() + ONE_HOUR);
		return getAppointment(start, end);
	}

	public Appointment getAppointment(Date start, Date end) {
		return getAppointment(userDao.get(1L), start, end);
	}

	public Appointment getAppointment(User owner, Date start, Date end) {
		return getAppointment(owner, null, start, end);
	}

	public Appointment getAppointment(User owner, Room r, Date start, Date end) {
		assertNotNull("Can't access to appointment dao implimentation", appointmentDao);

		// add new appointment
		Appointment ap = new Appointment();

		ap.setTitle("appointmentName");
		ap.setLocation("appointmentLocation");

		ap.setStart(start);
		ap.setEnd(end);
		ap.setDescription("appointmentDescription");
		ap.setInserted(new Date());
		ap.setDeleted(false);
		ap.setIsDaily(false);
		ap.setIsWeekly(false);
		ap.setIsMonthly(false);
		ap.setIsYearly(false);
		ap.setPasswordProtected(false);

		ap.setOwner(owner);
		ap.setConnectedEvent(false);

		if (ap.getReminder() == null) {
			ap.setReminder(Appointment.Reminder.none);
		}

		if (r == null) {
			r = new Room();
			r.setType(Room.Type.conference);
			r.setAppointment(true);
		}
		ap.setRoom(r);
		return ap;
	}

	public Appointment createAppointment(Appointment ap) {
		// add new appointment
		ap = appointmentDao.update(ap, null, false);
		assertNotNull("Cann't add appointment", ap.getId());
		return ap;
	}

	public User getUser() throws Exception {
		return getUser(UUID.randomUUID().toString());
	}

	protected static String getLogin(String uid) {
		return String.format("login%s", uid);
	}

	protected static String getEmail(String uid) {
		return String.format("email%s@local", uid);
	}

	protected String createPass() {
		return "pass1_!@#$%_A";
	}

	public User getUser(String uuid) throws Exception {
		User u = new User();
		// add user
		u.setFirstname("firstname" + uuid);
		u.setLastname("lastname" + uuid);
		u.setLogin(getLogin(uuid));
		u.setAddress(new Address());
		u.getAddress().setEmail(getEmail(uuid));
		u.setRights(UserDao.getDefaultRights());
		u.setTimeZoneId("Asia/Bangkok");
		u.updatePassword(createPass());
		u.setLanguageId(1L);
		return u;
	}

	public User createSystemUser(String login, boolean groupAdmin) throws Exception {
		return createSystemUser(login, groupAdmin, null);
	}

	public User createSystemUser(String login, boolean groupAdmin, Consumer<User> postprocess) throws Exception {
		User u = getUser();
		GroupUser gu = new GroupUser(groupDao.get(group), u);
		gu.setModerator(groupAdmin);
		u.getGroupUsers().add(gu);
		u.setLogin(login);
		u.updatePassword(userpass);
		if (postprocess != null) {
			postprocess.accept(u);
		}
		return createUser(u);
	}

	public User createUser() throws Exception {
		return createUser(UUID.randomUUID().toString());
	}

	public User createUser(String uuid) throws Exception {
		return createUser(getUser(uuid));
	}

	public User createUser(User u) {
		u = userDao.update(u, null);
		assertNotNull("Can't add user", u);
		return u;
	}

	private void makeDefaultScheme() throws Exception {
		InstallationConfig cfg = new InstallationConfig();
		cfg.setUsername(adminUsername);
		cfg.setPassword(userpass);
		cfg.setEmail(email);
		cfg.setGroup(group);
		cfg.setTimeZone(timeZone);
		importInitvalues.loadAll(cfg, false);
	}

	public User getContact(String uuid, Long ownerId) {
		return userDao.getContact("email" + uuid, "firstname" + uuid, "lastname" + uuid, ownerId);
	}

	public User createUserContact(Long ownerId) {
		return createUserContact(getContact(UUID.randomUUID().toString(), ownerId), ownerId);
	}

	public User createUserContact(User user, Long ownerId) {
		user = userDao.update(user, ownerId);
		assertNotNull("Cann't add user", user);
		return user;
	}
}
