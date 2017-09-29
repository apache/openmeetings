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
package org.apache.openmeetings.web.data;

import org.apache.openmeetings.db.entity.IDataProviderEntity;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.sort.AjaxFallbackOrderByBorder;

public class OmOrderByBorder<T extends IDataProviderEntity> extends AjaxFallbackOrderByBorder<String> {
	private static final long serialVersionUID = 1L;
	private DataViewContainer<T> container;

	public OmOrderByBorder(final String id, final String property, DataViewContainer<T> container) {
		super(id, property, container.getView().getDataProvider());
		this.container = container;
		setOutputMarkupId(true);
	}

	@Override
	protected void onSortChanged() {
		container.getView().setCurrentPage(0);
	}

	@Override
	protected void onAjaxClick(AjaxRequestTarget target) {
		target.add(container.getContainer(), container.getNavigator());
		target.add(container.getLinks());
	}
}
