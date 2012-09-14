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
package org.apache.openmeetings.web.pages;

import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.openmeetings.web.components.ConfirmableAjaxLink;
import org.apache.openmeetings.web.components.MenuPanel;
import org.apache.openmeetings.web.components.user.ChatPanel;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;

@AuthorizeInstantiation("USER")
public class MainPage extends BasePage {
	private static final long serialVersionUID = 6421960759218157999L;
	private final MenuPanel menu;
	
	public MainPage() {
		MarkupContainer contents = new WebMarkupContainer("contents");
		contents.add(new WebMarkupContainer("child")).setOutputMarkupId(true);
		add(contents);
		menu = new MenuPanel("menu", contents);
		add(menu);
		add(new ConfirmableAjaxLink("logout", WebSession.getString(634L)) {
			private static final long serialVersionUID = -2994610981053570537L;

			@Override
			public void onClick(AjaxRequestTarget target) {
				getSession().invalidate();
				setResponsePage(Application.get().getSignInPageClass());
			}
		});
		add(new ChatPanel("chat"));
	}
}
