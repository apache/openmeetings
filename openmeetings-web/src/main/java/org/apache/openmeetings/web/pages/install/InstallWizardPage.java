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
package org.apache.openmeetings.web.pages.install;

import org.apache.openmeetings.web.app.Application;
import org.apache.openmeetings.web.pages.BaseNotInitedPage;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.ajax.AjaxClientInfoBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.protocol.http.request.WebClientInfo;

public class InstallWizardPage extends BaseNotInitedPage {
	private static final long serialVersionUID = 1L;

	public InstallWizardPage() {
		if (Application.isInstalled()) {
			throw new RestartResponseException(Application.get().getHomePage());
		}
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		final InstallWizard wizard = new InstallWizard("wizard");
		add(wizard.setEnabled(false));
		// This code is required to detect time zone offset
		add(new AjaxClientInfoBehavior() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onClientInfo(AjaxRequestTarget target, WebClientInfo clientInfo) {
				super.onClientInfo(target, clientInfo);
				wizard.initTzDropDown();
				target.add(wizard.setEnabled(true));
			}
		});
	}
}
