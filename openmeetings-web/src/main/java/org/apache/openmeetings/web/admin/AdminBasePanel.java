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
package org.apache.openmeetings.web.admin;

import org.apache.openmeetings.web.common.BasePanel;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.PriorityHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

@AuthorizeInstantiation("Admin")
public abstract class AdminBasePanel extends BasePanel {
	private static final long serialVersionUID = 1L;
	private static final JavaScriptResourceReference ADMIN_SCRIPT = new JavaScriptResourceReference(AdminBasePanel.class, "admin.js");

	public AdminBasePanel(String id) {
		super(id);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(new PriorityHeaderItem(JavaScriptHeaderItem.forReference(ADMIN_SCRIPT)));
	}

	@Override
	public BasePanel onMenuPanelLoad(IPartialPageRequestHandler handler) {
		super.onMenuPanelLoad(handler);
		reinitJs(handler);
		return this;
	}

	protected StringBuilder getRowClass(Long id, Long selectedId) {
		StringBuilder sb = new StringBuilder(ROW_CLASS);
		if (id != null && id.equals(selectedId)) {
			sb.append(" ui-state-default");
		}
		return sb;
	}

	public static void reinitJs(IPartialPageRequestHandler handler) {
		handler.appendJavaScript("adminPanelInit();");
	}
}
