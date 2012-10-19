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
package org.apache.openmeetings.web.components.admin.configurations;

import org.apache.openmeetings.data.basic.dao.ConfigurationDaoImpl;
import org.apache.openmeetings.persistence.beans.basic.Configuration;
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
 * Handle {@link Configuration} items as list and form
 * 
 * @author swagner
 * 
 */
public class ConfigForm extends AdminBaseForm<Configuration> {

	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer listContainer;

	public ConfigForm(String id, WebMarkupContainer listContainer, final Configuration configuration) {
		super(id, new CompoundPropertyModel<Configuration>(configuration));
		setOutputMarkupId(true);
		this.listContainer = listContainer;
		add(new RequiredTextField<String>("conf_key"));
		add(new RequiredTextField<String>("conf_value"));
		add(DateLabel.forDatePattern("updatetime", "dd.MM.yyyy HH:mm:ss"));
		add(new Label("user.login"));
		add(new TextArea<String>("comment"));
		
		// attach an ajax validation behavior to all form component's keydown
		// event and throttle it down to once per second
		AjaxFormValidatingBehavior.addToAllFormComponents(this, "keydown",
				Duration.ONE_SECOND);

	}
	
	@Override
	protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
		Application.getBean(ConfigurationDaoImpl.class).update(getModelObject(), WebSession.getUserId());
		Configuration conf = Application.getBean(ConfigurationDaoImpl.class).get(getModelObject().getConfiguration_id());
		this.setModelObject(conf);
		hideNewRecord();
		target.add(this);
		target.add(listContainer);
		target.appendJavaScript("omConfigPanelInit();");
	}

	@Override
	protected void onNewSubmit(AjaxRequestTarget target, Form<?> form) {
		this.setModelObject(new Configuration());
		target.add(this);
		target.appendJavaScript("omConfigPanelInit();");
	}
	
	@Override
	protected void onRefreshSubmit(AjaxRequestTarget target, Form<?> form) {
		Configuration conf = this.getModelObject();
		if (conf.getConfiguration_id() != null) {
			conf = Application.getBean(ConfigurationDaoImpl.class).get(conf.getConfiguration_id());
		} else {
			conf = new Configuration();
		}
		this.setModelObject(conf);
		target.add(this);
		target.appendJavaScript("omConfigPanelInit();");
	}
	
	@Override
	protected void onDeleteSubmit(AjaxRequestTarget target, Form<?> form) {
		Application.getBean(ConfigurationDaoImpl.class).delete(this.getModelObject(), WebSession.getUserId());
		this.setModelObject(new Configuration());
		target.add(listContainer);
		target.add(this);
		target.appendJavaScript("omConfigPanelInit();");
	}
	
}
