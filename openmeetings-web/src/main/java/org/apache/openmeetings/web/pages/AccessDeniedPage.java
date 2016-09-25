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
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.IRequestParameters;

import com.googlecode.wicket.jquery.ui.form.button.Button;

public class AccessDeniedPage extends BaseInitedPage {
	private static final long serialVersionUID = 1L;

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(new Form<Void>("form").add(
				new Button("home") {
					private static final long serialVersionUID = 1L;

					@Override
					public void onSubmit() {
						setResponsePage(Application.get().getHomePage());
					}
				}
				, new Button("logout") {
					private static final long serialVersionUID = 1L;

					@Override
					public boolean isVisible() {
						return WebSession.get().isSignedIn();
					}

					@Override
					public void onSubmit() {
						getSession().invalidate();
						setResponsePage(Application.get().getSignInPageClass());
					}
				})
			);
	}

	@Override
	protected void onParameterArrival(IRequestParameters requestParameters, AjaxRequestTarget target) {
	}
}
