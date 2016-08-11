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
package org.apache.openmeetings.web.admin.servers;

import static org.apache.openmeetings.util.OpenmeetingsVariables.WEB_DATE_PATTERN;
import static org.apache.wicket.datetime.markup.html.basic.DateLabel.forDatePattern;

import org.apache.openmeetings.db.dao.server.ServerDao;
import org.apache.openmeetings.db.entity.server.Server;
import org.apache.openmeetings.web.admin.AdminBaseForm;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;

/**
 * Form component to insert/update/delete {@link Server}
 * 
 * @author swagner
 * 
 */
public class ServerForm extends AdminBaseForm<Server> {
	private final WebMarkupContainer listContainer;
	private static final long serialVersionUID = 1L;

	public ServerForm(String id, WebMarkupContainer listContainer, final Server server) {
		super(id, new CompoundPropertyModel<Server>(server));
		setOutputMarkupId(true);
		this.listContainer = listContainer;

		add(new RequiredTextField<String>("name").setLabel(Model.of(Application.getString(1500))));
		add(new CheckBox("active"));
		add(new RequiredTextField<String>("address").setLabel(Model.of(Application.getString(1501))));
		add(new TextField<Integer>("port"));
		add(new TextField<String>("user"));
		add(new TextField<String>("pass"));
		add(new TextField<String>("webapp"));
		add(new TextField<String>("protocol"));
		add(forDatePattern("lastPing", WEB_DATE_PATTERN));
		//add(new Label("pingRunning"));
		add(forDatePattern("inserted", WEB_DATE_PATTERN));
		add(new Label("insertedby.login"));
		add(forDatePattern("updated", WEB_DATE_PATTERN));
		add(new Label("updatedby.login"));
		add(new TextArea<String>("comment"));

		// attach an ajax validation behavior to all form component's keydown
		// event and throttle it down to once per second
		add(new AjaxFormValidatingBehavior("keydown", Duration.ONE_SECOND));
	}

	@Override
	protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
		Application.getBean(ServerDao.class).update(getModelObject(), WebSession.getUserId());
		Server server = Application.getBean(ServerDao.class).get(getModelObject().getId());
		setModelObject(server);
		hideNewRecord();
		target.add(this);
		target.add(listContainer);
		target.appendJavaScript("omServerPanelInit();");
	}

	@Override
	protected void onNewSubmit(AjaxRequestTarget target, Form<?> form) {
		Server s = new Server();
		s.setWebapp("openmeetings");
		s.setProtocol("http");
		setModelObject(s);
		target.add(this);
		target.appendJavaScript("omServerPanelInit();");
	}

	@Override
	protected void onRefreshSubmit(AjaxRequestTarget target, Form<?> form) {
		Server server = getModelObject();
		if (server.getId() != null) {
			server = Application.getBean(ServerDao.class).get(server.getId());
		} else {
			server = new Server();
		}
		setModelObject(server);
		target.add(this);
		target.appendJavaScript("omServerPanelInit();");
	}

	@Override
	protected void onDeleteSubmit(AjaxRequestTarget target, Form<?> form) {
		Application.getBean(ServerDao.class).delete(getModelObject(), WebSession.getUserId());
		this.setModelObject(new Server());
		target.add(listContainer);
		target.add(this);
		target.appendJavaScript("omServerPanelInit();");
	}

	@Override
	protected void onSaveError(AjaxRequestTarget target, Form<?> form) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onNewError(AjaxRequestTarget target, Form<?> form) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onRefreshError(AjaxRequestTarget target, Form<?> form) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void onDeleteError(AjaxRequestTarget target, Form<?> form) {
		// TODO Auto-generated method stub
	}
}
