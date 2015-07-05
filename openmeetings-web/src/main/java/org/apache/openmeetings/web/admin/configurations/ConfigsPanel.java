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
package org.apache.openmeetings.web.admin.configurations;

import org.apache.openmeetings.db.dao.basic.ConfigurationDao;
import org.apache.openmeetings.db.entity.basic.Configuration;
import org.apache.openmeetings.web.admin.AdminPanel;
import org.apache.openmeetings.web.admin.SearchableDataView;
import org.apache.openmeetings.web.common.PagedEntityListPanel;
import org.apache.openmeetings.web.data.DataViewContainer;
import org.apache.openmeetings.web.data.OmOrderByBorder;
import org.apache.openmeetings.web.data.SearchableDataProvider;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;

/**
 * add/update/delete {@link Configuration}
 * 
 * @author swagner
 * 
 */
public class ConfigsPanel extends AdminPanel {
	private static final long serialVersionUID = 1L;
	private ConfigForm form;
	private final WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");
	
	@Override
	public void onMenuPanelLoad(IPartialPageRequestHandler handler) {
		super.onMenuPanelLoad(handler);
		handler.appendJavaScript("omConfigPanelInit();");
	}

	public ConfigsPanel(String id) {
		super(id);
		
		SearchableDataView<Configuration> dataView = new SearchableDataView<Configuration>("configList"
			, new SearchableDataProvider<Configuration>(ConfigurationDao.class)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final Item<Configuration> item) {
				final Configuration c = item.getModelObject();
				item.add(new Label("id", c.getId()));
				item.add(new Label("key", c.getKey()));
				item.add(new Label("value", c.getValue()));
				item.add(new AjaxEventBehavior("onclick") {
					private static final long serialVersionUID = 1L;

					protected void onEvent(AjaxRequestTarget target) {
						form.hideNewRecord();
						form.setModelObject(c);
						target.add(form, listContainer);
						target.appendJavaScript("omConfigPanelInit();");
					}
				});
				item.add(AttributeModifier.replace("class", "clickable ui-widget-content"
						+ (c.getId().equals(form.getModelObject().getId()) ? " ui-state-default" : "")));
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
		DataViewContainer<Configuration> container = new DataViewContainer<Configuration>(listContainer, dataView, navigator);
		container.addLink(new OmOrderByBorder<Configuration>("orderById", "id", container))
			.addLink(new OmOrderByBorder<Configuration>("orderByKey", "key", container))
			.addLink(new OmOrderByBorder<Configuration>("orderByValue", "value", container));
		add(container.getLinks());
		add(navigator);
		
		form = new ConfigForm("form", listContainer, new Configuration());
		form.showNewRecord();
        add(form);
		
	}
}
