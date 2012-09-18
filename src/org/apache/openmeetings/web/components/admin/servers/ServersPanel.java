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
package org.apache.openmeetings.web.components.admin.servers;

import java.util.Iterator;

import org.apache.openmeetings.data.basic.dao.ServerDaoImpl;
import org.apache.openmeetings.persistence.beans.basic.Server;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.components.admin.AdminPanel;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class ServersPanel extends AdminPanel {

	private static final long serialVersionUID = -1L;
	private ServerForm form;
	
	public ServersPanel(String id) {
		super(id);
		DataView<Server> dataView = new DataView<Server>("serverList", new IDataProvider<Server>(){
			private static final long serialVersionUID = -1L;

			public void detach() {
				//empty
			}

			public Iterator<? extends Server> iterator(long first, long count) {
				return Application.getBean(ServerDaoImpl.class).getServerList((int)first, (int)count).iterator();
			}

			public long size() {
				return Application.getBean(ServerDaoImpl.class).getServerCount();
			}

			public IModel<Server> model(Server object) {
				return new CompoundPropertyModel<Server>(object);
			}
			
		}) {
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
						target.add(form);
					}
				});
				item.add(AttributeModifier.replace("class", (item.getIndex() % 2 == 1) ? "even" : "odd"));
			}
		};
		dataView.setItemsPerPage(8); //FIXME need to be parametrized
		add(dataView);
		add(new AjaxPagingNavigator("navigator", dataView));
		
		Server Server = new Server();
		form = new ServerForm("form", Server);
        add(form);
		
	}
}
