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
package org.apache.openmeetings.web.components.admin.servers;

import org.apache.openmeetings.data.basic.dao.ServerDaoImpl;
import org.apache.openmeetings.persistence.beans.basic.Server;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.components.admin.AdminBaseForm;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.CompoundPropertyModel;
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

	public ServerForm(String id, WebMarkupContainer listContainer,
			final Server server) {
		super(id, new CompoundPropertyModel<Server>(server));
		setOutputMarkupId(true);
		this.listContainer = listContainer;

		add(new RequiredTextField<String>("name"));
		add(new RequiredTextField<String>("address"));
		add(DateLabel.forDatePattern("inserted", "dd.MM.yyyy HH:mm:ss"));
		add(new Label("insertedby.login"));
		add(DateLabel.forDatePattern("updated", "dd.MM.yyyy HH:mm:ss"));
		add(new Label("updatedby.login"));
		add(new TextArea<String>("comment"));

		// attach an ajax validation behavior to all form component's keydown
		// event and throttle it down to once per second
		AjaxFormValidatingBehavior.addToAllFormComponents(this, "keydown",
				Duration.ONE_SECOND);
	}

	@Override
	protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
		Application.getBean(ServerDaoImpl.class).update(getModelObject(),
				WebSession.getUserId());
		Server server = Application.getBean(ServerDaoImpl.class).get(
				getModelObject().getId());
		setModelObject(server);
		hideNewRecord();
		target.add(this);
		target.add(listContainer);
	}

	@Override
	protected void onNewSubmit(AjaxRequestTarget target, Form<?> form) {
		setModelObject(new Server());
		target.add(this);
	}

	@Override
	protected void onRefreshSubmit(AjaxRequestTarget target, Form<?> form) {
		Server server = getModelObject();
		if (server.getId() <= 0) {
			server = Application.getBean(ServerDaoImpl.class).get(
					server.getId());
		} else {
			server = new Server();
		}
		setModelObject(server);
		target.add(this);
	}

	@Override
	protected void onDeleteSubmit(AjaxRequestTarget target, Form<?> form) {
		Application.getBean(ServerDaoImpl.class).delete(getModelObject(),
				WebSession.getUserId());
		this.setModelObject(new Server());
		target.add(listContainer);
		target.add(this);
	}

}
