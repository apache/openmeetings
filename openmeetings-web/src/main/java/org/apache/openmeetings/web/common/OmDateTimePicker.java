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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Locale;

import org.apache.wicket.extensions.markup.html.form.datetime.LocalDateTimeTextField;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.convert.converter.LocalDateTimeConverter;

public class OmDateTimePicker extends AbstractOmDateTimePicker<LocalDateTime> {
	private static final long serialVersionUID = 1L;
	private final LocalDateTimeConverter converter;

	public OmDateTimePicker(String id, IModel<LocalDateTime> model) {
		this(id, model, getDateTimeFormat());
	}

	private OmDateTimePicker(String id, IModel<LocalDateTime> model, final String pattern) {
		super(id, model, pattern);
		this.converter = new LocalDateTimeConverter() {
			private static final long serialVersionUID = 1L;

			@Override
			public DateTimeFormatter getDateTimeFormatter(Locale locale)
			{
				return new DateTimeFormatterBuilder()
						.parseCaseInsensitive()
						.appendPattern(pattern)
						.toFormatter(Locale.ENGLISH);
			}
		};
	}

	@Override
	protected FormComponent<LocalDateTime> newInput(String wicketId, String dateFormat) {
		return new LocalDateTimeTextField(wicketId, getModel(), dateFormat) {
			private static final long serialVersionUID = 1L;

			@Override
			protected IConverter<?> createConverter(Class<?> clazz) {
				if (LocalDateTime.class.isAssignableFrom(clazz)) {
					return converter;
				}
				return null;
			}
		};
	}
}
