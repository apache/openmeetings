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

import org.apache.openmeetings.web.common.BasePanel;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.util.string.Strings;

import com.googlecode.wicket.jquery.core.Options;
import com.googlecode.wicket.jquery.ui.widget.menu.IMenuItem;
import com.googlecode.wicket.jquery.ui.widget.menu.Menu;

/**
 * Loads the menu items into the main area
 * 
 * @author sebawagner
 *
 */
public class MenuPanel extends BasePanel {
	private static final long serialVersionUID = 1L;

	public MenuPanel(String id, List<IMenuItem> menus) {
		super(id);
		setOutputMarkupPlaceholderTag(true);
		setMarkupId(id);
		add(new Menu("menu", menus, new Options().set("icons", "{ submenu: 'ui-icon-triangle-1-s' }")
					.set("position", "{ my: 'left top', at: 'left bottom'}"))
		{
			private static final long serialVersionUID = 1L;

			@Override
			protected void addMenuItem(ListItem<IMenuItem> item, IMenuItem menuItem) {
				super.addMenuItem(item, menuItem);
				MenuItem m = (MenuItem)menuItem;
				if (Strings.isEmpty(m.getDesc())) {
					item.add(AttributeAppender.append("class", "top"));
				} else {
					item.add(AttributeAppender.append("class", "sub"));
					item.add(AttributeAppender.append("title", m.getDesc()));
				}
				if (!Strings.isEmpty(m.getCssClass())) {
					item.add(AttributeAppender.append("class", m.getCssClass()));
				}
			}
		});
	}

	public void update(IPartialPageRequestHandler target) {
		target.add(this);
	}
}
