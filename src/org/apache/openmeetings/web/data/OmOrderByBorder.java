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

import org.apache.openmeetings.persistence.beans.OmEntity;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.sort.AjaxFallbackOrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByLink.CssProvider;

public class OmOrderByBorder<T extends OmEntity> extends AjaxFallbackOrderByBorder<String> {
	private static final long serialVersionUID = -867341109912297431L;
	private DataViewContainer<T> container;
	
	public OmOrderByBorder(final String id, final String property, DataViewContainer<T> container) {
		super(id, property, container.view.getDataProvider(), new OmCssProvider());
		this.container = container;
		setOutputMarkupId(true);
	}
	
    protected void onSortChanged() {
    	container.view.setCurrentPage(0);
    }

	@Override
	protected void onAjaxClick(AjaxRequestTarget target) {
		target.add(container.container);
		target.add(container.orderLinks);
	}
	
	static class OmCssProvider extends CssProvider<String> {
		private static final long serialVersionUID = 60178231250586887L;

		public OmCssProvider() {
			super("ui-icon ui-icon-carat-1-n sort-icon", "ui-icon ui-icon-carat-1-s sort-icon", "ui-icon ui-icon-carat-2-n-s sort-icon");
		}
	}
}
