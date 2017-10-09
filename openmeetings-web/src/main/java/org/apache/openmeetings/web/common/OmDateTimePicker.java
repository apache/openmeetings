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
package org.apache.openmeetings.web.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;

import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.model.IModel;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.kendo.ui.form.datetime.local.DatePicker;
import com.googlecode.wicket.kendo.ui.form.datetime.local.DateTimePicker;
import com.googlecode.wicket.kendo.ui.form.datetime.local.TimePicker;

public class OmDateTimePicker extends DateTimePicker {
	private static final long serialVersionUID = 1L;

	public OmDateTimePicker(String id, IModel<LocalDateTime> model) {
		super(id, model, WebSession.get().getLocale());
	}

	@Override
	protected DatePicker newDatePicker(String id, IModel<LocalDate> model, Locale locale, String datePattern, Options options) {
		DatePicker dp = super.newDatePicker(id, model, locale, datePattern, options);
		dp.setLabel(getLabel());
		return dp;
	}

	@Override
	protected TimePicker newTimePicker(String id, IModel<LocalTime> model, Locale locale, String timePattern, Options options) {
		TimePicker tp = super.newTimePicker(id, model, locale, timePattern, options);
		tp.setLabel(getLabel());
		return tp;
	}
	//TODO render KendoCultureHeaderItem as soon as localized AM/PM will be correctly handled
}
