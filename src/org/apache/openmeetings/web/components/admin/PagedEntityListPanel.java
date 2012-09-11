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

import org.apache.openmeetings.persistence.beans.OmEntity;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.PropertyModel;

public abstract class PagedEntityListPanel extends AdminPanel {
	private static final long serialVersionUID = -4280843184916302671L;
	private int entityesPerPage = 50;
	private List<Integer> numbers = Arrays.asList(10, 25, 50, 75, 100, 200);
	
	public PagedEntityListPanel(String id, final DataView<? extends OmEntity> dataView) {
		super(id);
		
		dataView.setItemsPerPage(entityesPerPage);
		final AjaxPagingNavigator navigator = new AjaxPagingNavigator("navigator", dataView);
		add(navigator.setOutputMarkupId(true));
		
		add(new DropDownChoice<Integer>("entityesPerPage", new PropertyModel<Integer>(this, "entityesPerPage"), numbers)
			.add(new AjaxEventBehavior("change") {
				private static final long serialVersionUID = 7721662282201431562L;

				@Override
				protected void onEvent(AjaxRequestTarget target) {
					dataView.setItemsPerPage(entityesPerPage);
					target.add(navigator);
					PagedEntityListPanel.this.onEvent(target);
				}
			}));
	}

	protected abstract void onEvent(AjaxRequestTarget target);
}
