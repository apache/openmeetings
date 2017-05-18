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
package org.apache.openmeetings.web.pages.auth;

import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.markup.html.basic.Label;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.ui.widget.dialog.AbstractDialog;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;

public class KickMessageDialog extends AbstractDialog<String> {
	private static final long serialVersionUID = 1L;

	public KickMessageDialog(String id) {
		super(id, "");
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(new Label("message", getString("606")));
	};

	@Override
	public void onConfigure(JQueryBehavior behavior) {
		super.onConfigure(behavior);
		behavior.setOption("autoOpen", true);
		behavior.setOption("closeOnEscape", false);
		behavior.setOption("classes", "{'ui-dialog-titlebar': 'ui-corner-all no-close'}");
		behavior.setOption("resizable", false);
	}

	@Override
	public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
		WebSession.setKickedByAdmin(false);
		Application.get().restartResponseAtSignInPage();
	}

}
