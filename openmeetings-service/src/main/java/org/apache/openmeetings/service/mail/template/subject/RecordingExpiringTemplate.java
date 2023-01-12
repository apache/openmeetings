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
import static org.apache.openmeetings.util.OpenmeetingsVariables.getApplicationName;

import org.apache.openmeetings.db.dao.room.RoomDao;
import org.apache.openmeetings.db.entity.record.Recording;
import org.apache.openmeetings.db.entity.room.Room;
import org.apache.openmeetings.db.entity.user.User;
import org.apache.openmeetings.db.util.LocaleHelper;
import org.apache.openmeetings.service.mail.template.OmTextLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;

import jakarta.inject.Inject;

public class RecordingExpiringTemplate extends SubjectEmailTemplate {
	private static final long serialVersionUID = 1L;
	private final Recording rec;
	private long remainingDays;
	private final User u;

	@Inject
	protected RoomDao roomDao;

	private RecordingExpiringTemplate(User u, final Recording rec, long remainingDays) {
		super(LocaleHelper.getLocale(u));
		this.u = u;
		this.rec = rec;
		this.remainingDays = remainingDays;
	}

	public static SubjectEmailTemplate get(User u, Recording rec, long remainingDays) {
		ensureApplication(u.getLanguageId());
		return new RecordingExpiringTemplate(u, rec, remainingDays).create();
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		final String app = getApplicationName();
		add(new Label("greetings", getString("template.recording.expiring.greetings", locale, u.getFirstname())));
		add(new Label("body", getString("template.recording.expiring.body", locale, app, String.valueOf(remainingDays))));
		add(new Label("footer", getString("template.recording.expiring.footer", locale, app)).setEscapeModelStrings(false));
	}

	@Override
	Fragment getSubjectFragment() {
		Fragment f = new Fragment(COMP_ID, "subject", this);
		Room room = roomDao.get(rec.getRoomId());
		f.add(new OmTextLabel("prefix", getString("template.recording.expiring.subj.prefix", locale))
				, new OmTextLabel("room", room == null ? null : getString("template.recording.expiring.subj.room", locale, room.getName())).setVisible(room != null)
				);
		return f;
	}
}
