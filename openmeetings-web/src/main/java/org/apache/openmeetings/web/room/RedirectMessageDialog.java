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

import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.util.NonClosableMessageDialog;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.util.string.Strings;

import com.googlecode.wicket.jquery.core.JQueryBehavior;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogButton;
import com.googlecode.wicket.jquery.ui.widget.dialog.DialogIcon;

public class RedirectMessageDialog extends NonClosableMessageDialog {
	private static final long serialVersionUID = 1L;
	private static final int DELAY = 5;
	private final String labelId;
	private final String url;
	private final boolean autoOpen;
	private Component label;

	public RedirectMessageDialog(String id, String labelId, boolean autoOpen, String url) {
		super(id, "", "", new ArrayList<DialogButton>(), DialogIcon.ERROR);
		this.labelId = labelId;
		this.url = url;
		this.autoOpen = autoOpen;
	}

	@Override
	protected void onInitialize() {
		getTitle().setObject(getString("204"));
		super.onInitialize();
		if (autoOpen) {
			startTimer(null);
		}
	}

	private void startTimer(IPartialPageRequestHandler handler) {
		label.add(new OmRedirectTimerBehavior(DELAY, labelId) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onFinish(AjaxRequestTarget target) {
				if (Strings.isEmpty(url)) {
					throw new RestartResponseException(Application.get().getHomePage());
				} else {
					throw new RedirectToUrlException(url);
				}
			}
		});
		if (handler != null) {
			handler.add(label);
		}
	}

	@Override
	protected void onOpen(IPartialPageRequestHandler handler) {
		super.onOpen(handler);
		startTimer(handler);
	}

	@Override
	public void onConfigure(JQueryBehavior behavior) {
		super.onConfigure(behavior);
		behavior.setOption("autoOpen", autoOpen);
		behavior.setOption("resizable", false);
	}

	@Override
	public boolean isModal() {
		return true;
	}

	@Override
	public boolean isDefaultCloseEventEnabled() {
		return false;
	}

	@Override
	public void onClose(IPartialPageRequestHandler handler, DialogButton button) {
		//no-op
	}

	@Override
	protected Component newLabel(String id, IModel<String> model) {
		label = super.newLabel(id, model);
		return label;
	}
}
