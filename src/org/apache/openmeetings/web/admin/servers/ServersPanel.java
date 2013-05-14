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

import org.apache.openmeetings.data.basic.dao.ServerDao;
import org.apache.openmeetings.persistence.beans.basic.Server;
import org.apache.openmeetings.web.admin.AdminPanel;
import org.apache.openmeetings.web.admin.PagedEntityListPanel;
import org.apache.openmeetings.web.admin.SearchableDataView;
import org.apache.openmeetings.web.data.DataViewContainer;
import org.apache.openmeetings.web.data.SearchableDataProvider;
import org.apache.openmeetings.web.data.OrderByBorder;
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
	private static final long serialVersionUID = -2197334608577346569L;
	private ServerForm form;
	
	@Override
	public void onMenuPanelLoad(AjaxRequestTarget target) {
		target.appendJavaScript("omServerPanelInit();");
	}

	@SuppressWarnings("unchecked")
	public ServersPanel(String id) {
		super(id);
		SearchableDataView<Server> dataView = new SearchableDataView<Server>("serverList",
				new SearchableDataProvider<Server>(ServerDao.class)) {
			private static final long serialVersionUID = 8715559628755439596L;

			@Override
			protected void populateItem(final Item<Server> item) {
				final Server Server = item.getModelObject();
				item.add(new Label("id", "" + Server.getId()));
				item.add(new Label("name", "" + Server.getName()));
				item.add(new Label("address", "" + Server.getAddress()));
				item.add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = -8069413566800571061L;

					protected void onEvent(AjaxRequestTarget target) {
						form.setModelObject(Server);
						form.hideNewRecord();
						target.add(form);
						target.appendJavaScript("omServerPanelInit();");
					}
				});
				item.add(AttributeModifier.replace("class", (item.getIndex() % 2 == 1) ? "even" : "odd"));
			}
		};
		
		final WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");
		add(listContainer.add(dataView).setOutputMarkupId(true));
		DataViewContainer<Server> container = new DataViewContainer<Server>(listContainer, dataView);
		container.setLinks(new OrderByBorder<Server>("orderById", "id", container)
				, new OrderByBorder<Server>("orderByName", "name", container)
				, new OrderByBorder<Server>("orderByAddress", "address", container));
		add(container.orderLinks);
		add(new PagedEntityListPanel("navigator", dataView) {
			private static final long serialVersionUID = 5097048616003411362L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				target.add(listContainer);
			}
		});
		
		form = new ServerForm("form", listContainer, new Server());
		form.showNewRecord();
        add(form);
		
	}
}
