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
package org.apache.openmeetings.web.admin.oauth;

import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.pages.auth.SignInPage.getRedirectUri;

import java.util.Arrays;

import org.apache.openmeetings.db.dao.server.OAuth2Dao;
import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.apache.openmeetings.db.entity.server.OAuthServer.RequestMethod;
import org.apache.openmeetings.web.admin.AdminBaseForm;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

public class OAuthForm extends AdminBaseForm<OAuthServer> {
	private static final long serialVersionUID = 1L;
	private WebMarkupContainer listContainer;
	private TextField<String> redirectUriText;
	@SpringBean
	private OAuth2Dao oauthDao;

	public OAuthForm(String id, WebMarkupContainer listContainer, OAuthServer server) {
		super(id, new CompoundPropertyModel<>(server));
		this.listContainer = listContainer;
		setOutputMarkupId(true);

		add(new CheckBox("isEnabled"));
		add(new DropDownChoice<>("requestTokenMethod", Arrays.asList(RequestMethod.values()), new ChoiceRenderer<RequestMethod>("name", "name")));
	}

	@Override
	protected void onInitialize() {
		add(new RequiredTextField<String>("name").setLabel(Model.of(getString("165"))));
		add(new TextField<String>("iconUrl").setLabel(Model.of(getString("1575"))));
		add(new RequiredTextField<String>("clientId").setLabel(Model.of(getString("1576"))));
		add(new RequiredTextField<String>("clientSecret").setLabel(Model.of(getString("1577"))));
		add(redirectUriText = (TextField<String>) new TextField<>("redirectUri", Model.of("")).setLabel(Model.of(getString("1587"))));
		add(new RequiredTextField<String>("requestKeyUrl").setLabel(Model.of(getString("1578"))));
		add(new RequiredTextField<String>("requestTokenUrl").setLabel(Model.of(getString("1579"))));
		add(new RequiredTextField<String>("requestTokenAttributes").setLabel(Model.of(getString("1586"))));
		add(new RequiredTextField<String>("requestInfoUrl").setLabel(Model.of(getString("1580"))));
		add(new RequiredTextField<String>("loginParamName").setLabel(Model.of(getString("1582"))));
		add(new RequiredTextField<String>("emailParamName").setLabel(Model.of(getString("1583"))));
		add(new TextField<String>("firstnameParamName").setLabel(Model.of(getString("1584"))));
		add(new TextField<String>("lastnameParamName").setLabel(Model.of(getString("1585"))));
		super.onInitialize();
	}

	@Override
	protected void onModelChanged() {
		super.onModelChanged();
		redirectUriText.setModelObject(getRedirectUri(getModelObject()));
	}

	@Override
	protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
		oauthDao.update(getModelObject(), getUserId());
		OAuthServer oauthServer = oauthDao.get(getModelObject().getId());
		this.setModelObject(oauthServer);
		hideNewRecord();
		target.add(this);
		target.add(listContainer);
		reinitJs(target);
	}

	@Override
	protected void onNewSubmit(AjaxRequestTarget target, Form<?> form) {
		this.setModelObject(new OAuthServer());
		target.add(this);
		reinitJs(target);
	}

	@Override
	protected void onRefreshSubmit(AjaxRequestTarget target, Form<?> form) {
		OAuthServer server = this.getModelObject();
		if (server.getId() != null) {
			server = oauthDao.get(getModelObject().getId());
		} else {
			server = new OAuthServer();
		}
		this.setModelObject(server);
		target.add(this);
		reinitJs(target);
	}

	@Override
	protected void onDeleteSubmit(AjaxRequestTarget target, Form<?> form) {
		oauthDao.delete(getModelObject(), getUserId());
		this.setModelObject(new OAuthServer());
		target.add(listContainer);
		target.add(this);
		reinitJs(target);
	}
}
