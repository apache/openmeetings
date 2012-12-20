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
package org.apache.openmeetings.web.components.admin;

import java.util.Arrays;
import java.util.List;

import org.apache.openmeetings.persistence.beans.IDataProviderEntity;
import org.apache.openmeetings.web.data.SearchableDataProvider;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;

public abstract class PagedEntityListPanel extends AdminPanel {
	private static final long serialVersionUID = -4280843184916302671L;
	private int entitiesPerPage = 50;
	private List<Integer> numbers = Arrays.asList(10, 25, 50, 75, 100, 200);
	
	public PagedEntityListPanel(String id, final SearchableDataView<? extends IDataProviderEntity> dataView) {
		super(id);
		
		dataView.setItemsPerPage(entitiesPerPage);
		final Form<Void> f = new Form<Void>("pagingForm");

		f.add(new AdminPagingNavigator("navigator", dataView).setOutputMarkupId(true))
			.add(new DropDownChoice<Integer>("entitiesPerPage", new PropertyModel<Integer>(this, "entitiesPerPage"), numbers)
				.add(new AjaxFormComponentUpdatingBehavior("onchange") {
					private static final long serialVersionUID = -7754441983330112248L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						long newPage = dataView.getCurrentPage() * dataView.getItemsPerPage() / entitiesPerPage;
						dataView.setItemsPerPage(entitiesPerPage);
						dataView.setCurrentPage(newPage);
						target.add(f);
						PagedEntityListPanel.this.onEvent(target);
					}
				}));
		
		final SearchableDataProvider<? extends IDataProviderEntity> dp = dataView.getDataProvider();
		Form<Void> searchForm = new Form<Void>("searchForm");
		add(searchForm.setOutputMarkupId(true));
		searchForm.add(new TextField<String>("searchText", new PropertyModel<String>(dp, "search")).setOutputMarkupId(true));
		searchForm.add(new AjaxButton("search", searchForm) {
			private static final long serialVersionUID = -1659023337945692814L;

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				target.add(f);
				PagedEntityListPanel.this.onEvent(target);
			}
		});
		add(f.setOutputMarkupId(true));
	}

	protected abstract void onEvent(AjaxRequestTarget target);
}
