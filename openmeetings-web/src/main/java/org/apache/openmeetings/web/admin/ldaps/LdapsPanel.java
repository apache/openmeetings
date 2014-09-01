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
package org.apache.openmeetings.web.admin.ldaps;

import org.apache.openmeetings.db.dao.server.LdapConfigDao;
import org.apache.openmeetings.db.entity.server.LdapConfig;
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
 * {@link AdminPanel} to list and modify {@link LdapConfig}
 * 
 * @author swagner
 * 
 */
public class LdapsPanel extends AdminPanel {
	private static final long serialVersionUID = 1L;
	final WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");
	private LdapForm form;
	
	@Override
	public void onMenuPanelLoad(AjaxRequestTarget target) {
		super.onMenuPanelLoad(target);
		target.appendJavaScript("omLdapPanelInit();");
	}

	public LdapsPanel(String id) {
		super(id);
		SearchableDataView<LdapConfig> dataView = new SearchableDataView<LdapConfig>("ldapList"
			, new SearchableDataProvider<LdapConfig>(LdapConfigDao.class)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final Item<LdapConfig> item) {
				final LdapConfig lc = item.getModelObject();
				item.add(new Label("ldapConfigId", "" + lc.getId()));
				item.add(new Label("name", "" + lc.getName()));
				item.add(new Label("configFileName", "" + lc.getConfigFileName()));
				item.add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = 1L;

					protected void onEvent(AjaxRequestTarget target) {
						form.setModelObject(lc);
						form.hideNewRecord();
						target.add(form, listContainer);
						target.appendJavaScript("omLdapPanelInit();");
					}
				});
				item.add(AttributeModifier.replace("class", "clickable ui-widget-content"
						+ (lc.getId().equals(form.getModelObject().getId()) ? " ui-state-active" : "")));
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
		DataViewContainer<LdapConfig> container = new DataViewContainer<LdapConfig>(listContainer, dataView, navigator);
		container.addLink(new OmOrderByBorder<LdapConfig>("orderById", "ldapConfigId", container))
			.addLink(new OmOrderByBorder<LdapConfig>("orderByName", "name", container))
			.addLink(new OmOrderByBorder<LdapConfig>("orderByFile", "configFileName", container));
		add(container.getLinks());
		add(navigator);
		
		form = new LdapForm("form", listContainer, new LdapConfig());
		form.showNewRecord();
        add(form);
		
	}
}
