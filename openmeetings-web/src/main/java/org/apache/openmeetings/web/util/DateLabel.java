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
package org.apache.openmeetings.web.util;

import static org.apache.commons.lang3.time.FastDateFormat.MEDIUM;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

public class DateLabel extends Label {
	private static final long serialVersionUID = 1L;
	private FastDateFormat fmt;

	public DateLabel(String id) {
		super(id);
		initFmt();
	}

	public DateLabel(String id, IModel<?> model) {
		super(id, model);
		initFmt();
	}

	private void initFmt() {
		fmt = FastDateFormat.getDateTimeInstance(MEDIUM, MEDIUM, getLocale());
	}

	@Override
	public void onComponentTagBody(final MarkupStream markupStream, final ComponentTag openTag) {
		Object o = getDefaultModelObject();
		String s = getDefaultModelObjectAsString();
		if (o == null) {
			// no-op
		} else if (o instanceof Date || o.getClass().isAssignableFrom(Date.class)) {
			s = fmt.format((Date)o);
		} else if (o instanceof Calendar || o.getClass().isAssignableFrom(Calendar.class)) {
			s = fmt.format((Calendar)o);
		}
		replaceComponentTagBody(markupStream, openTag, s);
	}
}
