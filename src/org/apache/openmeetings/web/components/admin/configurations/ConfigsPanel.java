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
package org.apache.openmeetings.web.components.admin.configurations;

import java.util.Iterator;

import org.apache.openmeetings.data.basic.Configurationmanagement;
import org.apache.openmeetings.persistence.beans.basic.Configuration;
import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.components.admin.AdminPanel;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;

public class ConfigsPanel extends AdminPanel {

	private static final long serialVersionUID = -1L;
	private ConfigForm form;
	
	public ConfigsPanel(String id) {
		super(id);
		DataView<Configuration> dataView = new DataView<Configuration>("configList", new IDataProvider<Configuration>(){
			private static final long serialVersionUID = -1L;

			public void detach() {
				//empty
			}

			public Iterator<? extends Configuration> iterator(long first, long count) {
				return Application.getBean(Configurationmanagement.class).getNondeletedConfiguration((int)first, (int)count).iterator();
			}

			public long size() {
				return Application.getBean(Configurationmanagement.class).selectMaxFromConfigurations();
			}

			public IModel<Configuration> model(Configuration object) {
				return new CompoundPropertyModel<Configuration>(object);
			}
			
		}) {
			private static final long serialVersionUID = 8715559628755439596L;

			@Override
			protected void populateItem(Item<Configuration> item) {
				final Configuration configuration = item.getModelObject();
				item.add(new Label("configuration_id", "" + configuration.getConfiguration_id()));
				item.add(new Label("conf_key", "" + configuration.getConf_key()));
				item.add(new Label("conf_value", "" + configuration.getConf_value()));
				item.add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = -8069413566800571061L;

					protected void onEvent(AjaxRequestTarget target) {
						form.setModelObject(configuration);
						target.add(form);
					}
				});
			}
		};
		dataView.setItemsPerPage(8); //FIXME need to be parametrized
		add(dataView);
		add(new AjaxPagingNavigator("navigator", dataView));
		
		Configuration configuration = new Configuration();
		form = new ConfigForm("form", configuration);
        add(form);
		
	}
}
