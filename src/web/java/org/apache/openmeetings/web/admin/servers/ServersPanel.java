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

import org.apache.openmeetings.db.dao.server.ServerDao;
import org.apache.openmeetings.db.entity.server.Server;
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

/**
 * Form component with list and form to manipulate {@link Server}
 * 
 * @author swagner
 * 
 */
public class ServersPanel extends AdminPanel {
	private static final long serialVersionUID = 1L;
	final WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");
	private ServerForm form;
	
	@Override
	public void onMenuPanelLoad(AjaxRequestTarget target) {
		super.onMenuPanelLoad(target);
		target.appendJavaScript("omServerPanelInit();");
	}

	public ServersPanel(String id) {
		super(id);
		SearchableDataView<Server> dataView = new SearchableDataView<Server>("serverList",
				new SearchableDataProvider<Server>(ServerDao.class)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final Item<Server> item) {
				final Server server = item.getModelObject();
				item.add(new Label("id", "" + server.getId()));
				item.add(new Label("name", "" + server.getName()));
				item.add(new Label("address", "" + server.getAddress()));
				item.add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = 1L;

					protected void onEvent(AjaxRequestTarget target) {
						form.setModelObject(server);
						form.hideNewRecord();
						target.add(form, listContainer);
						target.appendJavaScript("omServerPanelInit();");
					}
				});
				item.add(AttributeModifier.replace("class", "clickable ui-widget-content"
						+ (server.getId().equals(form.getModelObject().getId()) ? " ui-state-active" : "")));
			}
		};
		
		add(listContainer.add(dataView).setOutputMarkupId(true));
		PagedEntityListPanel navigator = new PagedEntityListPanel("navigator", dataView) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				target.add(listContainer);
			}
		};
		DataViewContainer<Server> container = new DataViewContainer<Server>(listContainer, dataView, navigator);
		container.addLink(new OmOrderByBorder<Server>("orderById", "id", container))
			.addLink(new OmOrderByBorder<Server>("orderByName", "name", container))
			.addLink(new OmOrderByBorder<Server>("orderByAddress", "address", container));
		add(container.getLinks());
		add(navigator);
		
		form = new ServerForm("form", listContainer, new Server());
		form.showNewRecord();
        add(form);
		
	}
}
