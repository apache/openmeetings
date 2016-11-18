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
package org.apache.openmeetings.service.mail.template.subject;

import static org.apache.openmeetings.db.util.ApplicationHelper.ensureApplication;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.service.mail.template.OmTextLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.StringResourceModel;

public class RecordingExpiringTemplate extends AbstractSubjectEmailTemplate {
	private static final long serialVersionUID = 1L;
	private final Recording rec;
	private long remainingDays;
	private final User u;

	private RecordingExpiringTemplate(User u, final Recording rec, long remainingDays) {
		super(getOmSession().getLocale(u));
		this.u = u;
		this.rec = rec;
		this.remainingDays = remainingDays;
	}

	public static AbstractSubjectEmailTemplate get(User u, Recording rec, long remainingDays) {
		ensureApplication(u.getLanguageId());
		return new RecordingExpiringTemplate(u, rec, remainingDays).create();
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		final String app = getBean(ConfigurationDao.class).getAppName();
		add(new Label("greetings", new StringResourceModel("template.recording.expiring.greetings").setParameters(u.getFirstname())));
		add(new Label("body", new StringResourceModel("template.recording.expiring.body").setParameters(app, remainingDays)));
		add(new Label("footer", new StringResourceModel("template.recording.expiring.footer").setParameters(app)));
	}

	@Override
	Fragment getSubjectFragment() {
		Fragment f = new Fragment(COMP_ID, "subject", this);
		Room room = getBean(RoomDao.class).get(rec.getRoomId());
		f.add(new OmTextLabel("prefix", getString("template.recording.expiring.subj.prefix"))
				, new OmTextLabel("room", room == null ? null : new StringResourceModel("template.recording.expiring.subj.room").setParameters(room.getName())).setVisible(room != null)
				);
		return f;
	}
}
