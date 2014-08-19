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
package org.apache.openmeetings.web.admin.ldaps;

import static org.apache.openmeetings.util.OpenmeetingsVariables.WEB_DATE_PATTERN;
import static org.apache.wicket.datetime.markup.html.basic.DateLabel.forDatePattern;

import org.apache.openmeetings.db.dao.server.LdapConfigDao;
import org.apache.openmeetings.db.entity.server.LdapConfig;
import org.apache.openmeetings.web.admin.AdminBaseForm;
import static org.apache.openmeetings.web.app.Application.getBean;
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
 * Form components to insert/update/delete {@link LdapConfig}
 * 
 * @author swagner
 * 
 */
public class LdapForm extends AdminBaseForm<LdapConfig> {
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer listContainer;

	public LdapForm(String id, WebMarkupContainer listContainer, final LdapConfig ldapConfig) {
		super(id, new CompoundPropertyModel<LdapConfig>(ldapConfig));
		setOutputMarkupId(true);
		this.listContainer = listContainer;
		
		add(new RequiredTextField<String>("name").setLabel(Model.of(WebSession.getString(1108))));
		add(new CheckBox("active"));
		add(forDatePattern("inserted", WEB_DATE_PATTERN));
		add(new Label("insertedby.login"));
		add(forDatePattern("updated", WEB_DATE_PATTERN));
		add(new Label("updatedby.login"));
		add(new RequiredTextField<String>("configFileName").setLabel(Model.of(WebSession.getString(1115))));
		add(new CheckBox("addDomainToUserName"));
		add(new TextField<String>("domain"));
		add(new TextArea<String>("comment"));

		// attach an ajax validation behavior to all form component's keydown
		// event and throttle it down to once per second
		add(new AjaxFormValidatingBehavior("keydown", Duration.ONE_SECOND));
	}

	@Override
	protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
		getBean(LdapConfigDao.class).update(getModelObject(), WebSession.getUserId());
		LdapConfig ldapConfig = getBean(LdapConfigDao.class).get(getModelObject().getId());
		this.setModelObject(ldapConfig);
		hideNewRecord();
		target.add(this);
		target.add(listContainer);
		target.appendJavaScript("omLdapPanelInit();");
	}

	@Override
	protected void onNewSubmit(AjaxRequestTarget target, Form<?> form) {
		this.setModelObject(new LdapConfig());
		target.add(this);
		target.appendJavaScript("omLdapPanelInit();");
	}

	@Override
	protected void onRefreshSubmit(AjaxRequestTarget target, Form<?> form) {
		LdapConfig ldapConfig = this.getModelObject();
		if (ldapConfig.getId() <= 0) {
			ldapConfig = getBean(LdapConfigDao.class).get(ldapConfig.getId());
		} else {
			ldapConfig = new LdapConfig();
		}
		this.setModelObject(ldapConfig);
		target.add(this);
		target.appendJavaScript("omLdapPanelInit();");
	}

	@Override
	protected void onDeleteSubmit(AjaxRequestTarget target, Form<?> form) {
		getBean(LdapConfigDao.class).delete(getModelObject(), WebSession.getUserId());
		this.setModelObject(new LdapConfig());
		target.add(listContainer);
		target.add(this);
		target.appendJavaScript("omLdapPanelInit();");
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
