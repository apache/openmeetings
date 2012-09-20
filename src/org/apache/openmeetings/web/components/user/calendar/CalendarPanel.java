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
package org.apache.openmeetings.web.components.user.calendar;

import static org.apache.wicket.ajax.attributes.CallbackParameter.context;
import static org.apache.wicket.ajax.attributes.CallbackParameter.resolved;

import java.util.Calendar;

import org.apache.openmeetings.data.calendar.daos.AppointmentDaoImpl;
import org.apache.openmeetings.persistence.beans.calendar.Appointment;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.components.UserPanel;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.json.JSONArray;
import org.apache.wicket.ajax.json.JSONException;
import org.apache.wicket.ajax.json.JSONObject;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.handler.TextRequestHandler;

public class CalendarPanel extends UserPanel {
	private static final long serialVersionUID = -6536379497642291437L;
	
	public CalendarPanel(String id) {
		super(id);
		setOutputMarkupId(true);
		WebMarkupContainer calendar = new WebMarkupContainer("calendar");
		calendar.setOutputMarkupId(true);
		calendar.setMarkupId("calendar");
		add(calendar);
		add(new AbstractDefaultAjaxBehavior() {
			private static final long serialVersionUID = 6880514947331946407L;

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				attributes.setDataType("json");
				attributes.setWicketAjaxResponse(false);
				attributes.getAjaxCallListeners().add(new AjaxCallListener().onSuccess("callback(data);"));
			}
			
			@Override
			public void renderHead(Component component, IHeaderResponse response) {
				super.renderHead(component, response);
				response.render(JavaScriptHeaderItem.forScript(
						"function setEventSource(){\n"
						+ "	$('#calendar').fullCalendar('addEventSource', \n"
						+ "	{\n"
						+ "		events: " + this.getCallbackFunction(context("start"), context("end"), context("callback"), resolved("_start", "start.getTime()"), resolved("_end", "end.getTime()")) + "\n"
						//+ "		, color: '#4793E6'\n"
						//+ "		, textColor: 'black'\n"
						+ "});\n"
						+ "}", "setEvtSource"));
						
			}

			@Override
			protected void respond(AjaxRequestTarget target) {
				Calendar start = WebSession.getCalendar();
				start.setTimeInMillis(getRequestCycle().getRequest().getRequestParameters().getParameterValue("_start").toLong());
				Calendar end = WebSession.getCalendar();
				end.setTimeInMillis(getRequestCycle().getRequest().getRequestParameters().getParameterValue("_end").toLong());
				JSONArray events = new JSONArray();
				for (Appointment a : Application.getBean(AppointmentDaoImpl.class).getAppointmentsByRange(WebSession.getUserId(),
						start.getTime(), end.getTime())) {
					try {
						events.put(new JSONObject()
							.put("id", a.getAppointmentId())
							.put("title", a.getAppointmentName())
							.put("start", WebSession.getDateFormat().format(a.getAppointmentStarttime()))
							.put("end", WebSession.getDateFormat().format(a.getAppointmentEndtime()))
							.put("allDay", false));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
	            getRequestCycle().scheduleRequestHandlerAfterCurrent(
	            	new TextRequestHandler("application/json", "UTF-8", events.toString()));
			}
		});
	}
}
