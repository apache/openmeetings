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
package org.apache.openmeetings.web.admin.extra;

import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_CLASS;

import org.apache.openmeetings.db.dao.room.ExtraMenuDao;
import org.apache.openmeetings.db.entity.room.ExtraMenu;
import org.apache.openmeetings.web.admin.AdminBasePanel;
import org.apache.openmeetings.web.admin.SearchableDataView;
import org.apache.openmeetings.web.common.PagedEntityListPanel;
import org.apache.openmeetings.web.data.DataViewContainer;
import org.apache.openmeetings.web.data.OmOrderByBorder;
import org.apache.openmeetings.web.data.SearchableGroupAdminDataProvider;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;

@AuthorizeInstantiation({"ADMIN", "GROUP_ADMIN"})
public class ExtraPanel extends AdminBasePanel {
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer list = new WebMarkupContainer("list");
	private ExtraForm form;

	public ExtraPanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		SearchableDataView<ExtraMenu> dataView = new SearchableDataView<>("menu",
				new SearchableGroupAdminDataProvider<>(ExtraMenuDao.class))
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<ExtraMenu> item) {
				final ExtraMenu m = item.getModelObject();
				item.add(new Label("id"));
				item.add(new Label("name"));
				item.add(new Label("link"));
				item.add(AjaxEventBehavior.onEvent(EVT_CLICK, target -> {
					form.setModelObject(m);
					target.add(form, list);
				}));
				item.add(AttributeModifier.replace(ATTR_CLASS, getRowClass(m.getId(), form.getModelObject().getId())));
			}
		};
		add(list.add(dataView).setOutputMarkupId(true));
		final PagedEntityListPanel navigator = new PagedEntityListPanel("navigator", dataView) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				target.add(list);
			}
		};
		DataViewContainer<ExtraMenu> container = new DataViewContainer<>(list, dataView, navigator);
		container.addLink(new OmOrderByBorder<>("orderById", "id", container))
				.addLink(new OmOrderByBorder<>("orderByName", "name", container))
				.addLink(new OmOrderByBorder<>("orderByLink", "link", container));
		add(container.getLinks());
		add(navigator);

		form = new ExtraForm("form", list, new ExtraMenu());
		add(form);
		super.onInitialize();
	}
}
