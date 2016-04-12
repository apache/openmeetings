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

import java.util.Locale;

import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.model.IModel;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.kendo.ui.form.datetime.local.DatePicker;
import com.googlecode.wicket.kendo.ui.form.datetime.local.DateTimePicker;
import com.googlecode.wicket.kendo.ui.form.datetime.local.TimePicker;
import com.googlecode.wicket.kendo.ui.resource.KendoCultureResourceReference;

public class OmDateTimePicker extends DateTimePicker {
	private static final long serialVersionUID = 1L;

	public OmDateTimePicker(String id, IModel<LocalDateTime> model) {
		super(id, model);
	}
	
	@Override
	protected DatePicker newDatePicker(String id, IModel<LocalDate> model, Locale locale, String datePattern, Options options) {
		return new DatePicker(id, model, WebSession.get().getLocale());
	}
	
	@Override
	protected TimePicker newTimePicker(String id, IModel<LocalTime> model, Locale locale, String timePattern, Options options) {
		return new TimePicker(id, model, WebSession.get().getLocale());
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptHeaderItem.forReference(new KendoCultureResourceReference(WebSession.get().getLocale())));
	}
}
