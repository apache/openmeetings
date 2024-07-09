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
package org.apache.openmeetings.web.common.datetime;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.markup.html.form.datetime.LocalDateTextField;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.HiddenField;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.convert.converter.LocalDateConverter;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.tempusdominus.TempusDominusConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.tempusdominus.TempusDominusDisplayConfig.ViewModeType;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.tempusdominus.TempusDominusLocalizationConfig.DateFormatType;

public class AjaxOmDatePicker extends AbstractOmDateTimePicker<LocalDate> {
	private static final long serialVersionUID = 1L;

	public AjaxOmDatePicker(String id) {
		super(id, null);
	}

	@Override
	protected TempusDominusConfig patch(TempusDominusConfig config) {
		return config
				.withClass(LocalDate.class)
				.withLocalization(cfg -> cfg.withFormat(DateFormatType.L.name()))
				.withDisplay(cfg -> cfg.withViewMode(ViewModeType.YEARS))
				.withRestrictions(cfg -> cfg.withMaxDate(LocalDate.now().minusYears(1)));
		}

	@Override
	protected HiddenField<LocalDate> newHidden(String wicketId) {
		final IConverter<?> converter = new LocalDateConverter() {
			private static final long serialVersionUID = 1L;

			@Override
			public DateTimeFormatter getDateTimeFormatter(Locale locale) {
				return DateTimeFormatter.ISO_LOCAL_DATE;
			}
		};
		HiddenField<LocalDate> date = new HiddenField<>(wicketId, LocalDate.class) {
			private static final long serialVersionUID = 1L;

			@Override
			protected IConverter<?> createConverter(Class<?> clazz) {
				if (LocalDate.class.isAssignableFrom(clazz)) {
					return converter;
				}
				return null;
			}
		};
		date.add(OnChangeAjaxBehavior.onChange(this::onValueChanged));
		return date;
	}

	@Override
	protected FormComponent<LocalDate> newInput(String wicketId, String dateFormat) {
		return new LocalDateTextField(wicketId, getModel(), dateFormat);
	}

	protected void onValueChanged(IPartialPageRequestHandler target) {
		//no-op
	}
}
