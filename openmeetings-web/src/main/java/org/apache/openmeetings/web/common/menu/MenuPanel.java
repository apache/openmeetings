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

import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.panel.Panel;

import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.INavbarComponent;
import de.agilecoders.wicket.core.markup.html.bootstrap.navbar.Navbar;

/**
 * Loads the menu items into the main area
 *
 * @author sebawagner
 *
 */
public class MenuPanel extends Panel {
	private static final long serialVersionUID = 1L;
	private final Navbar navbar = new Navbar("menu");

	public MenuPanel(String id, List<INavbarComponent> menus) {
		super(id);
		setOutputMarkupPlaceholderTag(true);
		setMarkupId(id);
		navbar.addComponents(menus);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(navbar);
	}

	public void update(IPartialPageRequestHandler target) {
		target.add(this);
	}
}
