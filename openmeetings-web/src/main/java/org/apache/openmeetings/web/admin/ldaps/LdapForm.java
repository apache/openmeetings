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

import org.apache.openmeetings.db.dao.server.LdapConfigDao;
import org.apache.openmeetings.db.entity.server.LdapConfig;
import org.apache.openmeetings.web.admin.AdminBaseForm;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.util.DateLabel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.ResourceModel;

import jakarta.inject.Inject;

/**
 * Form components to insert/update/delete {@link LdapConfig}
 *
 * @author swagner
 *
 */
public class LdapForm extends AdminBaseForm<LdapConfig> {
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer listContainer;

	@Inject
	private LdapConfigDao ldapDao;

	public LdapForm(String id, WebMarkupContainer listContainer, final LdapConfig ldapConfig) {
		super(id, new CompoundPropertyModel<>(ldapConfig));
		setOutputMarkupId(true);
		this.listContainer = listContainer;
	}

	@Override
	protected void onInitialize() {
		add(new CheckBox("active"));
		add(new DateLabel("inserted"));
		add(new Label("insertedby.login"));
		add(new DateLabel("updated"));
		add(new Label("updatedby.login"));
		add(new CheckBox("addDomainToUserName"));
		add(new TextField<String>("domain"));
		add(new TextArea<String>("comment"));
		add(new RequiredTextField<String>("name").setLabel(new ResourceModel("165")));
		add(new RequiredTextField<String>("configFileName").setLabel(new ResourceModel("1115")));
		super.onInitialize();
		setNewRecordVisible(true);
	}

	@Override
	protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
		setModelObject(ldapDao.update(getModelObject(), WebSession.getUserId()));
		setNewRecordVisible(false);
		target.add(this, listContainer);
	}

	@Override
	protected void onNewSubmit(AjaxRequestTarget target, Form<?> form) {
		this.setModelObject(new LdapConfig());
		target.add(this);
	}

	@Override
	protected void onRefreshSubmit(AjaxRequestTarget target, Form<?> form) {
		LdapConfig ldapConfig = this.getModelObject();
		if (ldapConfig.getId() != null) {
			ldapConfig = ldapDao.get(ldapConfig.getId());
		} else {
			ldapConfig = new LdapConfig();
		}
		this.setModelObject(ldapConfig);
		target.add(this);
	}

	@Override
	protected void onDeleteSubmit(AjaxRequestTarget target, Form<?> form) {
		ldapDao.delete(getModelObject(), WebSession.getUserId());
		this.setModelObject(new LdapConfig());
		target.add(listContainer);
		target.add(this);
	}
}
