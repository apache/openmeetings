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

import org.apache.openmeetings.web.app.Application;
import org.apache.wicket.Component;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.util.string.Strings;

public class RedirectMessageDialog extends IconTextModal {
	private static final long serialVersionUID = 1L;
	private static final int DELAY = 5;
	private final String labelId;
	private final String url;
	private final boolean autoOpen;

	public RedirectMessageDialog(String id, String labelId, boolean autoOpen, String url) {
		super(id);
		this.labelId = labelId;
		this.url = url;
		this.autoOpen = autoOpen;
	}

	@Override
	protected void onInitialize() {
		header(new ResourceModel("204"));
		setCloseOnEscapeKey(false);
		show(autoOpen);
		withLabel(new ResourceModel(labelId));
		getLabel().setOutputMarkupId(true);
		withErrorIcon();
		super.onInitialize();
		if (autoOpen) {
			startTimer(null);
		}
	}

	private void startTimer(IPartialPageRequestHandler handler) {
		getLabel().add(new OmTimerBehavior(DELAY, labelId) {
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
			handler.add(getLabel());
		}
	}

	@Override
	public RedirectMessageDialog show(IPartialPageRequestHandler handler) {
		super.show(handler);
		startTimer(handler);
		return this;
	}

	@Override
	protected Component createHeaderCloseButton(String id) {
		return super.createHeaderCloseButton(id).setVisible(false);
	}
}
