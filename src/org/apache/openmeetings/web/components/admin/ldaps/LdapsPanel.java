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
package org.apache.openmeetings.web.components.admin.ldaps;

import java.util.Iterator;

import org.apache.openmeetings.data.basic.dao.LdapConfigDaoImpl;
import org.apache.openmeetings.persistence.beans.basic.LdapConfig;
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

public class LdapsPanel extends AdminPanel {

	private static final long serialVersionUID = -1L;
	private LdapForm form;
	
	public LdapsPanel(String id) {
		super(id);
		DataView<LdapConfig> dataView = new DataView<LdapConfig>("ldapList", new IDataProvider<LdapConfig>(){
			private static final long serialVersionUID = -1L;

			public void detach() {
				//empty
			}

			public Iterator<? extends LdapConfig> iterator(long first, long count) {
				return Application.getBean(LdapConfigDaoImpl.class).getNondeletedLdapConfig((int)first, (int)count).iterator();
			}

			public long size() {
				return Application.getBean(LdapConfigDaoImpl.class).selectMaxFromLdapConfig();
			}

			public IModel<LdapConfig> model(LdapConfig object) {
				return new CompoundPropertyModel<LdapConfig>(object);
			}
			
		}) {
			private static final long serialVersionUID = 8715559628755439596L;

			@Override
			protected void populateItem(Item<LdapConfig> item) {
				final LdapConfig ldapConfig = item.getModelObject();
				item.add(new Label("ldapConfigId", "" + ldapConfig.getLdapConfigId()));
				item.add(new Label("name", "" + ldapConfig.getName()));
				item.add(new Label("configFileName", "" + ldapConfig.getConfigFileName()));
				item.add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = -8069413566800571061L;

					protected void onEvent(AjaxRequestTarget target) {
						form.setModelObject(ldapConfig);
						target.add(form);
					}
				});
			}
		};
		dataView.setItemsPerPage(8); //FIXME need to be parametrized
		add(dataView);
		add(new AjaxPagingNavigator("navigator", dataView));
		
		LdapConfig ldapConfig = new LdapConfig();
		form = new LdapForm("form", ldapConfig);
        add(form);
		
	}
}
