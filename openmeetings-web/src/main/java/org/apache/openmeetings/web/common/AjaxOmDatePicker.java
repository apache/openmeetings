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

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.extensions.markup.html.form.datetime.LocalDateTextField;
import org.apache.wicket.markup.html.form.FormComponent;

public class AjaxOmDatePicker extends AbstractOmDateTimePicker<LocalDate> {
	private static final long serialVersionUID = 1L;

	public AjaxOmDatePicker(String id) {
		super(id, null, getDateFormat());
	}

	@Override
	protected FormComponent<LocalDate> newInput(String wicketId, String dateFormat) {
		LocalDateTextField input = new LocalDateTextField(wicketId, getModel(), dateFormat);
		input.add(new OnChangeAjaxBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				onValueChanged(target);
			}
		});
		return input;
	}

	protected void onValueChanged(IPartialPageRequestHandler target) {
		//no-op
	}
}
