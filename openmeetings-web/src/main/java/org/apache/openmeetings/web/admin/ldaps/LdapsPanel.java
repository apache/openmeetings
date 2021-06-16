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

import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_CLASS;

import org.apache.openmeetings.db.dao.server.LdapConfigDao;
import org.apache.openmeetings.db.entity.server.LdapConfig;
import org.apache.openmeetings.web.admin.AdminBasePanel;
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
 * {@link AdminBasePanel} to list and modify {@link LdapConfig}
 *
 * @author swagner
 *
 */
public class LdapsPanel extends AdminBasePanel {
	private static final long serialVersionUID = 1L;
	final WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");
	private LdapForm form;

	public LdapsPanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		SearchableDataView<LdapConfig> dataView = new SearchableDataView<>("ldapList"
				, new SearchableDataProvider<>(LdapConfigDao.class)) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final Item<LdapConfig> item) {
				final LdapConfig lc = item.getModelObject();
				item.add(new Label("id"));
				item.add(new Label("name"));
				item.add(new Label("configFileName"));
				item.add(AjaxEventBehavior.onEvent(EVT_CLICK, target -> {
					form.setModelObject(lc);
					form.setNewRecordVisible(false);
					target.add(form, listContainer);
				}));
				item.add(AttributeModifier.replace(ATTR_CLASS, getRowClass(lc.getId(), form.getModelObject().getId())));
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
		DataViewContainer<LdapConfig> container = new DataViewContainer<>(listContainer, dataView, navigator);
		container.addLink(new OmOrderByBorder<>("orderById", "id", container))
			.addLink(new OmOrderByBorder<>("orderByName", "name", container))
			.addLink(new OmOrderByBorder<>("orderByFile", "configFileName", container));
		add(container.getLinks());
		add(navigator);

		form = new LdapForm("form", listContainer, new LdapConfig());
		add(form);

		super.onInitialize();
	}
}
