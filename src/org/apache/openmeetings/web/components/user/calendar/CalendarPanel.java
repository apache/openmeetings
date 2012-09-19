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
import static org.apache.wicket.ajax.attributes.CallbackParameter.explicit;

import java.text.SimpleDateFormat;
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
import org.apache.wicket.core.util.string.JavaScriptUtils;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.handler.TextRequestHandler;

public class CalendarPanel extends UserPanel {
	private static final long serialVersionUID = -6536379497642291437L;
	public static SimpleDateFormat ISO8601FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	
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
				attributes.getAjaxCallListeners().add(new AjaxCallListener() {
					private static final long serialVersionUID = -2316088040375497938L;

					public CharSequence getSuccessHandler(Component component) {
						return "callback(data);";
					}
				});
			}
			
			@Override
			public void renderHead(Component component, IHeaderResponse response) {
				super.renderHead(component, response);
				response.render(JavaScriptHeaderItem.forScript(
						"function setEventSource(){\n"
						+ "	$('#calendar').fullCalendar('addEventSource', \n"
						+ "	{\n"
						+ "		events: " + this.getCallbackFunction(explicit("start"), explicit("end"), context("callback")) + "\n"
						//+ "		, color: '#4793E6'\n"
						//+ "		, textColor: 'black'\n"
						+ "});\n"
						+ "}", "setEvtSource"));
						
			}

			@Override
			protected void respond(AjaxRequestTarget target) {
				StringBuilder sb = new StringBuilder("[");
				Calendar start = Calendar.getInstance();
				start.set(2012, 8, 1);
				Calendar end = Calendar.getInstance();
				end.set(2012, 8, 30);
				String delim = "";
				for (Appointment a : Application.getBean(AppointmentDaoImpl.class).getAppointmentsByRange(WebSession.getUserId(),
						start.getTime(), end.getTime())) {
					sb.append(delim).append("{\n")
						.append("\"id\": ").append(a.getAppointmentId()).append("\n")
						.append(", \"title\": \"").append(JavaScriptUtils.escapeQuotes(a.getAppointmentName())).append("\"\n")
						.append(", \"start\": \"").append(ISO8601FORMAT.format(a.getAppointmentStarttime())).append("\"\n")
						.append(", \"end\": \"").append(ISO8601FORMAT.format(a.getAppointmentEndtime())).append("\"\n")
						.append(", \"allDay\": false\n")
						.append("}\n");
					delim = ", ";
				}
				sb.append("]");
	            getRequestCycle().scheduleRequestHandlerAfterCurrent(
	            	new TextRequestHandler("application/json", "UTF-8", sb.toString()));
			}
		});
	}
}
