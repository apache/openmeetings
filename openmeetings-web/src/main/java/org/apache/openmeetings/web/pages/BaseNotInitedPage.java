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
import org.apache.wicket.request.IRequestParameters;

public abstract class BaseNotInitedPage extends BasePage {
	private static final long serialVersionUID = 1L;

	@Override
	protected String getLanguageCode() {
		return WebSession.get().getLocale().getCountry();
	}

	@Override
	public boolean isRtl() {
		return Application.isInstalled() && WebSession.get().isRtlLocale();
	}

	@Override
	protected void onParameterArrival(IRequestParameters arg0, AjaxRequestTarget arg1) {
	}
}
