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
package org.apache.openmeetings.web.user.profile;

import static org.apache.openmeetings.db.util.FormatHelper.formatUser;
import static org.apache.openmeetings.web.app.WebSession.getRights;
import static org.apache.openmeetings.web.app.WebSession.getUserId;
import static org.apache.openmeetings.util.OpenmeetingsVariables.ATTR_CLASS;

import java.util.Iterator;
import java.util.Set;

import org.apache.openmeetings.db.dao.room.InvitationDao;
import org.apache.openmeetings.db.entity.room.Invitation;
import org.apache.openmeetings.db.entity.user.User.Right;
import org.apache.openmeetings.web.admin.SearchableDataView;
import org.apache.openmeetings.web.common.PagedEntityListPanel;
import org.apache.openmeetings.web.common.UserBasePanel;
import org.apache.openmeetings.web.data.DataViewContainer;
import org.apache.openmeetings.web.data.OmOrderByBorder;
import org.apache.openmeetings.web.data.SearchableDataProvider;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;

public class InvitationsPanel extends UserBasePanel {
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer list = new WebMarkupContainer("list");
	private Long selected = null;
	private final InvitationDetails form;

	public InvitationsPanel(String id) {
		super(id);
		setOutputMarkupId(true);

		SearchableDataView<Invitation> dataView = new SearchableDataView<>("invitations",
				new InvitationProvider())
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(Item<Invitation> item) {
				final Invitation i = item.getModelObject();
				item.add(new Label("id"));
				item.add(new Label("valid"/*, getString("admin.email.status." + m.getStatus().name())*/));
				item.add(new Label("invitee", formatUser(i.getInvitee())));
				item.add(AjaxEventBehavior.onEvent(EVT_CLICK, target -> {
					if (i.getId().equals(selected)) {
						selected = null;
					} else {
						selected = i.getId();
					}
					form.setModelObject(i);
					target.add(form, list);
				}));
				item.add(AttributeModifier.replace(ATTR_CLASS, getRowClass(i)));
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
		DataViewContainer<Invitation> container = new DataViewContainer<>(list, dataView, navigator);
		container.addLink(new OmOrderByBorder<>("orderById", "id", container))
				.addLink(new OmOrderByBorder<>("orderByValid", "valid", container))
				.addLink(new OmOrderByBorder<>("orderByInvitee", "invitee", container));
		add(container.getLinks());
		add(navigator);

		form = new InvitationDetails("form", list, new Invitation());
		add(form);
	}

	protected StringBuilder getRowClass(Invitation i) {
		Long id = i.getId();
		StringBuilder sb = new StringBuilder(ROW_CLASS);
		if (id != null && id.equals(selected)) {
			sb.append(" selected");
		}
		return sb;
	}

	private static class InvitationProvider extends SearchableDataProvider<Invitation> {
		private static final long serialVersionUID = 1L;
		private final Set<Right> rights;
		private final Long userId;
		public InvitationProvider() {
			super(InvitationDao.class);
			rights = getRights();
			userId = getUserId();
		}

		@Override
		protected InvitationDao getDao() {
			return (InvitationDao)super.getDao();
		}

		@Override
		public Iterator<? extends Invitation> iterator(long first, long count) {
			if (rights.contains(Right.ADMIN)) {
				return getDao().get(search, first, count, getSort()).iterator();
			} else if (rights.contains(Right.GROUP_ADMIN)) {
				return getDao().getGroup(search, first, count, userId, getSort()).iterator();
			} else {
				return getDao().getUser(search, first, count, userId, getSort()).iterator();
			}
		}

		@Override
		public long size() {
			if (rights.contains(Right.ADMIN)) {
				return getDao().count(search);
			} else if (rights.contains(Right.GROUP_ADMIN)) {
				return getDao().countGroup(search, userId);
			} else {
				return getDao().countUser(search, userId);
			}
		}
	}
}
