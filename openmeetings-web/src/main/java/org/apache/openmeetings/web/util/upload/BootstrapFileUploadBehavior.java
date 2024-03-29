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
package org.apache.openmeetings.web.util.upload;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.ResourceReference;

import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;

public class BootstrapFileUploadBehavior extends Behavior {
	private static final long serialVersionUID = 1L;
	private static final ResourceReference BOOTSTRAP_FILEINPUT_JS_REFERENCE
			= new WebjarsJavaScriptResourceReference("/jasny-bootstrap/current/js/jasny-bootstrap.js");
	private static final ResourceReference BOOTSTRAP_FILEINPUT_CSS_REFERENCE
			= new WebjarsCssResourceReference("/jasny-bootstrap/current/css/jasny-bootstrap.css");

	private static class Holder {
		private static final BootstrapFileUploadBehavior INSTANCE = new BootstrapFileUploadBehavior();
	}

	public static BootstrapFileUploadBehavior getInstance() {
		return Holder.INSTANCE;
	}

	private BootstrapFileUploadBehavior() {
	}

	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		super.renderHead(component, response);
		response.render(JavaScriptHeaderItem.forReference(BOOTSTRAP_FILEINPUT_JS_REFERENCE));
		response.render(CssHeaderItem.forReference(BOOTSTRAP_FILEINPUT_CSS_REFERENCE));
	}
}
