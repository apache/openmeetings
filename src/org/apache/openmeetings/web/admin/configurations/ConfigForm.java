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
package org.apache.openmeetings.web.admin.configurations;

import static org.apache.openmeetings.OpenmeetingsVariables.WEB_DATE_PATTERN;
import static org.apache.wicket.datetime.markup.html.basic.DateLabel.forDatePattern;

import org.apache.openmeetings.data.basic.dao.ConfigurationDao;
import org.apache.openmeetings.persistence.beans.basic.Configuration;
import org.apache.openmeetings.web.admin.AdminBaseForm;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;

/**
 * Handle {@link Configuration} items as list and form
 * 
 * @author swagner
 * 
 */
public class ConfigForm extends AdminBaseForm<Configuration> {

	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer listContainer;

	private ConfigurationDao getConfigDao() {
		return Application.getBean(ConfigurationDao.class);
	}
	
	private void refresh(AjaxRequestTarget target) {
		target.add(this);
		target.appendJavaScript("omConfigPanelInit();");
	}
	
	public ConfigForm(String id, WebMarkupContainer listContainer, Configuration configuration) {
		super(id, new CompoundPropertyModel<Configuration>(configuration));
		setOutputMarkupId(true);
		this.listContainer = listContainer;
		add(new RequiredTextField<String>("conf_key").setLabel(Model.of(WebSession.getString(267))).add(new IValidator<String>(){
			private static final long serialVersionUID = -3371792361118941958L;

			public void validate(IValidatable<String> validatable) {
				Configuration c = getConfigDao().forceGet(validatable.getValue());
				if (c != null && !c.getConfiguration_id().equals(ConfigForm.this.getModelObject().getConfiguration_id())) {
					error(WebSession.getString(1544L));
				}
			}
		}));
		add(new RequiredTextField<String>("conf_value").setLabel(Model.of(WebSession.getString(271))));
		add(forDatePattern("updatetime", WEB_DATE_PATTERN));
		add(new Label("user.login"));
		add(new TextArea<String>("comment"));
		
		// attach an ajax validation behavior to all form component's keydown
		// event and throttle it down to once per second
		AjaxFormValidatingBehavior.addToAllFormComponents(this, "keydown", Duration.ONE_SECOND);
	}
	
	@Override
	protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
		this.setModelObject(getConfigDao().update(getModelObject(), WebSession.getUserId()));
		hideNewRecord();
		target.add(listContainer);
		refresh(target);
	}

	@Override
	protected void onNewSubmit(AjaxRequestTarget target, Form<?> form) {
		this.setModelObject(new Configuration());
		refresh(target);
	}
	
	@Override
	protected void onRefreshSubmit(AjaxRequestTarget target, Form<?> form) {
		Configuration conf = this.getModelObject();
		if (conf.getConfiguration_id() != null) {
			conf = getConfigDao().get(conf.getConfiguration_id());
		} else {
			conf = new Configuration();
		}
		this.setModelObject(conf);
		refresh(target);
	}
	
	@Override
	protected void onDeleteSubmit(AjaxRequestTarget target, Form<?> form) {
		Application.getBean(ConfigurationDao.class).delete(this.getModelObject(), WebSession.getUserId());
		this.setModelObject(new Configuration());
		target.add(listContainer);
		refresh(target);
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
