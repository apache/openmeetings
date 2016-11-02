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

import java.util.TimeZone;

import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.service.mail.template.AbstractTemplatePanel;
import org.apache.openmeetings.service.mail.template.DashOmTextLabel;
import org.apache.openmeetings.service.mail.template.OmTextLabel;
import org.apache.openmeetings.util.CalendarPatterns;
import org.apache.wicket.core.util.string.ComponentRenderer;
import org.apache.wicket.markup.html.panel.Fragment;

public abstract class AbstractSubjectEmailTemplate extends AbstractTemplatePanel {
	private static final long serialVersionUID = 1L;
	protected Appointment a;
	protected TimeZone tz;
	private String email = null;
	private String subject = null;


	public AbstractSubjectEmailTemplate(Long langId, Appointment a, TimeZone tz) {
		super(langId);
		this.a = a;
		this.tz = tz;
	}

	abstract void omInit();

	@Override
	protected void onInitialize() {
		super.onInitialize();
		omInit();
		email = ComponentRenderer.renderComponent(this).toString();
		subject = ComponentRenderer.renderComponent(getSubjectFragment()).toString();
	}

	public final String getEmail() {
		return email;
	}

	abstract String getPrefix();

	Fragment getSubjectFragment() {
		Fragment f = new Fragment(COMP_ID, "subject", this);
		f.add(new OmTextLabel("prefix", getPrefix())
				, new OmTextLabel("title", a.getTitle())
				, new OmTextLabel("start", CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(a.getStart(), tz))
				, new DashOmTextLabel("dash")
				, new OmTextLabel("end", CalendarPatterns.getDateWithTimeByMiliSecondsAndTimeZone(a.getEnd(), tz))
				);
		return f;
	}

	public final String getSubject() {
		return subject;
	}
}
