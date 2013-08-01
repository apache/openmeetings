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
package org.apache.openmeetings.web.util;

import static org.apache.wicket.ajax.attributes.CallbackParameter.resolved;

import org.apache.openmeetings.web.app.WebSession;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.util.string.StringValue;

public class BaseUrlAjaxBehavior extends AbstractDefaultAjaxBehavior {

	private static final long serialVersionUID = 9219184342677317070L;

	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		super.renderHead(component, response);
		response.render(JavaScriptHeaderItem.forScript(getCallbackFunctionBody(resolved("baseUrl", "window.location.href")), "getBaseUrl"));
	}
	
	@Override
	protected void respond(AjaxRequestTarget target) {
		StringValue url = getComponent().getRequestCycle().getRequest().getRequestParameters().getParameterValue("baseUrl");
		String baseUrl = url.toString(); 
		if (baseUrl.indexOf('#') > 0 || baseUrl.indexOf('?') > 0) {
			baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf('/'));
		}
		WebSession.get().setBaseUrl(baseUrl);
	}

}
