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
import org.apache.openmeetings.web.admin.AdminPanel;
import org.apache.openmeetings.web.admin.SearchableDataView;
import org.apache.openmeetings.web.common.PagedEntityListPanel;
import org.apache.openmeetings.web.data.DataViewContainer;
import org.apache.openmeetings.web.data.OmOrderByBorder;
import org.apache.openmeetings.web.data.SearchableDataProvider;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;

public class OAuthPanel extends AdminPanel {
	private static final long serialVersionUID = -1L;
	final WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");
	private OAuthForm form;

	@SuppressWarnings("unchecked")
	public OAuthPanel(String id) {
		super(id);
		SearchableDataView<OAuthServer> dataView = new SearchableDataView<OAuthServer>("oauthServersList",
				new SearchableDataProvider<OAuthServer>(OAuth2Dao.class)) {

			private static final long serialVersionUID = -7701640237947383416L;

			@Override
			protected void populateItem(Item<OAuthServer> item) {
				final OAuthServer server = item.getModelObject();
				item.add(new Label("oauthServerId", String.valueOf(server.getId())));
				item.add(new Label("name", server.getName()));
				item.add(new AjaxEventBehavior("onclick") {

					private static final long serialVersionUID = 7380727643346943181L;

					@Override
					protected void onEvent(AjaxRequestTarget target) {
						form.setModelObject(server);
						form.hideNewRecord();
						target.add(form, listContainer);
						target.appendJavaScript("oauthPanelInit();");
					}
				});
				item.add(AttributeModifier.replace("class", "clickable "
						+ (item.getIndex() % 2 == 1 ? "even" : "odd")
						+ (server.getId().equals(form.getModelObject().getId()) ? " selected" : "")));
			}
			
		};
		
		add(listContainer.add(dataView).setOutputMarkupId(true));
		PagedEntityListPanel navigator = new PagedEntityListPanel("navigator", dataView) {
			private static final long serialVersionUID = -1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				target.add(listContainer);
			}
		};
		DataViewContainer<OAuthServer> container = new DataViewContainer<OAuthServer>(listContainer, dataView, navigator);
		container.setLinks(new OmOrderByBorder<OAuthServer>("orderById", "oauthServerId", container),
						   new OmOrderByBorder<OAuthServer>("orderByName", "name", container));
		add(container.orderLinks);
		add(navigator);
		
		form = new OAuthForm("form", listContainer, new OAuthServer());
		form.showNewRecord();
        add(form);
	}

}
