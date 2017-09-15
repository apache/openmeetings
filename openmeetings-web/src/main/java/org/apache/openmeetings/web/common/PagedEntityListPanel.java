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
package org.apache.openmeetings.web.common;

import java.util.Arrays;
import java.util.List;

import org.apache.openmeetings.db.entity.IDataProviderEntity;
import org.apache.openmeetings.web.admin.SearchableDataView;
import org.apache.openmeetings.web.data.SearchableDataProvider;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;

import com.googlecode.wicket.jquery.ui.form.button.AjaxButton;

public abstract class PagedEntityListPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private List<Integer> numbers = Arrays.asList(10, 25, 50, 75, 100, 200);

	public PagedEntityListPanel(String id, final SearchableDataView<? extends IDataProviderEntity> dataView) {
		super(id);

		final PagingNavigatorPanel navPanel = new PagingNavigatorPanel("pagedPanel", dataView, numbers) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onEvent(AjaxRequestTarget target) {
				PagedEntityListPanel.this.onEvent(target);
			}
		};

		final SearchableDataProvider<? extends IDataProviderEntity> dp = dataView.getDataProvider();
		Form<Void> searchForm = new Form<>("searchForm");
		add(searchForm.setOutputMarkupId(true));
		searchForm.add(new TextField<>("searchText", new PropertyModel<String>(dp, "search")).setOutputMarkupId(true));
		AjaxButton b = new AjaxButton("search", searchForm) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSubmit(AjaxRequestTarget target) {
				target.add(navPanel);
				PagedEntityListPanel.this.onEvent(target);
			}
		};
		searchForm.add(b);
		searchForm.setDefaultButton(b);
		add(navPanel);
	}

	protected abstract void onEvent(AjaxRequestTarget target);
}
