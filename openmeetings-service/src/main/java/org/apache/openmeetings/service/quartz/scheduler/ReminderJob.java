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
package org.apache.openmeetings.service.quartz.scheduler;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.openmeetings.core.rss.LoadAtomRssFeed.getFeedConnection;
import static org.apache.openmeetings.core.rss.LoadAtomRssFeed.setRss;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_RSS_FEED1;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_RSS_FEED2;
import static org.apache.openmeetings.util.OpenmeetingsVariables.CONFIG_DASHBOARD_SHOW_RSS;
import static org.apache.openmeetings.util.OpenmeetingsVariables.getWebAppRootKey;
import static org.apache.openmeetings.util.OpenmeetingsVariables.isInitComplete;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import org.apache.openmeetings.core.mail.MailHandler;
import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.user.UserDao;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.service.calendar.AppointmentLogic;
import org.apache.openmeetings.service.mail.template.subject.RecordingExpiringTemplate;
import org.apache.openmeetings.service.mail.template.subject.SubjectEmailTemplate;
import org.apache.wicket.util.string.Strings;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.red5.logging.Red5LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;

@Component("reminderJob")
public class ReminderJob extends AbstractJob {
	private static Logger log = Red5LoggerFactory.getLogger(ReminderJob.class, getWebAppRootKey());
	private static final int MAX_ITEM_COUNT = 5;
	@Autowired
	private AppointmentLogic appointmentLogic;
	@Autowired
	private UserDao userDao;
	@Autowired
	private MailHandler mailHandler;
	@Autowired
	private ConfigurationDao cfgDao;;

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
		SAXReader reader = new SAXReader(false);
		reader.setEncoding(UTF_8.name());
		JSONArray feed = new JSONArray();
		for (String url : new String[] {cfgDao.getString(CONFIG_DASHBOARD_RSS_FEED1, ""), cfgDao.getString(CONFIG_DASHBOARD_RSS_FEED2, "")}) {
			if (!Strings.isEmpty(url)) {
				HttpURLConnection con = null;
				try {
					con = getFeedConnection(url);
					try (InputStream is = con.getInputStream()) {
						Document doc = reader.read(is);
						int i = 0;
						for (Element entry : doc.getRootElement().elements("item")) {
							i++;
							feed.put(new JSONObject()
									.put("title", entry.element("title").getStringValue())
									.put("link", entry.element("link").getStringValue())
									.put("content", entry.element("description").getStringValue())
									.put("published", entry.element("pubDate").getStringValue())
									.put("author", entry.element("author").getStringValue())
									);
							if (i > MAX_ITEM_COUNT) {
								break;
							}
						}
						i = 0;
						for (Element entry : doc.getRootElement().elements("entry")) {
							i++;
							Element date = entry.element("published");
							if (date == null) {
								date = entry.element("updated");
							}
							feed.put(new JSONObject()
									.put("title", entry.element("title").getStringValue())
									.put("link", entry.element("link").getStringValue())
									.put("content", entry.element("content").getStringValue())
									.put("published", date.getStringValue())
									.put("author", entry.element("author").getStringValue())
									);
							if (i > MAX_ITEM_COUNT) {
								break;
							}
						}
					}
				} catch (IOException|DocumentException e) {
					log.error("Unexpected error while getting RSS", e);
				} finally {
					if (con != null) {
						con.disconnect();
					}
				}
			}
		}
		if (feed.length() > 0) {
			setRss(feed);
		}
	}
}
