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
package org.apache.openmeetings.web.room.wb;

import static org.apache.wicket.AttributeModifier.append;

import java.util.ArrayList;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.googlecode.wicket.jquery.ui.widget.tabs.TabbedPanel;

public class WbTabbedPanel extends TabbedPanel {
	private static final long serialVersionUID = 1L;
	private final WbPanel wb;

	public WbTabbedPanel(String id, WbPanel wb) {
		super(id, new ArrayList<>());
		setOutputMarkupId(true);
		this.wb = wb;
	}

	@Override
	protected void onConfigure() {
		super.onConfigure();
		//setActiveTab(selectedIdx);
	}

	@Override
	public void onActivate(AjaxRequestTarget target, int index, ITab tab) {
		//selectedIdx = index;
	}

	@Override
	protected WebMarkupContainer newTabContainer(String id, String tabId, ITab tab, int index) {
		WebMarkupContainer t = new Fragment(id, "tab-fragment", WbTabbedPanel.this);
		final Component link = wb.isReadOnly()
				? newTitleLabel("link", tab.getTitle())
				: new AjaxEditableLabel<String>("link", tab.getTitle()) {
						private static final long serialVersionUID = 1L;

						@Override
						protected String getLabelAjaxEvent() {
							return "dblclick";
						}

						@Override
						protected void onSubmit(AjaxRequestTarget target) {
							super.onSubmit(target);
						}
				};
		link.add(append("href", "#" + tabId), append("class", "ui-tabs-anchor"), append("title", tab.getTitle()));

		WebMarkupContainer close = new WebMarkupContainer("close");
		t.add(link, close.setVisible(!wb.isReadOnly()));
		return t;
	}

	public void addWb(final String name) {
		add(new ITab() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isVisible() {
				return true;
			}

			@Override
			public IModel<String> getTitle() {
				return Model.of(name);
			}

			@Override
			public WebMarkupContainer getPanel(String containerId) {
				return new WbArea(containerId);
			}
		});
	}
}
