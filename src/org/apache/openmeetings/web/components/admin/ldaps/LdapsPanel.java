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

import org.apache.openmeetings.data.basic.dao.LdapConfigDaoImpl;
import org.apache.openmeetings.persistence.beans.basic.LdapConfig;
import org.apache.openmeetings.web.components.admin.AdminPanel;
import org.apache.openmeetings.web.components.admin.PagedEntityListPanel;
import org.apache.openmeetings.web.data.OmDataProvider;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;

/**
 * {@link AdminPanel} to list and modify {@link LdapConfig}
 * 
 * @author swagner
 * 
 */
public class LdapsPanel extends AdminPanel {

	private static final long serialVersionUID = -1L;
	private LdapForm form;
	
	public LdapsPanel(String id) {
		super(id);
		DataView<LdapConfig> dataView = new DataView<LdapConfig>("ldapList"
			, new OmDataProvider<LdapConfig>(LdapConfigDaoImpl.class)) {
			private static final long serialVersionUID = 8715559628755439596L;

			@Override
			protected void populateItem(final Item<LdapConfig> item) {
				final LdapConfig ldapConfig = item.getModelObject();
				item.add(new Label("ldapConfigId", "" + ldapConfig.getLdapConfigId()));
				item.add(new Label("name", "" + ldapConfig.getName()));
				item.add(new Label("configFileName", "" + ldapConfig.getConfigFileName()));
				item.add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = -8069413566800571061L;

					protected void onEvent(AjaxRequestTarget target) {
						form.setModelObject(ldapConfig);
						form.hideNewRecord();
						target.add(form);
					}
				});
				item.add(AttributeModifier.replace("class", (item.getIndex() % 2 == 1) ? "even" : "odd"));
			}
		};
		final WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");
		add(listContainer.add(dataView).setOutputMarkupId(true));
		add(new PagedEntityListPanel("navigator", dataView) {
			private static final long serialVersionUID = -1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				target.add(listContainer);
			}
		});
		
		form = new LdapForm("form", listContainer, new LdapConfig());
		form.showNewRecord();
        add(form);
		
	}
}
