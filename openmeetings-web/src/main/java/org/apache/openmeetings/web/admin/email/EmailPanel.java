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
package org.apache.openmeetings.web.admin.email;

import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_CLASS;

import org.apache.openmeetings.db.dao.basic.MailMessageDao;
import org.apache.openmeetings.db.entity.basic.MailMessage;
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

public class EmailPanel extends AdminBasePanel {
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer list = new WebMarkupContainer("list");
	private final EmailForm form;

	public EmailPanel(String id) {
		super(id);
		SearchableDataView<MailMessage> dataView = new SearchableDataView<MailMessage>("email",
				new SearchableDataProvider<>(MailMessageDao.class))
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<MailMessage> item) {
				final MailMessage m = item.getModelObject();
				item.add(new Label("id"));
				item.add(new Label("status", getString("admin.email.status." + m.getStatus().name())));
				item.add(new Label("subject"));
				item.add(new AjaxEventBehavior(EVT_CLICK) {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onEvent(AjaxRequestTarget target) {
						form.setModelObject(m);
						target.add(form, list);
					}
				});
				item.add(AttributeModifier.replace(ATTR_CLASS, getRowClass(m)));
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
		DataViewContainer<MailMessage> container = new DataViewContainer<>(list, dataView, navigator);
		container.addLink(new OmOrderByBorder<>("orderById", "id", container))
				.addLink(new OmOrderByBorder<>("orderBySubject", "subject", container))
				.addLink(new OmOrderByBorder<>("orderByStatus", "status", container));
		add(container.getLinks());
		add(navigator);

		form = new EmailForm("form", list, new MailMessage());
		add(form);
	}

	private StringBuilder getRowClass(final MailMessage m) {
		StringBuilder sb = getRowClass(m.getId(), form.getModelObject().getId());
		if (MailMessage.Status.ERROR == m.getStatus()) {
			sb.append(" ui-state-error");
		}
		return sb;
	}
}
