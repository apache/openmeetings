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

import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.PropertyModel;

public abstract class PagingNavigatorPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private int entitiesPerPage;

	public PagingNavigatorPanel(String id, final DataView<?> dataView, List<Integer> numbers) {
		this(id, dataView, numbers, 50);
	}

	public PagingNavigatorPanel(String id, final DataView<?> dataView, List<Integer> numbers, int _entitiesPerPage) {
		super(id);
		setOutputMarkupId(true);
		this.entitiesPerPage = _entitiesPerPage;
		dataView.setItemsPerPage(entitiesPerPage);
		final Form<Void> f = new Form<>("pagingForm");
		f.add(new OmPagingNavigator("navigator", dataView).setOutputMarkupId(true))
			.add(new DropDownChoice<>("entitiesPerPage", new PropertyModel<Integer>(this, "entitiesPerPage"), numbers)
				.add(new AjaxFormComponentUpdatingBehavior("change") {
					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						long newPage = dataView.getCurrentPage() * dataView.getItemsPerPage() / entitiesPerPage;
						dataView.setItemsPerPage(entitiesPerPage);
						dataView.setCurrentPage(newPage);
						target.add(f);
						PagingNavigatorPanel.this.onEvent(target);
					}
				}));
		add(f.setOutputMarkupId(true));
	}

	public int getEntitiesPerPage() {
		return entitiesPerPage;
	}

	public void setEntitiesPerPage(int entitiesPerPage) {
		this.entitiesPerPage = entitiesPerPage;
	}

	protected abstract void onEvent(AjaxRequestTarget target);
}
