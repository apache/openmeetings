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
import static org.apache.wicket.ajax.attributes.CallbackParameter.resolved;

import java.util.Calendar;

import org.apache.openmeetings.data.calendar.daos.AppointmentDao;
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
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.handler.TextRequestHandler;

public class CalendarPanel extends UserPanel {

	private static final long serialVersionUID = -6536379497642291437L;
	
	@Override
	public void onMenuPanelLoad(AjaxRequestTarget target) {
		target.appendJavaScript("calendarInit();");
	}

	public CalendarPanel(String id) {
		super(id);
		WebMarkupContainer calendar = new WebMarkupContainer("calendar");
		calendar.setOutputMarkupId(true);
		calendar.setMarkupId("calendar");
		add(calendar);
		
		final CalendarForm form = new CalendarForm("appointment", new CompoundPropertyModel<Appointment>(new Appointment()));
		add(form);
		
		//fetchEvents
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
					"var fetchEventsFunc = "
						+ this.getCallbackFunction(
							context("start")
							, context("end")
							, context("callback")
							, resolved("_start", "start.getTime()")
							, resolved("_end", "end.getTime()")) + ";"
					, "fetchEventsFunc"));
			}

			@Override
			protected void respond(AjaxRequestTarget target) {
				Calendar start = WebSession.getCalendar();
				start.setTimeInMillis(getRequestCycle().getRequest().getRequestParameters().getParameterValue("_start").toLong());
				Calendar end = WebSession.getCalendar();
				end.setTimeInMillis(getRequestCycle().getRequest().getRequestParameters().getParameterValue("_end").toLong());
				JSONArray events = new JSONArray();
				for (Appointment a : Application.getBean(AppointmentDao.class).getAppointmentsByRange(WebSession.getUserId(),
						start.getTime(), end.getTime())) {
					try {
						events.put(new JSONObject()
							.put("id", a.getAppointmentId())
							.put("title", a.getAppointmentName())
							.put("description", a.getAppointmentDescription())
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
		//dropEvent
		add(new DropResizeBehavior(true, "dropEventFunc"));
		//resizeEvent
		add(new DropResizeBehavior(false, "resizeEventFunc"));
		//create on click-and-drag
		add(new AbstractDefaultAjaxBehavior() {
			private static final long serialVersionUID = 6880514947331946407L;

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				attributes.getAjaxCallListeners().add(new AjaxCallListener().onSuccess("$('#appointment').dialog('open');"));
			}
			
			@Override
			public void renderHead(Component component, IHeaderResponse response) {
				super.renderHead(component, response);
				response.render(JavaScriptHeaderItem.forScript(
					"var selectFunc = "
						+ this.getCallbackFunction(
							context("start")
							, context("end")
							, context("allDay")
							, context("jsEvent")
							, context("view")
							, resolved("_start", "start.getTime()")
							, resolved("_end", "end.getTime()")) + ";"
					, "selectFunc"));
			}

			@Override
			protected void respond(AjaxRequestTarget target) {
				Calendar start = WebSession.getCalendar();
				start.setTimeInMillis(getRequestCycle().getRequest().getRequestParameters().getParameterValue("_start").toLong());
				Calendar end = WebSession.getCalendar();
				end.setTimeInMillis(getRequestCycle().getRequest().getRequestParameters().getParameterValue("_end").toLong());
				
				if (start.equals(end)) {
					end.add(Calendar.HOUR_OF_DAY, 1);
				}
				Appointment a = new Appointment();
				a.setAppointmentStarttime(start.getTime());
				a.setAppointmentEndtime(end.getTime());
				form.setModelObject(a);
				
				target.add(form);
			}
		});
		//eventClick
		add(new AbstractDefaultAjaxBehavior() {
			private static final long serialVersionUID = 6880514947331946407L;

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				attributes.getAjaxCallListeners().add(new AjaxCallListener().onSuccess("$('#appointment').dialog('open');"));
			}
			
			@Override
			public void renderHead(Component component, IHeaderResponse response) {
				super.renderHead(component, response);
				response.render(JavaScriptHeaderItem.forScript(
					"var eventClickFunc = "
						+ this.getCallbackFunction(
							context("event")
							, context("jsEvent")
							, context("view")
							, resolved("_id", "event.id")) + ";"
					, "eventClickFunc"));
			}

			@Override
			protected void respond(AjaxRequestTarget target) {
				Appointment a = Application.getBean(AppointmentDao.class).getAppointmentById(
						getRequestCycle().getRequest().getRequestParameters().getParameterValue("_id").toLong());
				form.setModelObject(a);
				
				target.add(form);
			}
		});
	}

	private class DropResizeBehavior extends AbstractDefaultAjaxBehavior {
		private static final long serialVersionUID = -3060872155563135236L;
		private boolean drop = false;
		private String funcName;
		
		DropResizeBehavior(boolean drop, String funcName) {
			this.drop = drop;
			this.funcName = funcName;
		}
		
		@Override
		protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
			super.updateAjaxAttributes(attributes);
			attributes.getAjaxCallListeners().add(new AjaxCallListener().onFailure("revertFunc();"));
		}
		
		@Override
		public void renderHead(Component component, IHeaderResponse response) {
			super.renderHead(component, response);
			response.render(JavaScriptHeaderItem.forScript(
				"var " + funcName + " = "
					+ (drop
						? this.getCallbackFunction(
							context("event")
							, explicit("dayDelta")
							, explicit("minuteDelta")
							, context("allDay")
							, context("revertFunc")
							, context("jsEvent")
							, context("ui")
							, context("view")
							, resolved("_id", "event.id"))
						: this.getCallbackFunction(
							context("event")
							, explicit("dayDelta")
							, explicit("minuteDelta")
							, context("revertFunc")
							, context("jsEvent")
							, context("ui")
							, context("view")
							, resolved("_id", "event.id"))) + ";"
				, funcName));
		}
		
		@Override
		protected void respond(AjaxRequestTarget target) {
			AppointmentDao dao = Application.getBean(AppointmentDao.class);
			Appointment a = dao.getAppointmentById(
				getRequestCycle().getRequest().getRequestParameters().getParameterValue("_id").toLong());

			int dayDelta = getRequestCycle().getRequest().getRequestParameters().getParameterValue("dayDelta").toInt();
			int minuteDelta = getRequestCycle().getRequest().getRequestParameters().getParameterValue("minuteDelta").toInt();

			Calendar cal = WebSession.getCalendar();
			if (drop) {
				cal.setTime(a.getAppointmentStarttime());
				cal.add(Calendar.DATE, dayDelta);
				cal.add(Calendar.MINUTE, minuteDelta);
				a.setAppointmentStarttime(cal.getTime());
			}
			cal.setTime(a.getAppointmentEndtime());
			cal.add(Calendar.DATE, dayDelta);
			cal.add(Calendar.MINUTE, minuteDelta);
			a.setAppointmentEndtime(cal.getTime());
			
			dao.updateAppointment(a);
		}
	}
}
