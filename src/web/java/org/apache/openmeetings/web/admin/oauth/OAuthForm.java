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

import org.apache.openmeetings.db.dao.server.OAuth2Dao;
import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.apache.openmeetings.web.admin.AdminBaseForm;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.pages.auth.SignInPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormValidatingBehavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;

public class OAuthForm extends AdminBaseForm<OAuthServer> {
	private static final long serialVersionUID = 1L;
	private WebMarkupContainer listContainer;
	private TextField<String> redirectUriText;

	public OAuthForm(String id, WebMarkupContainer listContainer, OAuthServer server) {
		super(id, new CompoundPropertyModel<OAuthServer>(server));
		this.listContainer = listContainer;
		setOutputMarkupId(true);
		
		add(new CheckBox("isEnabled"));
		add(new RequiredTextField<String>("name").setLabel(Model.of(WebSession.getString(1573))));
		add(new TextField<String>("iconUrl").setLabel(Model.of(WebSession.getString(1575))));
		add(new RequiredTextField<String>("clientId").setLabel(Model.of(WebSession.getString(1576))));
		add(new RequiredTextField<String>("clientSecret").setLabel(Model.of(WebSession.getString(1577))));
		redirectUriText = (TextField<String>) new TextField<String>("redirectUri", Model.of("")).setLabel(Model.of(WebSession.getString(1587)));
		add(redirectUriText);
		add(new RequiredTextField<String>("requestKeyUrl").setLabel(Model.of(WebSession.getString(1578))));
		add(new RequiredTextField<String>("requestTokenUrl").setLabel(Model.of(WebSession.getString(1579))));
		add(new RequiredTextField<String>("requestTokenAttributes").setLabel(Model.of(WebSession.getString(1586))));
		add(new RequiredTextField<String>("requestInfoUrl").setLabel(Model.of(WebSession.getString(1580))));
		add(new RequiredTextField<String>("loginParamName").setLabel(Model.of(WebSession.getString(1582))));
		add(new RequiredTextField<String>("emailParamName").setLabel(Model.of(WebSession.getString(1583))));
		add(new TextField<String>("firstnameParamName").setLabel(Model.of(WebSession.getString(1584))));
		add(new TextField<String>("lastnameParamName").setLabel(Model.of(WebSession.getString(1585))));
		
		// attach an ajax validation behavior to all form component's keydown
		// event and throttle it down to once per second
		AjaxFormValidatingBehavior.addToAllFormComponents(this, "keydown", Duration.ONE_SECOND);
	}
	
	@Override
	protected void onModelChanged() {
		super.onModelChanged();
		String redirectUri = SignInPage.getRedirectUri(getModelObject(), this);
		redirectUriText.setModelObject(redirectUri);
	}

	@Override
	protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
		OAuth2Dao oAuth2Dao = Application.getBean(OAuth2Dao.class);
		oAuth2Dao.update(getModelObject(), WebSession.getUserId());
		OAuthServer oauthServer = oAuth2Dao.get(getModelObject().getId());
		this.setModelObject(oauthServer);
		hideNewRecord();
		target.add(this);
		target.add(listContainer);
		target.appendJavaScript("oauthPanelInit();");
	}

	@Override
	protected void onNewSubmit(AjaxRequestTarget target, Form<?> form) {
		this.setModelObject(new OAuthServer());
		target.add(this);
		target.appendJavaScript("oauthPanelInit();");
	}

	@Override
	protected void onRefreshSubmit(AjaxRequestTarget target, Form<?> form) {
		OAuthServer server = this.getModelObject();
		if (server.getId() <= 0) {
			server = Application.getBean(OAuth2Dao.class).get(getModelObject().getId());
		} else {
			server = new OAuthServer();
		}
		this.setModelObject(server);
		target.add(this);
		target.appendJavaScript("oauthPanelInit();");
	}

	@Override
	protected void onDeleteSubmit(AjaxRequestTarget target, Form<?> form) {
		Application.getBean(OAuth2Dao.class).delete(getModelObject(), WebSession.getUserId());
		this.setModelObject(new OAuthServer());
		target.add(listContainer);
		target.add(this);
		target.appendJavaScript("oauthPanelInit();");
	}

	@Override
	protected void onSaveError(AjaxRequestTarget target, Form<?> form) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onDeleteError(AjaxRequestTarget target, Form<?> form) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onRefreshError(AjaxRequestTarget target, Form<?> form) {
		// TODO Auto-generated method stub
		
	}	
	
	@Override
	protected void onNewError(AjaxRequestTarget target, Form<?> form) {
		// TODO Auto-generated method stub
		
	}
	
}
