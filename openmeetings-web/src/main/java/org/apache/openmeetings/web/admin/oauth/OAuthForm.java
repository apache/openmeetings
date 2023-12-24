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

import static org.apache.openmeetings.web.app.UserManager.getRedirectUri;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.web.common.confirmation.ConfirmationHelper.newOkCancelDangerConfirm;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.openmeetings.db.dao.server.OAuth2Dao;
import org.apache.openmeetings.db.entity.server.OAuthServer;
import org.apache.openmeetings.db.entity.server.OAuthServer.RequestInfoMethod;
import org.apache.openmeetings.db.entity.server.OAuthServer.RequestTokenMethod;
import org.apache.openmeetings.web.admin.AdminBaseForm;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.util.string.Strings;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome6IconType;
import jakarta.inject.Inject;

public class OAuthForm extends AdminBaseForm<OAuthServer> {
	private static final long serialVersionUID = 1L;
	private WebMarkupContainer listContainer;
	private final WebMarkupContainer attrsContainer = new WebMarkupContainer("attrsContainer");
	private TextField<String> redirectUriText;
	private final ListView<Map.Entry<String, String>> mappingView = new ListView<>("mapping", new ListModel<>(new ArrayList<>())) {
		private static final long serialVersionUID = 1L;

		@Override
		protected void populateItem(final ListItem<Map.Entry<String, String>> item) {
			final Map.Entry<String, String> entry = item.getModelObject();
			BootstrapAjaxLink<String> del = new BootstrapAjaxLink<>("delete", Buttons.Type.Outline_Danger) {
				private static final long serialVersionUID = 1L;

				@Override
				public void onClick(AjaxRequestTarget target) {
					OAuthServer s = OAuthForm.this.getModelObject();
					s.getMapping().remove(entry.getKey());
					updateMapping();
					target.add(attrsContainer);
				}
			};
			del.setIconType(FontAwesome6IconType.xmark_s)
					.add(newOkCancelDangerConfirm(this, getString("833")));
			item.add(new Label("key", Model.of(entry.getKey())))
				.add(new Label("value", Model.of(entry.getValue())))
				.add(del);
		}
	};

	@Inject
	private OAuth2Dao oauthDao;

	public OAuthForm(String id, WebMarkupContainer listContainer, OAuthServer server) {
		super(id, new CompoundPropertyModel<>(server));
		this.listContainer = listContainer;
		setOutputMarkupId(true);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(new CheckBox("isEnabled"));
		add(new RequiredTextField<String>("name").setLabel(new ResourceModel("165")));
		add(new TextField<String>("iconUrl").setLabel(new ResourceModel("1575")));
		add(new RequiredTextField<String>("clientId").setLabel(Model.of("client_id")));
		add(new RequiredTextField<String>("clientSecret").setLabel(Model.of("client_secret")));
		add(redirectUriText = (TextField<String>) new TextField<>("redirectUri", Model.of("")).setLabel(new ResourceModel("1587")));
		add(new RequiredTextField<String>("requestKeyUrl").setLabel(new ResourceModel("1578")));
		add(new DropDownChoice<>("requestTokenMethod", List.of(RequestTokenMethod.values()), new ChoiceRenderer<>("name", "name")));
		add(new RequiredTextField<String>("requestTokenUrl").setLabel(new ResourceModel("1579")));
		add(new RequiredTextField<String>("requestTokenAttributes").setLabel(new ResourceModel("1586")));
		add(new RequiredTextField<String>("requestInfoUrl").setLabel(new ResourceModel("1580")));
		add(new DropDownChoice<>("requestInfoMethod", List.of(RequestInfoMethod.values()), new ChoiceRenderer<>("name", "name")));
		Form<Void> mappingForm = new Form<>("mappingForm");
		final TextField<String> omAttr = new TextField<>("omAttr", Model.of(""));
		final TextField<String> oauthAttr = new TextField<>("oauthAttr", Model.of(""));
		add(mappingForm.add(omAttr, oauthAttr
				, new BootstrapAjaxButton("addMapping", new ResourceModel("1261"), mappingForm, Buttons.Type.Outline_Primary) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onSubmit(AjaxRequestTarget target) {
						if (Strings.isEmpty(omAttr.getModelObject()) || Strings.isEmpty(oauthAttr.getModelObject())) {
							return;
						}
						OAuthServer s = OAuthForm.this.getModelObject();
						s.addMapping(omAttr.getModelObject(), oauthAttr.getModelObject());
						updateMapping();
						target.add(attrsContainer, mappingForm);
					}
				}).setOutputMarkupId(true));
		add(attrsContainer.add(updateMapping()).setOutputMarkupId(true));
		setNewRecordVisible(true);
	}

	private Component updateMapping() {
		mappingView.setModelObject(getModelObject().getMapping().entrySet()
				.stream()
				.map(SimpleEntry::new)
				.map(e -> (Entry<String, String>)e)
				.toList());
		return mappingView;
	}

	@Override
	protected void onModelChanged() {
		super.onModelChanged();
		redirectUriText.setModelObject(getRedirectUri(getModelObject()));
		updateMapping();
	}

	@Override
	protected void onSaveSubmit(AjaxRequestTarget target, Form<?> form) {
		oauthDao.update(getModelObject(), getUserId());
		OAuthServer oauthServer = oauthDao.get(getModelObject().getId());
		this.setModelObject(oauthServer);
		setNewRecordVisible(false);
		target.add(this);
		target.add(listContainer);
	}

	@Override
	protected void onNewSubmit(AjaxRequestTarget target, Form<?> form) {
		this.setModelObject(new OAuthServer());
		target.add(this);
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
	}

	@Override
	protected void onDeleteSubmit(AjaxRequestTarget target, Form<?> form) {
		oauthDao.delete(getModelObject(), getUserId());
		this.setModelObject(new OAuthServer());
		target.add(listContainer);
		target.add(this);
	}
}
