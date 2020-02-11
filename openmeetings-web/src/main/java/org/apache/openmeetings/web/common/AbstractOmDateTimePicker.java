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

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.model.IModel;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.AbstractDateTimePickerWithIcon;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePickerConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePickerIconConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType;

public abstract class AbstractOmDateTimePicker<T> extends AbstractDateTimePickerWithIcon<T> {
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_DATE_FORMAT = "yyyy/MM/dd";
	private static final String DEFAULT_DATE_TIME_FORMAT = DEFAULT_DATE_FORMAT + " HH:mm:ss";

	public AbstractOmDateTimePicker(String id, IModel<T> model) {
		this(id, model, getDateTimeFormat());
	}

	public AbstractOmDateTimePicker(String id, IModel<T> model, String format) {
		super(id, model, new DatetimePickerConfig()
				.useLocale(WebSession.get().getLocale().toLanguageTag())
				.withFormat(format)
				.with(new DatetimePickerIconConfig()
						.useDateIcon(FontAwesome5IconType.calendar_s)
						.useTimeIcon(FontAwesome5IconType.clock_s)
						.useUpIcon(FontAwesome5IconType.arrow_up_s)
						.useDownIcon(FontAwesome5IconType.arrow_down_s)
						.usePreviousIcon(FontAwesome5IconType.arrow_left_s)
						.useNextIcon(FontAwesome5IconType.arrow_right_s)
						.useTodayIcon(FontAwesome5IconType.calendar_check_s)
						.useClearIcon(FontAwesome5IconType.eraser_s)
						.useCloseIcon(FontAwesome5IconType.times_s))
				);
		setRenderBodyOnly(false);
	}

	public static String getDateTimeFormat() {
		DateFormat fmt = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, WebSession.get().getLocale());
		if (fmt instanceof SimpleDateFormat) {
			return ((SimpleDateFormat)fmt).toPattern();
		}
		return DEFAULT_DATE_TIME_FORMAT;
	}

	public static String getDateFormat() {
		DateFormat fmt = DateFormat.getDateInstance(DateFormat.SHORT, WebSession.get().getLocale());
		if (fmt instanceof SimpleDateFormat) {
			return ((SimpleDateFormat)fmt).toPattern();
		}
		return DEFAULT_DATE_FORMAT;
	}
}
