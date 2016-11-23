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

import static org.apache.commons.lang3.time.FastDateFormat.MEDIUM;
import static org.apache.commons.lang3.time.FastDateFormat.SHORT;

import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.openmeetings.db.entity.calendar.Appointment;
import org.apache.openmeetings.service.mail.template.DashOmTextLabel;
import org.apache.openmeetings.service.mail.template.OmTextLabel;
import org.apache.wicket.markup.html.panel.Fragment;

public abstract class AbstractAppointmentTemplate extends AbstractSubjectEmailTemplate {
	private static final long serialVersionUID = 1L;
	protected Appointment a;
	protected TimeZone tz;

	public AbstractAppointmentTemplate(Locale locale, Appointment a, TimeZone tz) {
		super(locale);
		this.a = a;
		this.tz = tz;
	}

	abstract String getPrefix();

	@Override
	Fragment getSubjectFragment() {
		Fragment f = new Fragment(COMP_ID, "subject", this);
		f.add(new OmTextLabel("prefix", getPrefix())
				, new OmTextLabel("title", a.getTitle())
				, new OmTextLabel("start", format(a.getStart(), SHORT))
				, new DashOmTextLabel("dash")
				, new OmTextLabel("end", format(a.getEnd(), SHORT))
				);
		return f;
	}

	protected String format(Date d) {
		return format(d, MEDIUM);
	}

	protected String format(Date d, int fmt) {
		return FastDateFormat.getDateTimeInstance(fmt, fmt, tz, locale).format(d);
	}
}
