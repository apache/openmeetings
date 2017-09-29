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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.db.entity.IDataProviderEntity;
import org.apache.openmeetings.web.admin.SearchableDataView;
import org.apache.openmeetings.web.common.PagedEntityListPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;

public class DataViewContainer<T extends IDataProviderEntity> implements Serializable {
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer container;
	private final SearchableDataView<T> view;
	private final PagedEntityListPanel navigator;
	private List<OmOrderByBorder<T>> orderLinks = new ArrayList<>();

	public DataViewContainer(WebMarkupContainer container, SearchableDataView<T> view, PagedEntityListPanel navigator) {
		this.container = container;
		this.view = view;
		this.navigator = navigator;
		navigator.setOutputMarkupId(true);
	}

	public DataViewContainer<T> addLink(OmOrderByBorder<T> link) {
		orderLinks.add(link);
		return this;
	}

	public OmOrderByBorder<T>[] getLinks() {
		@SuppressWarnings("unchecked")
		OmOrderByBorder<T>[] a = new OmOrderByBorder[0];
		return orderLinks.toArray(a);
	}

	public WebMarkupContainer getContainer() {
		return container;
	}

	public SearchableDataView<T> getView() {
		return view;
	}

	public PagedEntityListPanel getNavigator() {
		return navigator;
	}
}
