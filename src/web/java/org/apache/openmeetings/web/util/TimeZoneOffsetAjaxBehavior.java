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

public class TimeZoneOffsetAjaxBehavior extends AbstractDefaultAjaxBehavior {
	private static final long serialVersionUID = 5857580283353735353L;

	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		super.renderHead(component, response);
		if (WebSession.get().getBrowserTimeZoneOffset() == Integer.MIN_VALUE) {
			response.render(JavaScriptHeaderItem.forScript(getCallbackFunctionBody(resolved("tzOffset", "getTimeZoneOffsetMinutes()")), "getTzOffset"));
		}
	}
	
	@Override
	protected void respond(AjaxRequestTarget target) {
		StringValue offset = getComponent().getRequestCycle().getRequest().getRequestParameters().getParameterValue("tzOffset");
		try {
			WebSession.get().setBrowserTimeZoneOffset(offset.toInteger());
			customFunction();
		} catch (NumberFormatException ex) { }
	}
	
	protected void customFunction(){		
	}

}
