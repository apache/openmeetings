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
package org.apache.openmeetings.web.room;

import java.util.ArrayList;
import java.util.List;

import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.BasePanel;
import org.apache.openmeetings.web.common.menu.MenuItem;
import org.apache.openmeetings.web.common.menu.MenuPanel;
import org.apache.openmeetings.web.common.menu.RoomMenuItem;
import org.apache.wicket.ajax.AjaxRequestTarget;

public class RoomPanel extends BasePanel {
	private static final long serialVersionUID = 1L;

	public RoomPanel(String id, long roomId) {
		super(id);
		add(new MenuPanel("menu", getMenu()));
	}

	private List<MenuItem> getMenu() {
		//TODO hide/show
		List<MenuItem> menu = new ArrayList<MenuItem>();
		menu.add(new RoomMenuItem(WebSession.getString(308), WebSession.getString(309), "room menu exit"));
		MenuItem files = new RoomMenuItem(WebSession.getString(245));
		List<RoomMenuItem> fileItems = new ArrayList<RoomMenuItem>();
		fileItems.add(new RoomMenuItem(WebSession.getString(15)));
		files.setChildren(fileItems);
		menu.add(files);
		
		MenuItem actions = new RoomMenuItem(WebSession.getString(635));
		List<RoomMenuItem> actionItems = new ArrayList<RoomMenuItem>();
		actionItems.add(new RoomMenuItem(WebSession.getString(213)));
		actionItems.add(new RoomMenuItem(WebSession.getString(239)));
		actionItems.add(new RoomMenuItem(WebSession.getString(784)));
		actionItems.add(new RoomMenuItem(WebSession.getString(785)));
		actionItems.add(new RoomMenuItem(WebSession.getString(786)));
		actionItems.add(new RoomMenuItem(WebSession.getString(24)));
		actionItems.add(new RoomMenuItem(WebSession.getString(37)));
		actionItems.add(new RoomMenuItem(WebSession.getString(42)));
		actionItems.add(new RoomMenuItem(WebSession.getString(1447)));
		actionItems.add(new RoomMenuItem(WebSession.getString(1126)));
		actions.setChildren(actionItems);
		menu.add(actions);
		return menu;
	}
	
	@Override
	public void onMenuPanelLoad(AjaxRequestTarget target) {
		target.add(getMainPage().getHeader().setVisible(false), getMainPage().getMenu().setVisible(false)
				, getMainPage().getTopLinks().setVisible(false));
	}
	
	@Override
	public void cleanup(AjaxRequestTarget target) {
		target.add(getMainPage().getHeader().setVisible(true), getMainPage().getMenu().setVisible(true)
				, getMainPage().getTopLinks().setVisible(true));
	}
}
