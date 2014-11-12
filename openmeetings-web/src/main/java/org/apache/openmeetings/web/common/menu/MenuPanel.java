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
package org.apache.openmeetings.web.common.menu;

import java.util.List;

import org.apache.openmeetings.db.entity.label.FieldLanguage;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.BasePanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.util.string.Strings;

/**
 * Loads the menu items into the main area
 * 
 * @author sebawagner
 *
 */
public class MenuPanel extends BasePanel {
	private static final long serialVersionUID = 1L;
	private final WebMarkupContainer menuContainer = new WebMarkupContainer("menuContainer");

	public MenuPanel(String id, List<MenuItem> menus) {
		super(id);
		setOutputMarkupPlaceholderTag(true);
		setMarkupId(id);
		
		add(menuContainer.setOutputMarkupId(true));
		menuContainer.add(new ListView<MenuItem>("mainItem", menus) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void populateItem(final ListItem<MenuItem> item) {
				final MenuItem gl = item.getModelObject();
				AjaxLink<Void> link = new AjaxLink<Void>("link") {
					private static final long serialVersionUID = 1L;

					public void onClick(AjaxRequestTarget target) {
						gl.onClick(getMainPage(), target);
					};
				};
				if (null != gl.getChildren()) {
					link.add(new AttributeAppender("onclick", "return false;"));
				}
				item.add(link.add(new Label("label", gl.getName()).setRenderBodyOnly(true)));
				if (!Strings.isEmpty(gl.getCssClass())) {
					link.add(AttributeAppender.append("class", gl.getCssClass()));
				}
				if (!Strings.isEmpty(gl.getDesc())) {
					link.add(AttributeAppender.append("title", gl.getDesc()));
				}
				item.setVisible(gl.isActive());
				item.add(new WebMarkupContainer("childItems").add(new ListView<MenuItem>("childItem", gl.getChildren()) {
						private static final long serialVersionUID = 1L;
	
						@Override
						protected void populateItem(final ListItem<MenuItem> item1) {
							final MenuItem m = item1.getModelObject();
							item1.setVisible(m.isActive());
							item1.add(new AjaxLink<Void>("link") {
								private static final long serialVersionUID = 1L;
								{
									add(new Label("name", m.getName()));
									add(new Label("description", m.getDesc()));
								}
								
								public void onClick(AjaxRequestTarget target) {
									m.onClick(getMainPage(), target);
								}
							});
						}
					}).setVisible(null != gl.getChildren()));
			}
		});
		add(new MenuFunctionsBehavior(menuContainer.getMarkupId(), id));
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		FieldLanguage lang = WebSession.getLanguageObj();
		if (lang.isRtl()) {
			response.render(CssHeaderItem.forUrl("css/jquery.ui.menubar-rtl.css"));
		}
	}
}
