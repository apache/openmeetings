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

import org.apache.openmeetings.db.util.FormatHelper;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.common.menu.MenuPanel;
import org.apache.openmeetings.web.pages.BasePage;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;

public abstract class BasePanel extends Panel {
	private static final long serialVersionUID = 1L;

	public BasePanel(String id) {
		super(id);
		setOutputMarkupId(true);
	}

	public BasePanel(String id, IModel<?> model) {
		super(id, model);
		setOutputMarkupId(true);
	}

	public BasePage getBasePage() {
		return (BasePage)getPage();
	}

	public MainPanel getMainPanel() {
		return findParent(MainPanel.class);
	}

	protected boolean isRtl() { //TODO unify, remove copy/paste
		return FormatHelper.isRtlLanguage(WebSession.get().getLocale().toLanguageTag());
	}

	/**
	 * Overwrite this method to execute Java code after Panel is loaded by the
	 * {@link MenuPanel}
	 * 
	 * @param target
	 */
	public BasePanel onMenuPanelLoad(IPartialPageRequestHandler handler) {
		handler.add(getBasePage().getHeader().setVisible(true), getMainPanel().getMenu().setVisible(true)
				, getMainPanel().getTopLinks().setVisible(true));
		return this;
	}

	/**
	 * This method should be overridden to perform necessary cleanup: remove timers etc.
	 * 
	 * @param handler
	 */
	public void cleanup(IPartialPageRequestHandler handler) {
	}
	
	/**
	 * This method should be overridden to perform after "new message" dialog was closed.
	 * 
	 * @param handler
	 */
	public void onNewMessageClose(IPartialPageRequestHandler handler) {
	}
}
