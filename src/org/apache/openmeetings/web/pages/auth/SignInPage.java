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

import org.apache.openmeetings.web.pages.BasePage;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public class SignInPage extends BasePage {
	private static final long serialVersionUID = -3843571657066167592L;
	private SignInDialog d;
	
	public SignInPage(PageParameters p) {
		this();
	}
	
	public SignInPage() {
		d = new SignInDialog("signin");
		add(d);
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		//TODO need to be removed if autoOen will be enabled
		response.render(OnDomReadyHeaderItem.forScript("$('#" + d.getMarkupId() + "').dialog('open');"));
	}
}
