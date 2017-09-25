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
/**
 * @author Sebastien Briquet
 */
package org.apache.openmeetings.web.user.calendar;

import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.ResourceReference;
import org.apache.wicket.resource.TextTemplateResourceReference;
import org.apache.wicket.util.collections.MicroMap;

public class CalendarFunctionsBehavior extends Behavior {
	private static final long serialVersionUID = 1L;
	private final String markupId;

	public CalendarFunctionsBehavior(String markupId) {
		this.markupId = markupId;
	}

	private IModel<Map<String, Object>> newResourceModel() {
		return Model.ofMap(new MicroMap<String, Object>("markupId", this.markupId));
	}

	private ResourceReference newResourceReference() {
		return new TextTemplateResourceReference(CalendarFunctionsBehavior.class, "calendar-functions.js", this.newResourceModel());
	}

	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		super.renderHead(component, response);
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(this.newResourceReference(), "calendar-functions")));
	}
}
