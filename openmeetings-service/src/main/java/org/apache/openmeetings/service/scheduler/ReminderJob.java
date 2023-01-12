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
package org.apache.openmeetings.service.scheduler;

import static org.apache.openmeetings.core.rss.LoadAtomRssFeed.setRss;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_RSS_FEED1;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_RSS_FEED2;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_SHOW_RSS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isInitComplete;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.openmeetings.core.mail.MailHandler;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.user.GroupUserDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.dto.basic.Health;
import org.apache.openmeetings.db.entity.user.Group;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.service.calendar.AppointmentLogic;
import org.apache.openmeetings.service.mail.template.subject.NewGroupUsersNotificationTemplate;
import org.apache.openmeetings.service.mail.template.subject.RecordingExpiringTemplate;
import org.apache.openmeetings.service.mail.template.subject.SubjectEmailTemplate;
import org.apache.openmeetings.util.crypt.CryptProvider;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import com.github.openjson.JSONArray;

import jakarta.inject.Inject;

@Component("reminderJob")
public class ReminderJob extends AbstractJob {
	private static Logger log = LoggerFactory.getLogger(ReminderJob.class);
	@Inject
	private AppointmentLogic appointmentLogic;
	@Inject
	private UserDao userDao;
	@Inject
	private MailHandler mailHandler;
	@Inject
	private ConfigurationDao cfgDao;
	@Inject
	private GroupUserDao groupUserDao;

	// public for testing
	public Map<Long, LocalDateTime> groupNotifications = new HashMap<>();

	public void remindMeetings() {
		log.trace("ReminderJob.remindMeetings");
		if (!isInitComplete()) {
			return;
		}
		try {
			appointmentLogic.doScheduledMeetingReminder();
		} catch (Exception err) {
			log.error("execute", err);
		}
	}

	public void remindExpiringRecordings() {
		log.trace("ReminderJob.remindExpiringRecordings");
		processExpiringRecordings(false, (rec, days) -> {
			if (days > 0) {
				User u = userDao.get(rec.getInsertedBy());
				if (u == null) {
					log.debug("Unable to send expiration email due to recording owner is NULL, {}", rec);
				} else {
					SubjectEmailTemplate templ = RecordingExpiringTemplate.get(u, rec, days);
					mailHandler.send(u.getAddress().getEmail(), templ.getSubject(), templ.getEmail());
				}
			} else {
				log.debug("Recording is too old to send notification, {} days", days);
			}
			rec.setNotified(true);
			recordingDao.update(rec);
		});
	}

	public void loadRss() {
		log.trace("ReminderJob.loadRss");
		if (!isInitComplete()) {
			return;
		}
		if (!cfgDao.getBool(CONFIG_DASHBOARD_SHOW_RSS, false)) {
			log.debug("Rss disabled by Admin");
			return;
		}
		JSONArray feed = new JSONArray();
		for (String url : new String[] {cfgDao.getString(CONFIG_DASHBOARD_RSS_FEED1, ""), cfgDao.getString(CONFIG_DASHBOARD_RSS_FEED2, "")}) {
			if (!Strings.isEmpty(url)) {
				AtomReader.load(url, feed);
			}
		}
		if (feed.length() > 0) {
			setRss(feed);
		}
	}

	public void checkHealth() {
		log.trace("ReminderJob.checkHealth");
		boolean dbOk = false;
		try {
			cfgDao.count();
			dbOk = true;
		} catch (Exception e) {
			log.error("DB seems to be down");
		}
		Health.getInstance().setInited(isInitComplete())
				.setInstalled(CryptProvider.get() != null)
				.setDbOk(dbOk);
	}

	public void notifyNewGroupUsers() {
		log.trace("ReminderJob.notifyNewGroupUsers");
		if (!isInitComplete()) {
			return;
		}
		LocalDateTime now = LocalDateTime.now();
		for (Group g : groupDao.getGroupsForUserNotifications()) {
			LocalDateTime lastChecked = groupNotifications.get(g.getId());
			if (lastChecked == null) {
				groupNotifications.put(g.getId(), now);
				continue;
			}
			if (!Duration.between(lastChecked, now).minusMinutes(g.getNotifyInterval()).isNegative()) {
				long count = groupUserDao.getGroupUserCountAddedAfter(g.getId(), Date.from(lastChecked.atZone(ZoneId.systemDefault()).toInstant()));
				if (count > 0) {
					for (User u : groupUserDao.getGroupModerators(g.getId())) {
						SubjectEmailTemplate templ = NewGroupUsersNotificationTemplate.get(u, g, count);
						mailHandler.send(u.getAddress().getEmail(), templ.getSubject(), templ.getEmail());
					}
				}
				groupNotifications.put(g.getId(), now);
			}
		}
	}
}
